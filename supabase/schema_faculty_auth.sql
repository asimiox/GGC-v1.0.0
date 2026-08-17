-- ==============================================================================
-- GGC M.B.Din Official Android App - Teacher/Faculty Authentication Schema
-- ==============================================================================
-- Run this SQL in your Supabase Project SQL Editor (https://supabase.com/dashboard/project/mhiudbdnrooipovvonfb/sql)

-- 1. Official Faculty Registry Table (College Administration Approved List)
CREATE TABLE IF NOT EXISTS public.official_faculty (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    faculty_id TEXT NOT NULL,
    full_name TEXT NOT NULL,
    department TEXT NOT NULL,
    designation TEXT NOT NULL,
    qualification TEXT NOT NULL,
    institutional_email TEXT,
    is_claimed BOOLEAN DEFAULT FALSE NOT NULL,
    claimed_by_user_id UUID UNIQUE REFERENCES auth.users(id) ON DELETE SET NULL,
    claimed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    CONSTRAINT uq_official_faculty_id UNIQUE (faculty_id)
);

-- Case-insensitive unique indexes for Faculty Identifier & Institutional Email
CREATE UNIQUE INDEX IF NOT EXISTS idx_official_faculty_id_lower ON public.official_faculty (LOWER(TRIM(faculty_id)));
CREATE UNIQUE INDEX IF NOT EXISTS idx_official_faculty_email_lower ON public.official_faculty (LOWER(TRIM(institutional_email))) WHERE institutional_email IS NOT NULL;

-- 2. Verified Faculty Profiles Table (Linked to Supabase Auth User)
CREATE TABLE IF NOT EXISTS public.faculty_profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    username TEXT NOT NULL,
    faculty_id TEXT NOT NULL,
    full_name TEXT NOT NULL,
    department TEXT NOT NULL,
    designation TEXT NOT NULL,
    qualification TEXT NOT NULL,
    institutional_email TEXT,
    phone_number TEXT,
    official_record_id UUID NOT NULL REFERENCES public.official_faculty(id) ON DELETE RESTRICT,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    CONSTRAINT uq_faculty_profile_username UNIQUE (username),
    CONSTRAINT uq_faculty_profile_faculty_id UNIQUE (faculty_id),
    CONSTRAINT uq_faculty_profile_official_id UNIQUE (official_record_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_faculty_profile_username_lower ON public.faculty_profiles (LOWER(TRIM(username)));
CREATE UNIQUE INDEX IF NOT EXISTS idx_faculty_profile_id_lower ON public.faculty_profiles (LOWER(TRIM(faculty_id)));
CREATE UNIQUE INDEX IF NOT EXISTS idx_faculty_profile_email_lower ON public.faculty_profiles (LOWER(TRIM(institutional_email))) WHERE institutional_email IS NOT NULL;

-- 3. Enable Row Level Security (RLS)
ALTER TABLE public.official_faculty ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.faculty_profiles ENABLE ROW LEVEL SECURITY;

-- 4. RLS Policies
DROP POLICY IF EXISTS "Allow public verification of official faculty records" ON public.official_faculty;
CREATE POLICY "Allow public verification of official faculty records" 
ON public.official_faculty
FOR SELECT
USING (true);

DROP POLICY IF EXISTS "Allow users to read own faculty profile" ON public.faculty_profiles;
CREATE POLICY "Allow users to read own faculty profile"
ON public.faculty_profiles
FOR SELECT
USING (auth.uid() = id);

DROP POLICY IF EXISTS "Allow authenticated user to insert own faculty profile" ON public.faculty_profiles;
CREATE POLICY "Allow authenticated user to insert own faculty profile"
ON public.faculty_profiles
FOR INSERT
WITH CHECK (auth.uid() = id);

DROP POLICY IF EXISTS "Allow authenticated user to update own faculty profile" ON public.faculty_profiles;
CREATE POLICY "Allow authenticated user to update own faculty profile"
ON public.faculty_profiles
FOR UPDATE
USING (auth.uid() = id)
WITH CHECK (auth.uid() = id);

-- 5. Pre-check Eligibility Function for Teacher / Faculty Members
CREATE OR REPLACE FUNCTION public.check_faculty_eligibility(
    p_faculty_id TEXT,
    p_department TEXT,
    p_username TEXT,
    p_institutional_email TEXT DEFAULT NULL
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
    v_clean_faculty_id TEXT := UPPER(TRIM(p_faculty_id));
    v_clean_department TEXT := TRIM(p_department);
    v_clean_username TEXT := LOWER(TRIM(p_username));
    v_clean_email TEXT := LOWER(TRIM(COALESCE(p_institutional_email, '')));
    v_record RECORD;
BEGIN
    -- 1. Check username uniqueness across faculty profiles
    IF EXISTS (
        SELECT 1 FROM public.faculty_profiles 
        WHERE LOWER(TRIM(username)) = v_clean_username
    ) THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'Username "' || p_username || '" is already taken by another user.');
    END IF;

    -- 2. Check if faculty identifier is already registered in faculty profiles
    IF EXISTS (
        SELECT 1 FROM public.faculty_profiles 
        WHERE UPPER(TRIM(faculty_id)) = v_clean_faculty_id
    ) THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'Faculty ID "' || p_faculty_id || '" is already linked to an existing faculty account.');
    END IF;

    -- 3. Check if institutional email is already registered in faculty profiles
    IF v_clean_email <> '' AND EXISTS (
        SELECT 1 FROM public.faculty_profiles 
        WHERE LOWER(TRIM(institutional_email)) = v_clean_email
    ) THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'Institutional email "' || p_institutional_email || '" is already registered.');
    END IF;

    -- 4. Find official faculty record in college registry
    SELECT * INTO v_record
    FROM public.official_faculty
    WHERE UPPER(TRIM(faculty_id)) = v_clean_faculty_id;

    IF NOT FOUND THEN
        RETURN jsonb_build_object(
            'eligible', false, 
            'error', 'No official faculty record found matching Faculty ID "' || p_faculty_id || '". Please verify with Principal / College Administration Office.'
        );
    END IF;

    -- 5. Check department match (case-insensitive)
    IF v_clean_department <> '' AND LOWER(TRIM(v_record.department)) <> LOWER(v_clean_department) THEN
        RETURN jsonb_build_object(
            'eligible', false, 
            'error', 'Selected Department (' || p_department || ') does not match official faculty department (' || v_record.department || ').'
        );
    END IF;

    -- 6. Check if already claimed
    IF v_record.is_claimed = TRUE OR v_record.claimed_by_user_id IS NOT NULL THEN
        RETURN jsonb_build_object(
            'eligible', false, 
            'error', 'This official faculty record (' || v_record.full_name || ', ' || v_record.faculty_id || ') has already been registered and claimed.'
        );
    END IF;

    RETURN jsonb_build_object(
        'eligible', true,
        'full_name', v_record.full_name,
        'department', v_record.department,
        'designation', v_record.designation,
        'qualification', v_record.qualification,
        'institutional_email', v_record.institutional_email
    );
END;
$$;

-- 6. Atomic Stored Procedure / RPC for Race-Free Faculty Signup with Row Locking
CREATE OR REPLACE FUNCTION public.register_faculty_account(
    p_user_id UUID,
    p_faculty_id TEXT,
    p_full_name TEXT,
    p_department TEXT,
    p_username TEXT,
    p_institutional_email TEXT DEFAULT NULL,
    p_phone TEXT DEFAULT NULL
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
    v_clean_faculty_id TEXT;
    v_clean_department TEXT;
    v_clean_username TEXT;
    v_clean_email TEXT;
    v_clean_phone TEXT;
    v_record RECORD;
BEGIN
    -- 1. Sanitize & Normalize Inputs
    v_clean_faculty_id := UPPER(TRIM(p_faculty_id));
    v_clean_department := TRIM(p_department);
    v_clean_username := LOWER(TRIM(p_username));
    v_clean_email := LOWER(TRIM(COALESCE(p_institutional_email, '')));
    v_clean_phone := TRIM(COALESCE(p_phone, ''));

    IF v_clean_faculty_id = '' OR v_clean_username = '' THEN
        RETURN jsonb_build_object('success', false, 'error', 'Faculty ID and Username are required.');
    END IF;

    -- 2. Check Username uniqueness in profiles
    IF EXISTS (
        SELECT 1 FROM public.faculty_profiles 
        WHERE LOWER(TRIM(username)) = v_clean_username
    ) THEN
        RETURN jsonb_build_object('success', false, 'error', 'The username "' || p_username || '" is already taken. Please choose another username.');
    END IF;

    -- 3. Check Faculty ID uniqueness in profiles
    IF EXISTS (
        SELECT 1 FROM public.faculty_profiles 
        WHERE UPPER(TRIM(faculty_id)) = v_clean_faculty_id
    ) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Faculty ID "' || p_faculty_id || '" is already registered to an account.');
    END IF;

    -- 4. Check Institutional Email uniqueness in profiles if provided
    IF v_clean_email <> '' AND EXISTS (
        SELECT 1 FROM public.faculty_profiles 
        WHERE LOWER(TRIM(institutional_email)) = v_clean_email
    ) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Institutional email "' || p_institutional_email || '" is already registered.');
    END IF;

    -- 5. Lock and verify Official Faculty Record from Administration Registry (FOR UPDATE prevents race conditions)
    SELECT * INTO v_record
    FROM public.official_faculty
    WHERE UPPER(TRIM(faculty_id)) = v_clean_faculty_id
    FOR UPDATE;

    IF NOT FOUND THEN
        RETURN jsonb_build_object(
            'success', false, 
            'error', 'No official faculty record found matching Faculty ID "' || p_faculty_id || '". Please verify with College Administration.'
        );
    END IF;

    -- 6. Verify Department match
    IF v_clean_department <> '' AND LOWER(TRIM(v_record.department)) <> LOWER(v_clean_department) THEN
        RETURN jsonb_build_object(
            'success', false, 
            'error', 'Selected Department (' || p_department || ') does not match official faculty department (' || v_record.department || ').'
        );
    END IF;

    -- 7. Check if Official Record is already claimed
    IF v_record.is_claimed = TRUE OR v_record.claimed_by_user_id IS NOT NULL THEN
        RETURN jsonb_build_object(
            'success', false, 
            'error', 'This official faculty record has already been claimed by a registered account.'
        );
    END IF;

    -- 8. Insert into faculty_profiles
    INSERT INTO public.faculty_profiles (
        id,
        username,
        faculty_id,
        full_name,
        department,
        designation,
        qualification,
        institutional_email,
        phone_number,
        official_record_id,
        created_at
    ) VALUES (
        p_user_id,
        v_clean_username,
        v_clean_faculty_id,
        v_record.full_name,
        v_record.department,
        v_record.designation,
        v_record.qualification,
        NULLIF(v_clean_email, ''),
        NULLIF(v_clean_phone, ''),
        v_record.id,
        NOW()
    );

    -- 9. Mark Official Faculty Record as Claimed atomically
    UPDATE public.official_faculty
    SET is_claimed = TRUE,
        claimed_by_user_id = p_user_id,
        claimed_at = NOW()
    WHERE id = v_record.id;

    RETURN jsonb_build_object(
        'success', true, 
        'message', 'Faculty account registered and verified successfully.',
        'profile', jsonb_build_object(
            'id', p_user_id,
            'username', v_clean_username,
            'faculty_id', v_clean_faculty_id,
            'full_name', v_record.full_name,
            'department', v_record.department,
            'designation', v_record.designation,
            'qualification', v_record.qualification,
            'institutional_email', v_clean_email
        )
    );
EXCEPTION
    WHEN unique_violation THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unique constraint violation: A faculty profile with this Faculty ID, Email, or Username already exists.');
    WHEN OTHERS THEN
        RETURN jsonb_build_object('success', false, 'error', 'Database error: ' || SQLERRM);
END;
$$;

-- 7. Seed Official Faculty Registry with all 41 approved College Faculty Members
INSERT INTO public.official_faculty (faculty_id, full_name, designation, qualification, department, institutional_email)
VALUES
    ('FAC-01', 'Amir Ahmed', 'Principal', 'MSc-Botany', 'Botany', 'principal@ggcmbdin.edu.pk'),
    ('FAC-02', 'Dr. Abdul Manan', 'Vice Principal - Associate Professor - HOD Mathematics', 'PhD in Mathematics', 'Mathematics', 'math.hod@ggcmbdin.edu.pk'),
    ('FAC-03', 'Muhammad Faiyaz', 'Assistant Professor - HOD Information Technology', 'M-Phil Computer Science', 'Information Technology', 'cs.hod@ggcmbdin.edu.pk'),
    ('FAC-04', 'Muhammad Ikram Bhatti', 'Assistant Professor - HOD English', 'M.A English', 'English', 'english.hod@ggcmbdin.edu.pk'),
    ('FAC-05', 'Saifullah', 'Assistant Professor - HOD Islamiyat', 'M-Phil Islamiyat', 'Islamiyat', 'islamiyat.hod@ggcmbdin.edu.pk'),
    ('FAC-06', 'Muhammad Asif Zaman', 'Assistant Professor - HOD Physics', 'M.Sc Physics', 'Physics', 'physics.hod@ggcmbdin.edu.pk'),
    ('FAC-07', 'Muhammad Adnan Saghir', 'Lecturer - HOD Education', 'M-Phil', 'Education', 'education.hod@ggcmbdin.edu.pk'),
    ('FAC-08', 'Waqas Arshad', 'Lecturer - HOD Zoology', 'M-Phil Zoology', 'Zoology', 'zoology.hod@ggcmbdin.edu.pk'),
    ('FAC-09', 'Khuram Ijaz Aslam', 'Lecturer - HOD Statistics', 'M-Phil Statistics', 'Statistics', 'stats.hod@ggcmbdin.edu.pk'),
    ('FAC-10', 'Saif Ullah Warraich', 'Assistant Professor - HOD History', 'M.A History', 'History', 'history.hod@ggcmbdin.edu.pk'),
    ('FAC-11', 'Muhammad Umer Minhas', 'Assistant Professor - HOD Chemistry', 'M.Sc Chemistry', 'Chemistry', 'chemistry.hod@ggcmbdin.edu.pk'),
    ('FAC-12', 'Ansar Iqbal', 'Assistant Professor - HOD Economics', 'M.A Economics', 'Economics', 'economics.hod@ggcmbdin.edu.pk'),
    ('FAC-13', 'Afrasiab', 'Assistant Professor - HOD Political Science', 'M-Phil Political Science', 'Political Science', 'polscience.hod@ggcmbdin.edu.pk'),
    ('FAC-14', 'Muhammad Iqbal', 'Associate Professor - HOD Urdu', 'M-Phil Urdu', 'Urdu', 'urdu.hod@ggcmbdin.edu.pk'),
    ('FAC-15', 'Faisal Shahzad', 'Lecturer', 'M.Phil Urdu', 'Urdu', 'faisal.urdu@ggcmbdin.edu.pk'),
    ('FAC-16', 'Muhammad Husnain', 'Lecturer', 'M-Phil Islamic Studies', 'Islamiyat', 'husnain.is@ggcmbdin.edu.pk'),
    ('FAC-17', 'Dr. Ghulam Murtaza', 'Lecturer', 'PhD Islamic Studies', 'Islamiyat', 'murtaza.is@ggcmbdin.edu.pk'),
    ('FAC-18', 'Muhammad Shahzad', 'Lecturer', 'BS - Physics', 'Physics', 'shahzad.phy@ggcmbdin.edu.pk'),
    ('FAC-19', 'Muhammad Adnan', 'Lecturer', 'M-Phil Physics', 'Physics', 'adnan.phy@ggcmbdin.edu.pk'),
    ('FAC-20', 'Muhammad Ijaz', 'Lecturer', 'BS - English', 'English', 'ijaz.eng@ggcmbdin.edu.pk'),
    ('FAC-21', 'Tanvir Ahmad', 'Lecturer', 'M.Phil Statistics', 'Statistics', 'tanvir.stats@ggcmbdin.edu.pk'),
    ('FAC-22', 'Kamran Saeed Pracha', 'Lecturer', 'M-Phil Zoology', 'Zoology', 'kamran.zoo@ggcmbdin.edu.pk'),
    ('FAC-23', 'Ubaid Ullah', 'Lecturer', 'M.Sc Information Technology', 'Information Technology', 'ubaid.it@ggcmbdin.edu.pk'),
    ('FAC-24', 'Muhammad Sajid Mehmood', 'Lecturer', 'M-Phil English', 'English', 'sajid.eng@ggcmbdin.edu.pk'),
    ('FAC-25', 'Tariq Ashraf', 'Lecturer', 'M-Phil Business Administration', 'BBA', 'tariq.bba@ggcmbdin.edu.pk'),
    ('FAC-26', 'Asad Ali', 'Lecturer', 'BS - Political Science', 'Political Science', 'asad.pol@ggcmbdin.edu.pk'),
    ('FAC-27', 'Muhammad Faryad', 'Assistant Professor', 'M.A English', 'English', 'faryad.eng@ggcmbdin.edu.pk'),
    ('FAC-28', 'Dr. Khalid Mahmood', 'Assistant Professor', 'PhD Chemistry', 'Chemistry', 'khalid.chem@ggcmbdin.edu.pk'),
    ('FAC-29', 'Zaman Niaz', 'Assistant Professor', 'M-Phil Urdu', 'Urdu', 'zaman.urdu@ggcmbdin.edu.pk'),
    ('FAC-30', 'Naveed Akram', 'Assistant Professor', 'M-Phil Persian', 'Persian', 'naveed.persian@ggcmbdin.edu.pk'),
    ('FAC-31', 'Shahid Imran', 'Assistant Professor', 'M.Sc Mathematics', 'Mathematics', 'shahid.math@ggcmbdin.edu.pk'),
    ('FAC-32', 'Amjad Javaid Butt', 'Assistant Professor', 'M-Phil Islamic Studies', 'Islamiyat', 'amjad.is@ggcmbdin.edu.pk'),
    ('FAC-33', 'Saqib Gulzar', 'Assistant Professor', 'M-Phil Political Science', 'Political Science', 'saqib.pol@ggcmbdin.edu.pk'),
    ('FAC-34', 'Majid Bashir', 'Assistant Professor', 'M-Phil English', 'English', 'majid.eng@ggcmbdin.edu.pk'),
    ('FAC-35', 'Dr. Adil Mubeen', 'Assistant Professor', 'PhD Physics', 'Physics', 'adil.phy@ggcmbdin.edu.pk'),
    ('FAC-36', 'Muhammad Latif', 'Assistant Professor', 'M-Phil Mathematics', 'Mathematics', 'latif.math@ggcmbdin.edu.pk'),
    ('FAC-37', 'Dr. Azhar Iqbal', 'Assistant Professor', 'PhD Islamic Studies', 'Islamiyat', 'azhar.is@ggcmbdin.edu.pk'),
    ('FAC-38', 'Muhammad Attique', 'Assistant Professor', 'M-Phil Islamic Studies', 'Islamiyat', 'attique.is@ggcmbdin.edu.pk'),
    ('FAC-39', 'Mumtaz Hussain', 'Assistant Professor', 'M.Sc Mathematics', 'Mathematics', 'mumtaz.math@ggcmbdin.edu.pk'),
    ('FAC-40', 'Muhammad Mansha Khan', 'Assistant Professor', 'M.A Political Science', 'Political Science', 'mansha.pol@ggcmbdin.edu.pk'),
    ('FAC-41', 'Mujahid Ali', 'Associate Professor', 'M-Phil Persian', 'Persian', 'mujahid.persian@ggcmbdin.edu.pk')
ON CONFLICT (faculty_id) DO NOTHING;
