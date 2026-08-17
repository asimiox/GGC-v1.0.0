-- ==============================================================================
-- GGC M.B.Din Official Android App - Intermediate Student Authentication Schema
-- ==============================================================================
-- Run this SQL in your Supabase Project SQL Editor (https://supabase.com/dashboard/project/mhiudbdnrooipovvonfb/sql)

-- 1. Official Intermediate Students Table (Admin Registry)
CREATE TABLE IF NOT EXISTS public.official_intermediate_students (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    roll_number TEXT NOT NULL,
    registration_number TEXT NOT NULL,
    program TEXT NOT NULL,
    first_name TEXT,
    last_name TEXT,
    is_claimed BOOLEAN DEFAULT FALSE NOT NULL,
    claimed_by_user_id UUID UNIQUE REFERENCES auth.users(id) ON DELETE SET NULL,
    claimed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    CONSTRAINT uq_official_inter_roll_number UNIQUE (roll_number),
    CONSTRAINT uq_official_inter_reg_number UNIQUE (registration_number)
);

-- Case-insensitive unique indexes for Roll Number & Registration Number
CREATE UNIQUE INDEX IF NOT EXISTS idx_official_inter_roll_lower ON public.official_intermediate_students (LOWER(TRIM(roll_number)));
CREATE UNIQUE INDEX IF NOT EXISTS idx_official_inter_reg_lower ON public.official_intermediate_students (LOWER(TRIM(registration_number)));

-- 2. Verified Intermediate Student Profiles (Linked to Supabase Auth user)
CREATE TABLE IF NOT EXISTS public.intermediate_student_profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    username TEXT NOT NULL,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    roll_number TEXT NOT NULL,
    registration_number TEXT NOT NULL,
    program TEXT NOT NULL,
    official_record_id UUID NOT NULL REFERENCES public.official_intermediate_students(id) ON DELETE RESTRICT,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    CONSTRAINT uq_inter_profile_username UNIQUE (username),
    CONSTRAINT uq_inter_profile_roll UNIQUE (roll_number),
    CONSTRAINT uq_inter_profile_reg UNIQUE (registration_number),
    CONSTRAINT uq_inter_profile_record UNIQUE (official_record_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_inter_profile_username_lower ON public.intermediate_student_profiles (LOWER(TRIM(username)));
CREATE UNIQUE INDEX IF NOT EXISTS idx_inter_profile_roll_lower ON public.intermediate_student_profiles (LOWER(TRIM(roll_number)));
CREATE UNIQUE INDEX IF NOT EXISTS idx_inter_profile_reg_lower ON public.intermediate_student_profiles (LOWER(TRIM(registration_number)));

-- 3. Enable Row Level Security (RLS)
ALTER TABLE public.official_intermediate_students ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.intermediate_student_profiles ENABLE ROW LEVEL SECURITY;

-- 4. RLS Policies
DROP POLICY IF EXISTS "Allow public verification of student records" ON public.official_intermediate_students;
CREATE POLICY "Allow public verification of student records" 
ON public.official_intermediate_students
FOR SELECT
USING (true);

DROP POLICY IF EXISTS "Allow users to read own intermediate profile" ON public.intermediate_student_profiles;
CREATE POLICY "Allow users to read own intermediate profile"
ON public.intermediate_student_profiles
FOR SELECT
USING (auth.uid() = id);

DROP POLICY IF EXISTS "Allow authenticated user to insert own profile" ON public.intermediate_student_profiles;
CREATE POLICY "Allow authenticated user to insert own profile"
ON public.intermediate_student_profiles
FOR INSERT
WITH CHECK (auth.uid() = id);

-- 5. Pre-check Eligibility Function
CREATE OR REPLACE FUNCTION public.check_intermediate_student_eligibility(
    p_roll_number TEXT,
    p_registration_number TEXT,
    p_program TEXT,
    p_username TEXT
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
    v_clean_roll TEXT := UPPER(TRIM(p_roll_number));
    v_clean_reg TEXT := UPPER(TRIM(p_registration_number));
    v_clean_username TEXT := LOWER(TRIM(p_username));
    v_clean_program TEXT := TRIM(p_program);
    v_record RECORD;
BEGIN
    -- Check username uniqueness
    IF EXISTS (
        SELECT 1 FROM public.intermediate_student_profiles 
        WHERE LOWER(TRIM(username)) = v_clean_username
    ) THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'Username "' || p_username || '" is already taken.');
    END IF;

    -- Check if roll number is already registered in profiles
    IF EXISTS (
        SELECT 1 FROM public.intermediate_student_profiles 
        WHERE UPPER(TRIM(roll_number)) = v_clean_roll
    ) THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'College Roll Number "' || p_roll_number || '" is already linked to an existing account.');
    END IF;

    -- Check if registration number is already registered in profiles
    IF EXISTS (
        SELECT 1 FROM public.intermediate_student_profiles 
        WHERE UPPER(TRIM(registration_number)) = v_clean_reg
    ) THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'Registration Number "' || p_registration_number || '" is already registered.');
    END IF;

    -- Find official student record in registry
    SELECT * INTO v_record
    FROM public.official_intermediate_students
    WHERE UPPER(TRIM(roll_number)) = v_clean_roll 
      AND UPPER(TRIM(registration_number)) = v_clean_reg;

    IF NOT FOUND THEN
        RETURN jsonb_build_object(
            'eligible', false, 
            'error', 'No official student record found matching Roll No: ' || p_roll_number || ' and Reg No: ' || p_registration_number || '. Please verify with College Admission Office.'
        );
    END IF;

    -- Check program match
    IF LOWER(TRIM(v_record.program)) <> LOWER(v_clean_program) THEN
        RETURN jsonb_build_object(
            'eligible', false, 
            'error', 'Selected Program (' || p_program || ') does not match official enrolled program (' || v_record.program || ') for this student.'
        );
    END IF;

    -- Check if already claimed
    IF v_record.is_claimed = TRUE OR v_record.claimed_by_user_id IS NOT NULL THEN
        RETURN jsonb_build_object(
            'eligible', false, 
            'error', 'This student record (Roll No: ' || p_roll_number || ') has already been claimed by a registered account.'
        );
    END IF;

    RETURN jsonb_build_object(
        'eligible', true,
        'official_first_name', v_record.first_name,
        'official_last_name', v_record.last_name,
        'program', v_record.program
    );
END;
$$;

-- 6. Atomic Stored Procedure / RPC for Race-Free Intermediate Student Signup
CREATE OR REPLACE FUNCTION public.register_intermediate_student_account(
    p_user_id UUID,
    p_first_name TEXT,
    p_last_name TEXT,
    p_roll_number TEXT,
    p_registration_number TEXT,
    p_program TEXT,
    p_username TEXT
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
    v_clean_roll TEXT;
    v_clean_reg TEXT;
    v_clean_username TEXT;
    v_clean_program TEXT;
    v_record RECORD;
BEGIN
    -- 1. Sanitize & Normalize Inputs
    v_clean_roll := UPPER(TRIM(p_roll_number));
    v_clean_reg := UPPER(TRIM(p_registration_number));
    v_clean_username := LOWER(TRIM(p_username));
    v_clean_program := TRIM(p_program);

    IF v_clean_roll = '' OR v_clean_reg = '' OR v_clean_username = '' OR v_clean_program = '' THEN
        RETURN jsonb_build_object('success', false, 'error', 'All fields (Roll No, Reg No, Program, Username) are required.');
    END IF;

    -- 2. Check Username uniqueness
    IF EXISTS (
        SELECT 1 FROM public.intermediate_student_profiles 
        WHERE LOWER(TRIM(username)) = v_clean_username
    ) THEN
        RETURN jsonb_build_object('success', false, 'error', 'The username "' || p_username || '" is already taken. Please choose another username.');
    END IF;

    -- 3. Check Roll Number uniqueness in profiles
    IF EXISTS (
        SELECT 1 FROM public.intermediate_student_profiles 
        WHERE UPPER(TRIM(roll_number)) = v_clean_roll
    ) THEN
        RETURN jsonb_build_object('success', false, 'error', 'College Roll Number "' || p_roll_number || '" is already linked to an existing student account.');
    END IF;

    -- 4. Check Registration Number uniqueness in profiles
    IF EXISTS (
        SELECT 1 FROM public.intermediate_student_profiles 
        WHERE UPPER(TRIM(registration_number)) = v_clean_reg
    ) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Registration Number "' || p_registration_number || '" is already registered.');
    END IF;

    -- 5. Lock and verify Official Student Record from Administration Registry
    SELECT * INTO v_record
    FROM public.official_intermediate_students
    WHERE UPPER(TRIM(roll_number)) = v_clean_roll 
      AND UPPER(TRIM(registration_number)) = v_clean_reg
    FOR UPDATE;

    IF NOT FOUND THEN
        RETURN jsonb_build_object(
            'success', false, 
            'error', 'No official student record found matching Roll No: ' || p_roll_number || ' and Reg No: ' || p_registration_number || '. Please verify with College Admission/Academic office.'
        );
    END IF;

    -- 6. Verify Program match (case-insensitive)
    IF LOWER(TRIM(v_record.program)) <> LOWER(v_clean_program) THEN
        RETURN jsonb_build_object(
            'success', false, 
            'error', 'Selected Program (' || p_program || ') does not match official enrolled program (' || v_record.program || ') for this Roll Number.'
        );
    END IF;

    -- 7. Check if Official Record is already claimed
    IF v_record.is_claimed = TRUE OR v_record.claimed_by_user_id IS NOT NULL THEN
        RETURN jsonb_build_object(
            'success', false, 
            'error', 'This official student identity has already been claimed by a registered account.'
        );
    END IF;

    -- 8. Insert into intermediate_student_profiles
    INSERT INTO public.intermediate_student_profiles (
        id,
        username,
        first_name,
        last_name,
        roll_number,
        registration_number,
        program,
        official_record_id,
        created_at
    ) VALUES (
        p_user_id,
        v_clean_username,
        TRIM(p_first_name),
        TRIM(p_last_name),
        v_clean_roll,
        v_clean_reg,
        v_record.program,
        v_record.id,
        NOW()
    );

    -- 9. Mark Official Student Record as Claimed atomically
    UPDATE public.official_intermediate_students
    SET is_claimed = TRUE,
        claimed_by_user_id = p_user_id,
        claimed_at = NOW()
    WHERE id = v_record.id;

    RETURN jsonb_build_object(
        'success', true, 
        'message', 'Intermediate student registered and verified successfully.',
        'profile', jsonb_build_object(
            'id', p_user_id,
            'username', v_clean_username,
            'first_name', TRIM(p_first_name),
            'last_name', TRIM(p_last_name),
            'roll_number', v_clean_roll,
            'registration_number', v_clean_reg,
            'program', v_record.program
        )
    );
EXCEPTION
    WHEN unique_violation THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unique constraint violation: A student profile with this Roll Number, Registration Number, or Username already exists.');
    WHEN OTHERS THEN
        RETURN jsonb_build_object('success', false, 'error', 'Database error: ' || SQLERRM);
END;
$$;
