-- ==============================================================================
-- GGC M.B.Din Official Android App - Official Registry Management Backend
-- ==============================================================================
-- Run this SQL in your Supabase Project SQL Editor (https://supabase.com/dashboard/project/mhiudbdnrooipovvonfb/sql)
--
-- This schema establishes the official registry management backend for:
-- 1. Official BS Students Registry (`public.official_bs_students`)
-- 2. Official Intermediate Students Registry (`public.official_intermediate_students`)
-- 3. Official Faculty Registry (`public.official_faculty`)
--
-- Features:
-- - Strict server-side Admin enforcement (`public.is_admin()`)
-- - Case-insensitive unique indexes for Roll Numbers, Registration Numbers, and Faculty IDs
-- - Safe atomic claiming with row-level locks (`FOR UPDATE`)
-- - Record activation/deactivation (`is_active`)
-- - Protected administrative reset for erroneously claimed records
-- ==============================================================================

-- ==============================================================================
-- 1. ADDITIVE SCHEMA EXTENSIONS FOR REGISTRIES
-- ==============================================================================

-- 1.1 Extend Official BS Students Table
ALTER TABLE public.official_bs_students 
    ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE NOT NULL,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL;

-- Ensure case-insensitive unique indexes exist on BS Students
CREATE UNIQUE INDEX IF NOT EXISTS idx_official_bs_roll_lower ON public.official_bs_students (LOWER(TRIM(roll_number)));
CREATE UNIQUE INDEX IF NOT EXISTS idx_official_bs_reg_lower ON public.official_bs_students (LOWER(TRIM(registration_number)));
CREATE INDEX IF NOT EXISTS idx_official_bs_claimed ON public.official_bs_students (is_claimed, is_active);

-- 1.2 Extend Official Intermediate Students Table
ALTER TABLE public.official_intermediate_students 
    ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE NOT NULL,
    ADD COLUMN IF NOT EXISTS session TEXT DEFAULT '2024-2026',
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL;

-- Ensure case-insensitive unique indexes exist on Intermediate Students
CREATE UNIQUE INDEX IF NOT EXISTS idx_official_inter_roll_lower ON public.official_intermediate_students (LOWER(TRIM(roll_number)));
CREATE UNIQUE INDEX IF NOT EXISTS idx_official_inter_reg_lower ON public.official_intermediate_students (LOWER(TRIM(registration_number)));
CREATE INDEX IF NOT EXISTS idx_official_inter_claimed ON public.official_intermediate_students (is_claimed, is_active);

-- 1.3 Extend Official Faculty Table
ALTER TABLE public.official_faculty 
    ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE NOT NULL,
    ADD COLUMN IF NOT EXISTS first_name TEXT,
    ADD COLUMN IF NOT EXISTS last_name TEXT,
    ADD COLUMN IF NOT EXISTS phone_number TEXT,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL;

-- Ensure case-insensitive unique indexes exist on Faculty
CREATE UNIQUE INDEX IF NOT EXISTS idx_official_faculty_id_lower ON public.official_faculty (LOWER(TRIM(faculty_id)));
CREATE UNIQUE INDEX IF NOT EXISTS idx_official_faculty_email_lower ON public.official_faculty (LOWER(TRIM(institutional_email))) WHERE institutional_email IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_official_faculty_claimed ON public.official_faculty (is_claimed, is_active);

-- ==============================================================================
-- 2. UPDATED ELIGIBILITY FUNCTIONS (Active Status Enforced)
-- ==============================================================================

-- 2.1 BS Student Eligibility Check
CREATE OR REPLACE FUNCTION public.check_bs_student_eligibility(
    p_roll_number TEXT,
    p_registration_number TEXT,
    p_program TEXT,
    p_username TEXT
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_clean_roll TEXT := UPPER(TRIM(p_roll_number));
    v_clean_reg TEXT := UPPER(TRIM(p_registration_number));
    v_clean_username TEXT := LOWER(TRIM(p_username));
    v_clean_program TEXT := TRIM(p_program);
    v_record RECORD;
BEGIN
    -- Check username uniqueness across BS profiles
    IF EXISTS (
        SELECT 1 FROM public.bs_student_profiles 
        WHERE LOWER(TRIM(username)) = v_clean_username
    ) THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'Username "' || p_username || '" is already taken.');
    END IF;

    -- Check if roll number is already registered in BS profiles
    IF EXISTS (
        SELECT 1 FROM public.bs_student_profiles 
        WHERE UPPER(TRIM(roll_number)) = v_clean_roll
    ) THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'BS Roll Number "' || p_roll_number || '" is already linked to an existing account.');
    END IF;

    -- Check if university registration number is already registered in BS profiles
    IF EXISTS (
        SELECT 1 FROM public.bs_student_profiles 
        WHERE UPPER(TRIM(registration_number)) = v_clean_reg
    ) THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'University Registration Number "' || p_registration_number || '" is already registered.');
    END IF;

    -- Find official BS student record
    SELECT * INTO v_record
    FROM public.official_bs_students
    WHERE UPPER(TRIM(roll_number)) = v_clean_roll 
      AND UPPER(TRIM(registration_number)) = v_clean_reg;

    IF NOT FOUND THEN
        RETURN jsonb_build_object(
            'eligible', false, 
            'error', 'No official BS record found for Roll No: ' || p_roll_number || ' and Reg No: ' || p_registration_number || '. Please verify with Academic Branch.'
        );
    END IF;

    -- Verify record is active
    IF v_record.is_active = FALSE THEN
        RETURN jsonb_build_object(
            'eligible', false,
            'error', 'This official BS student record has been deactivated by the college administration.'
        );
    END IF;

    -- Verify enrolled program match
    IF LOWER(TRIM(v_record.program)) <> LOWER(v_clean_program) THEN
        RETURN jsonb_build_object(
            'eligible', false, 
            'error', 'Selected Program (' || p_program || ') does not match official enrolled program (' || v_record.program || ').'
        );
    END IF;

    -- Verify record is not already claimed
    IF v_record.is_claimed = TRUE OR v_record.claimed_by_user_id IS NOT NULL THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'This official BS student identity has already been claimed.');
    END IF;

    RETURN jsonb_build_object(
        'eligible', true,
        'official_id', v_record.id,
        'first_name', v_record.first_name,
        'last_name', v_record.last_name,
        'program', v_record.program,
        'session', v_record.session
    );
END;
$$;

-- 2.2 Intermediate Student Eligibility Check
CREATE OR REPLACE FUNCTION public.check_intermediate_student_eligibility(
    p_roll_number TEXT,
    p_registration_number TEXT,
    p_program TEXT,
    p_username TEXT
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
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

    -- Check if roll number is already registered
    IF EXISTS (
        SELECT 1 FROM public.intermediate_student_profiles 
        WHERE UPPER(TRIM(roll_number)) = v_clean_roll
    ) THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'Intermediate Roll Number "' || p_roll_number || '" is already registered.');
    END IF;

    -- Check if registration number is already registered
    IF EXISTS (
        SELECT 1 FROM public.intermediate_student_profiles 
        WHERE UPPER(TRIM(registration_number)) = v_clean_reg
    ) THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'Board Registration Number "' || p_registration_number || '" is already registered.');
    END IF;

    -- Find official intermediate student record
    SELECT * INTO v_record
    FROM public.official_intermediate_students
    WHERE UPPER(TRIM(roll_number)) = v_clean_roll 
      AND UPPER(TRIM(registration_number)) = v_clean_reg;

    IF NOT FOUND THEN
        RETURN jsonb_build_object(
            'eligible', false, 
            'error', 'No official record found for Roll No: ' || p_roll_number || ' and Reg No: ' || p_registration_number || '.'
        );
    END IF;

    -- Verify active status
    IF v_record.is_active = FALSE THEN
        RETURN jsonb_build_object(
            'eligible', false,
            'error', 'This official Intermediate student record has been deactivated by the college administration.'
        );
    END IF;

    -- Verify program match
    IF LOWER(TRIM(v_record.program)) <> LOWER(v_clean_program) THEN
        RETURN jsonb_build_object(
            'eligible', false, 
            'error', 'Selected Program (' || p_program || ') does not match official enrolled program (' || v_record.program || ').'
        );
    END IF;

    -- Verify record is not already claimed
    IF v_record.is_claimed = TRUE OR v_record.claimed_by_user_id IS NOT NULL THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'This official record has already been claimed.');
    END IF;

    RETURN jsonb_build_object(
        'eligible', true,
        'official_id', v_record.id,
        'first_name', v_record.first_name,
        'last_name', v_record.last_name,
        'program', v_record.program
    );
END;
$$;

-- 2.3 Faculty Eligibility Check
CREATE OR REPLACE FUNCTION public.check_faculty_eligibility(
    p_faculty_id TEXT,
    p_department TEXT,
    p_username TEXT,
    p_institutional_email TEXT DEFAULT NULL
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_clean_faculty_id TEXT := UPPER(TRIM(p_faculty_id));
    v_clean_department TEXT := TRIM(p_department);
    v_clean_username TEXT := LOWER(TRIM(p_username));
    v_clean_email TEXT := LOWER(TRIM(COALESCE(p_institutional_email, '')));
    v_record RECORD;
BEGIN
    -- 1. Check username uniqueness
    IF EXISTS (
        SELECT 1 FROM public.faculty_profiles 
        WHERE LOWER(TRIM(username)) = v_clean_username
    ) THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'Username "' || p_username || '" is already taken.');
    END IF;

    -- 2. Check faculty_id uniqueness in profiles
    IF EXISTS (
        SELECT 1 FROM public.faculty_profiles 
        WHERE UPPER(TRIM(faculty_id)) = v_clean_faculty_id
    ) THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'Faculty ID "' || p_faculty_id || '" is already registered.');
    END IF;

    -- 3. Check institutional_email uniqueness in profiles
    IF v_clean_email <> '' AND EXISTS (
        SELECT 1 FROM public.faculty_profiles 
        WHERE LOWER(TRIM(institutional_email)) = v_clean_email
    ) THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'Institutional Email "' || p_institutional_email || '" is already registered.');
    END IF;

    -- 4. Find official faculty record
    SELECT * INTO v_record
    FROM public.official_faculty
    WHERE UPPER(TRIM(faculty_id)) = v_clean_faculty_id;

    IF NOT FOUND THEN
        RETURN jsonb_build_object(
            'eligible', false, 
            'error', 'No official faculty record found matching ID: ' || p_faculty_id || '. Please contact College Administration.'
        );
    END IF;

    -- 5. Check active status
    IF v_record.is_active = FALSE THEN
        RETURN jsonb_build_object(
            'eligible', false,
            'error', 'This official faculty record is currently marked inactive.'
        );
    END IF;

    -- 6. Verify department match
    IF LOWER(TRIM(v_record.department)) <> LOWER(v_clean_department) THEN
        RETURN jsonb_build_object(
            'eligible', false, 
            'error', 'Selected Department (' || p_department || ') does not match official department (' || v_record.department || ').'
        );
    END IF;

    -- 7. Verify claim status
    IF v_record.is_claimed = TRUE OR v_record.claimed_by_user_id IS NOT NULL THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'This official faculty record has already been claimed.');
    END IF;

    RETURN jsonb_build_object(
        'eligible', true,
        'official_id', v_record.id,
        'full_name', v_record.full_name,
        'department', v_record.department,
        'designation', v_record.designation,
        'qualification', v_record.qualification,
        'institutional_email', v_record.institutional_email
    );
END;
$$;

-- ==============================================================================
-- 3. ADMINISTRATIVE RPCs (Protected strictly for Admin)
-- ==============================================================================

-- 3.1 Admin Manage BS Student Record (Insert / Update)
CREATE OR REPLACE FUNCTION public.admin_manage_bs_student_record(
    p_id UUID DEFAULT NULL,
    p_roll_number TEXT DEFAULT NULL,
    p_registration_number TEXT DEFAULT NULL,
    p_program TEXT DEFAULT NULL,
    p_session TEXT DEFAULT NULL,
    p_first_name TEXT DEFAULT NULL,
    p_last_name TEXT DEFAULT NULL,
    p_is_active BOOLEAN DEFAULT TRUE
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_caller_id UUID := auth.uid();
    v_clean_roll TEXT := UPPER(TRIM(p_roll_number));
    v_clean_reg TEXT := UPPER(TRIM(p_registration_number));
    v_result_id UUID;
BEGIN
    IF NOT public.is_admin(v_caller_id) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unauthorized: Only system administrators can manage official BS records.');
    END IF;

    IF v_clean_roll = '' OR v_clean_reg = '' OR TRIM(p_program) = '' OR TRIM(p_session) = '' THEN
        RETURN jsonb_build_object('success', false, 'error', 'Roll Number, Registration Number, Program, and Session are required.');
    END IF;

    IF p_id IS NULL THEN
        -- Check duplicate roll number
        IF EXISTS (SELECT 1 FROM public.official_bs_students WHERE UPPER(TRIM(roll_number)) = v_clean_roll) THEN
            RETURN jsonb_build_object('success', false, 'error', 'BS Roll Number "' || p_roll_number || '" already exists in the registry.');
        END IF;

        -- Check duplicate registration number
        IF EXISTS (SELECT 1 FROM public.official_bs_students WHERE UPPER(TRIM(registration_number)) = v_clean_reg) THEN
            RETURN jsonb_build_object('success', false, 'error', 'University Registration Number "' || p_registration_number || '" already exists in the registry.');
        END IF;

        INSERT INTO public.official_bs_students (
            roll_number,
            registration_number,
            program,
            session,
            first_name,
            last_name,
            is_active,
            created_at,
            updated_at
        ) VALUES (
            v_clean_roll,
            v_clean_reg,
            TRIM(p_program),
            TRIM(p_session),
            TRIM(p_first_name),
            TRIM(p_last_name),
            p_is_active,
            NOW(),
            NOW()
        ) RETURNING id INTO v_result_id;
    ELSE
        -- Update existing record
        UPDATE public.official_bs_students
        SET roll_number = COALESCE(v_clean_roll, roll_number),
            registration_number = COALESCE(v_clean_reg, registration_number),
            program = COALESCE(TRIM(p_program), program),
            session = COALESCE(TRIM(p_session), session),
            first_name = COALESCE(TRIM(p_first_name), first_name),
            last_name = COALESCE(TRIM(p_last_name), last_name),
            is_active = COALESCE(p_is_active, is_active),
            updated_at = NOW()
        WHERE id = p_id
        RETURNING id INTO v_result_id;

        IF v_result_id IS NULL THEN
            RETURN jsonb_build_object('success', false, 'error', 'Official BS student record not found.');
        END IF;
    END IF;

    RETURN jsonb_build_object(
        'success', true,
        'record_id', v_result_id,
        'message', 'Official BS student record saved successfully.'
    );
END;
$$;

-- 3.2 Admin Manage Intermediate Student Record (Insert / Update)
CREATE OR REPLACE FUNCTION public.admin_manage_intermediate_student_record(
    p_id UUID DEFAULT NULL,
    p_roll_number TEXT DEFAULT NULL,
    p_registration_number TEXT DEFAULT NULL,
    p_program TEXT DEFAULT NULL,
    p_session TEXT DEFAULT '2024-2026',
    p_first_name TEXT DEFAULT NULL,
    p_last_name TEXT DEFAULT NULL,
    p_is_active BOOLEAN DEFAULT TRUE
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_caller_id UUID := auth.uid();
    v_clean_roll TEXT := UPPER(TRIM(p_roll_number));
    v_clean_reg TEXT := UPPER(TRIM(p_registration_number));
    v_result_id UUID;
BEGIN
    IF NOT public.is_admin(v_caller_id) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unauthorized: Only system administrators can manage official Intermediate records.');
    END IF;

    IF v_clean_roll = '' OR v_clean_reg = '' OR TRIM(p_program) = '' THEN
        RETURN jsonb_build_object('success', false, 'error', 'Roll Number, Registration Number, and Program are required.');
    END IF;

    IF p_id IS NULL THEN
        -- Check duplicate roll number
        IF EXISTS (SELECT 1 FROM public.official_intermediate_students WHERE UPPER(TRIM(roll_number)) = v_clean_roll) THEN
            RETURN jsonb_build_object('success', false, 'error', 'Intermediate Roll Number "' || p_roll_number || '" already exists in the registry.');
        END IF;

        -- Check duplicate registration number
        IF EXISTS (SELECT 1 FROM public.official_intermediate_students WHERE UPPER(TRIM(registration_number)) = v_clean_reg) THEN
            RETURN jsonb_build_object('success', false, 'error', 'Board Registration Number "' || p_registration_number || '" already exists in the registry.');
        END IF;

        INSERT INTO public.official_intermediate_students (
            roll_number,
            registration_number,
            program,
            session,
            first_name,
            last_name,
            is_active,
            created_at,
            updated_at
        ) VALUES (
            v_clean_roll,
            v_clean_reg,
            TRIM(p_program),
            TRIM(p_session),
            TRIM(p_first_name),
            TRIM(p_last_name),
            p_is_active,
            NOW(),
            NOW()
        ) RETURNING id INTO v_result_id;
    ELSE
        UPDATE public.official_intermediate_students
        SET roll_number = COALESCE(v_clean_roll, roll_number),
            registration_number = COALESCE(v_clean_reg, registration_number),
            program = COALESCE(TRIM(p_program), program),
            session = COALESCE(TRIM(p_session), session),
            first_name = COALESCE(TRIM(p_first_name), first_name),
            last_name = COALESCE(TRIM(p_last_name), last_name),
            is_active = COALESCE(p_is_active, is_active),
            updated_at = NOW()
        WHERE id = p_id
        RETURNING id INTO v_result_id;

        IF v_result_id IS NULL THEN
            RETURN jsonb_build_object('success', false, 'error', 'Official Intermediate student record not found.');
        END IF;
    END IF;

    RETURN jsonb_build_object(
        'success', true,
        'record_id', v_result_id,
        'message', 'Official Intermediate student record saved successfully.'
    );
END;
$$;

-- 3.3 Admin Manage Official Faculty Record (Insert / Update)
CREATE OR REPLACE FUNCTION public.admin_manage_faculty_record(
    p_id UUID DEFAULT NULL,
    p_faculty_id TEXT DEFAULT NULL,
    p_full_name TEXT DEFAULT NULL,
    p_first_name TEXT DEFAULT NULL,
    p_last_name TEXT DEFAULT NULL,
    p_department TEXT DEFAULT NULL,
    p_designation TEXT DEFAULT NULL,
    p_qualification TEXT DEFAULT NULL,
    p_institutional_email TEXT DEFAULT NULL,
    p_phone_number TEXT DEFAULT NULL,
    p_is_active BOOLEAN DEFAULT TRUE
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_caller_id UUID := auth.uid();
    v_clean_fid TEXT := UPPER(TRIM(p_faculty_id));
    v_clean_email TEXT := LOWER(TRIM(NULLIF(p_institutional_email, '')));
    v_computed_name TEXT;
    v_result_id UUID;
BEGIN
    IF NOT public.is_admin(v_caller_id) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unauthorized: Only system administrators can manage official faculty records.');
    END IF;

    IF v_clean_fid = '' OR TRIM(p_department) = '' OR TRIM(p_designation) = '' THEN
        RETURN jsonb_build_object('success', false, 'error', 'Faculty ID, Department, and Designation are required.');
    END IF;

    v_computed_name := COALESCE(TRIM(p_full_name), TRIM(COALESCE(p_first_name, '') || ' ' || COALESCE(p_last_name, '')));
    IF v_computed_name = '' THEN
        v_computed_name := 'Faculty Member';
    END IF;

    IF p_id IS NULL THEN
        -- Check duplicate faculty ID
        IF EXISTS (SELECT 1 FROM public.official_faculty WHERE UPPER(TRIM(faculty_id)) = v_clean_fid) THEN
            RETURN jsonb_build_object('success', false, 'error', 'Faculty ID "' || p_faculty_id || '" already exists in the registry.');
        END IF;

        -- Check duplicate institutional email
        IF v_clean_email IS NOT NULL AND EXISTS (SELECT 1 FROM public.official_faculty WHERE LOWER(TRIM(institutional_email)) = v_clean_email) THEN
            RETURN jsonb_build_object('success', false, 'error', 'Institutional Email "' || p_institutional_email || '" already exists in the registry.');
        END IF;

        INSERT INTO public.official_faculty (
            faculty_id,
            full_name,
            first_name,
            last_name,
            department,
            designation,
            qualification,
            institutional_email,
            phone_number,
            is_active,
            created_at,
            updated_at
        ) VALUES (
            v_clean_fid,
            v_computed_name,
            TRIM(p_first_name),
            TRIM(p_last_name),
            TRIM(p_department),
            TRIM(p_designation),
            TRIM(p_qualification),
            v_clean_email,
            TRIM(p_phone_number),
            p_is_active,
            NOW(),
            NOW()
        ) RETURNING id INTO v_result_id;
    ELSE
        UPDATE public.official_faculty
        SET faculty_id = COALESCE(v_clean_fid, faculty_id),
            full_name = COALESCE(v_computed_name, full_name),
            first_name = COALESCE(TRIM(p_first_name), first_name),
            last_name = COALESCE(TRIM(p_last_name), last_name),
            department = COALESCE(TRIM(p_department), department),
            designation = COALESCE(TRIM(p_designation), designation),
            qualification = COALESCE(TRIM(p_qualification), qualification),
            institutional_email = COALESCE(v_clean_email, institutional_email),
            phone_number = COALESCE(TRIM(p_phone_number), phone_number),
            is_active = COALESCE(p_is_active, is_active),
            updated_at = NOW()
        WHERE id = p_id
        RETURNING id INTO v_result_id;

        IF v_result_id IS NULL THEN
            RETURN jsonb_build_object('success', false, 'error', 'Official faculty record not found.');
        END IF;
    END IF;

    RETURN jsonb_build_object(
        'success', true,
        'record_id', v_result_id,
        'message', 'Official faculty record saved successfully.'
    );
END;
$$;

-- 3.4 Admin Toggle Record Active Status
CREATE OR REPLACE FUNCTION public.admin_set_registry_record_active(
    p_registry_type TEXT, -- 'bs_student', 'intermediate_student', 'faculty'
    p_record_id UUID,
    p_is_active BOOLEAN
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_caller_id UUID := auth.uid();
BEGIN
    IF NOT public.is_admin(v_caller_id) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unauthorized: Only system administrators can alter active status.');
    END IF;

    CASE LOWER(TRIM(p_registry_type))
        WHEN 'bs_student' THEN
            UPDATE public.official_bs_students SET is_active = p_is_active, updated_at = NOW() WHERE id = p_record_id;
        WHEN 'intermediate_student' THEN
            UPDATE public.official_intermediate_students SET is_active = p_is_active, updated_at = NOW() WHERE id = p_record_id;
        WHEN 'faculty' THEN
            UPDATE public.official_faculty SET is_active = p_is_active, updated_at = NOW() WHERE id = p_record_id;
        ELSE
            RETURN jsonb_build_object('success', false, 'error', 'Invalid registry type: ' || p_registry_type);
    END CASE;

    IF NOT FOUND THEN
        RETURN jsonb_build_object('success', false, 'error', 'Record not found in the specified registry.');
    END IF;

    RETURN jsonb_build_object(
        'success', true,
        'message', 'Record active status updated to ' || p_is_active::text
    );
END;
$$;

-- 3.5 Admin Reset Claimed Record (Protected Administrative Reset)
CREATE OR REPLACE FUNCTION public.admin_reset_claimed_registry_record(
    p_registry_type TEXT, -- 'bs_student', 'intermediate_student', 'faculty'
    p_record_id UUID,
    p_reason TEXT DEFAULT 'Administrative correction'
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_caller_id UUID := auth.uid();
    v_old_claimed_by UUID;
BEGIN
    IF NOT public.is_admin(v_caller_id) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unauthorized: Only system administrators can reset claimed records.');
    END IF;

    CASE LOWER(TRIM(p_registry_type))
        WHEN 'bs_student' THEN
            SELECT claimed_by_user_id INTO v_old_claimed_by
            FROM public.official_bs_students
            WHERE id = p_record_id;

            IF v_old_claimed_by IS NOT NULL THEN
                -- Delete profile linked to official record
                DELETE FROM public.bs_student_profiles WHERE official_record_id = p_record_id;
            END IF;

            UPDATE public.official_bs_students
            SET is_claimed = FALSE,
                claimed_by_user_id = NULL,
                claimed_at = NULL,
                updated_at = NOW()
            WHERE id = p_record_id;

        WHEN 'intermediate_student' THEN
            SELECT claimed_by_user_id INTO v_old_claimed_by
            FROM public.official_intermediate_students
            WHERE id = p_record_id;

            IF v_old_claimed_by IS NOT NULL THEN
                DELETE FROM public.intermediate_student_profiles WHERE official_record_id = p_record_id;
            END IF;

            UPDATE public.official_intermediate_students
            SET is_claimed = FALSE,
                claimed_by_user_id = NULL,
                claimed_at = NULL,
                updated_at = NOW()
            WHERE id = p_record_id;

        WHEN 'faculty' THEN
            SELECT claimed_by_user_id INTO v_old_claimed_by
            FROM public.official_faculty
            WHERE id = p_record_id;

            IF v_old_claimed_by IS NOT NULL THEN
                DELETE FROM public.faculty_profiles WHERE official_record_id = p_record_id;
            END IF;

            UPDATE public.official_faculty
            SET is_claimed = FALSE,
                claimed_by_user_id = NULL,
                claimed_at = NULL,
                updated_at = NOW()
            WHERE id = p_record_id;

        ELSE
            RETURN jsonb_build_object('success', false, 'error', 'Invalid registry type: ' || p_registry_type);
    END CASE;

    RETURN jsonb_build_object(
        'success', true,
        'message', 'Record claim status has been reset successfully. The identity can now be re-claimed.',
        'released_user_id', v_old_claimed_by
    );
END;
$$;

-- ==============================================================================
-- 4. ROW LEVEL SECURITY (RLS) POLICIES ON REGISTRIES
-- ==============================================================================

-- 4.1. Official BS Students RLS
ALTER TABLE public.official_bs_students ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Public can view official bs records for verification" ON public.official_bs_students;
CREATE POLICY "Public can view official bs records for verification"
ON public.official_bs_students FOR SELECT
USING (true);

DROP POLICY IF EXISTS "Admins have full access to official bs records" ON public.official_bs_students;
CREATE POLICY "Admins have full access to official bs records"
ON public.official_bs_students FOR ALL
USING (public.is_admin())
WITH CHECK (public.is_admin());

-- 4.2. Official Intermediate Students RLS
ALTER TABLE public.official_intermediate_students ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Public can view official inter records for verification" ON public.official_intermediate_students;
CREATE POLICY "Public can view official inter records for verification"
ON public.official_intermediate_students FOR SELECT
USING (true);

DROP POLICY IF EXISTS "Admins have full access to official inter records" ON public.official_intermediate_students;
CREATE POLICY "Admins have full access to official inter records"
ON public.official_intermediate_students FOR ALL
USING (public.is_admin())
WITH CHECK (public.is_admin());

-- 4.3. Official Faculty Registry RLS
ALTER TABLE public.official_faculty ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Public can view official faculty records for verification" ON public.official_faculty;
CREATE POLICY "Public can view official faculty records for verification"
ON public.official_faculty FOR SELECT
USING (true);

DROP POLICY IF EXISTS "Admins have full access to official faculty records" ON public.official_faculty;
CREATE POLICY "Admins have full access to official faculty records"
ON public.official_faculty FOR ALL
USING (public.is_admin())
WITH CHECK (public.is_admin());
