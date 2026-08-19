-- ==============================================================================
-- GGC M.B.DIN - OPEN DYNAMIC STUDENT REGISTRATION MIGRATION
-- ==============================================================================
-- Run this script in your Supabase SQL Editor:
-- https://supabase.com/dashboard/project/mhiudbdnrooipovvonfb/sql
--
-- This script enables open self-registration for new students while strictly
-- guaranteeing uniqueness on:
-- 1. Username
-- 2. College Roll Number
-- 3. Board/University Registration Number
--
-- If an official record exists in the admin registry, it is automatically
-- claimed and linked. If not, the student account is created dynamically.
-- ==============================================================================

-- 1. Ensure official_record_id is nullable for dynamic registrations
ALTER TABLE public.intermediate_student_profiles 
    ALTER COLUMN official_record_id DROP NOT NULL;

ALTER TABLE public.bs_student_profiles 
    ALTER COLUMN official_record_id DROP NOT NULL;

-- 2. Ensure case-insensitive unique indexes exist on intermediate profiles
CREATE UNIQUE INDEX IF NOT EXISTS idx_inter_profile_username_lower ON public.intermediate_student_profiles (LOWER(TRIM(username)));
CREATE UNIQUE INDEX IF NOT EXISTS idx_inter_profile_roll_lower ON public.intermediate_student_profiles (UPPER(TRIM(roll_number)));
CREATE UNIQUE INDEX IF NOT EXISTS idx_inter_profile_reg_lower ON public.intermediate_student_profiles (UPPER(TRIM(registration_number)));

-- 3. Ensure case-insensitive unique indexes exist on BS profiles
CREATE UNIQUE INDEX IF NOT EXISTS idx_bs_profile_username_lower ON public.bs_student_profiles (LOWER(TRIM(username)));
CREATE UNIQUE INDEX IF NOT EXISTS idx_bs_profile_roll_lower ON public.bs_student_profiles (UPPER(TRIM(roll_number)));
CREATE UNIQUE INDEX IF NOT EXISTS idx_bs_profile_reg_lower ON public.bs_student_profiles (UPPER(TRIM(registration_number)));

-- ==============================================================================
-- 4. INTERMEDIATE STUDENT ELIGIBILITY CHECK (DYNAMIC + UNIQUE CHECKS)
-- ==============================================================================
CREATE OR REPLACE FUNCTION public.check_intermediate_student_eligibility(
    p_roll_number TEXT,
    p_registration_number TEXT,
    p_program_name TEXT,
    p_username TEXT
)
RETURNS JSONB
LANGUAGE plpgsql
STABLE
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_clean_roll TEXT;
    v_clean_reg TEXT;
    v_clean_username TEXT;
    v_record RECORD;
BEGIN
    v_clean_roll := UPPER(TRIM(COALESCE(p_roll_number, '')));
    v_clean_reg := UPPER(TRIM(COALESCE(p_registration_number, '')));
    v_clean_username := LOWER(TRIM(COALESCE(p_username, '')));

    IF v_clean_roll = '' OR v_clean_reg = '' OR v_clean_username = '' THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'Roll Number, Registration Number, and Username are required.');
    END IF;

    -- 1. Check Username Uniqueness
    IF EXISTS (
        SELECT 1 FROM public.intermediate_student_profiles 
        WHERE LOWER(TRIM(username)) = v_clean_username
    ) THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'The username "' || p_username || '" is already taken. Please choose another.');
    END IF;

    -- 2. Check Roll Number Uniqueness
    IF EXISTS (
        SELECT 1 FROM public.intermediate_student_profiles 
        WHERE UPPER(TRIM(roll_number)) = v_clean_roll
    ) THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'College Roll Number "' || p_roll_number || '" is already registered with an existing student account.');
    END IF;

    -- 3. Check Registration Number Uniqueness
    IF EXISTS (
        SELECT 1 FROM public.intermediate_student_profiles 
        WHERE UPPER(TRIM(registration_number)) = v_clean_reg
    ) THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'Registration Number "' || p_registration_number || '" is already registered with an existing student account.');
    END IF;

    -- 4. Check if an official registry record exists:
    SELECT * INTO v_record FROM public.official_intermediate_students
    WHERE UPPER(TRIM(roll_number)) = v_clean_roll
       OR UPPER(TRIM(registration_number)) = v_clean_reg;

    IF FOUND THEN
        IF NOT v_record.is_active THEN
            RETURN jsonb_build_object('eligible', false, 'error', 'This official student record is marked inactive.');
        END IF;

        IF v_record.is_claimed THEN
            RETURN jsonb_build_object('eligible', false, 'error', 'An account has already been claimed for this Roll/Registration number.');
        END IF;
    END IF;

    RETURN jsonb_build_object(
        'eligible', true,
        'message', 'Student record is eligible for registration.'
    );
END;
$$;

-- ==============================================================================
-- 5. INTERMEDIATE STUDENT ACCOUNT CLAIM / REGISTRATION
-- ==============================================================================
CREATE OR REPLACE FUNCTION public.claim_intermediate_student_account(
    p_roll_number TEXT,
    p_registration_number TEXT,
    p_program_name TEXT,
    p_username TEXT,
    p_first_name TEXT,
    p_last_name TEXT
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_user_id UUID;
    v_clean_roll TEXT;
    v_clean_reg TEXT;
    v_clean_username TEXT;
    v_clean_program TEXT;
    v_clean_first TEXT;
    v_clean_last TEXT;
    v_record RECORD;
    v_official_id UUID := NULL;
BEGIN
    v_user_id := auth.uid();
    IF v_user_id IS NULL THEN
        RETURN jsonb_build_object('success', false, 'error', 'Not authenticated.');
    END IF;

    v_clean_roll := UPPER(TRIM(COALESCE(p_roll_number, '')));
    v_clean_reg := UPPER(TRIM(COALESCE(p_registration_number, '')));
    v_clean_username := LOWER(TRIM(COALESCE(p_username, '')));
    v_clean_program := TRIM(COALESCE(p_program_name, ''));
    v_clean_first := TRIM(COALESCE(p_first_name, ''));
    v_clean_last := TRIM(COALESCE(p_last_name, ''));

    -- Duplicate check on username
    IF EXISTS (
        SELECT 1 FROM public.intermediate_student_profiles 
        WHERE LOWER(TRIM(username)) = v_clean_username AND id <> v_user_id
    ) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Username "' || p_username || '" is already taken.');
    END IF;

    -- Duplicate check on roll number
    IF EXISTS (
        SELECT 1 FROM public.intermediate_student_profiles 
        WHERE UPPER(TRIM(roll_number)) = v_clean_roll AND id <> v_user_id
    ) THEN
        RETURN jsonb_build_object('success', false, 'error', 'College Roll Number "' || p_roll_number || '" is already registered.');
    END IF;

    -- Duplicate check on registration number
    IF EXISTS (
        SELECT 1 FROM public.intermediate_student_profiles 
        WHERE UPPER(TRIM(registration_number)) = v_clean_reg AND id <> v_user_id
    ) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Registration Number "' || p_registration_number || '" is already registered.');
    END IF;

    -- Check if official registry record exists
    SELECT * INTO v_record FROM public.official_intermediate_students
    WHERE UPPER(TRIM(roll_number)) = v_clean_roll
       OR UPPER(TRIM(registration_number)) = v_clean_reg
    FOR UPDATE;

    IF FOUND THEN
        IF v_record.is_claimed AND v_record.claimed_by_user_id <> v_user_id THEN
            RETURN jsonb_build_object('success', false, 'error', 'This official record has already been claimed by another account.');
        END IF;

        v_official_id := v_record.id;

        UPDATE public.official_intermediate_students
        SET is_claimed = TRUE,
            claimed_by_user_id = v_user_id,
            claimed_at = NOW(),
            updated_at = NOW()
        WHERE id = v_record.id;
    END IF;

    -- Insert or update intermediate student profile
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
        v_user_id,
        v_clean_username,
        v_clean_first,
        v_clean_last,
        v_clean_roll,
        v_clean_reg,
        v_clean_program,
        v_official_id,
        NOW()
    )
    ON CONFLICT (id) DO UPDATE SET
        username = v_clean_username,
        first_name = v_clean_first,
        last_name = v_clean_last,
        roll_number = v_clean_roll,
        registration_number = v_clean_reg,
        program = v_clean_program,
        official_record_id = COALESCE(v_official_id, public.intermediate_student_profiles.official_record_id);

    -- Assign user role
    INSERT INTO public.user_roles (user_id, role, department)
    VALUES (v_user_id, 'student_intermediate'::public.app_role, v_clean_program)
    ON CONFLICT (user_id) DO UPDATE SET 
        role = 'student_intermediate'::public.app_role, 
        department = v_clean_program;

    RETURN jsonb_build_object(
        'success', true,
        'message', 'Intermediate student account created successfully.'
    );
END;
$$;

-- ==============================================================================
-- 6. BS STUDENT ELIGIBILITY CHECK (DYNAMIC + UNIQUE CHECKS)
-- ==============================================================================
CREATE OR REPLACE FUNCTION public.check_bs_student_eligibility(
    p_roll_number TEXT,
    p_registration_number TEXT,
    p_program_name TEXT,
    p_username TEXT
)
RETURNS JSONB
LANGUAGE plpgsql
STABLE
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_clean_roll TEXT;
    v_clean_reg TEXT;
    v_clean_username TEXT;
    v_record RECORD;
BEGIN
    v_clean_roll := UPPER(TRIM(COALESCE(p_roll_number, '')));
    v_clean_reg := UPPER(TRIM(COALESCE(p_registration_number, '')));
    v_clean_username := LOWER(TRIM(COALESCE(p_username, '')));

    IF v_clean_roll = '' OR v_clean_reg = '' OR v_clean_username = '' THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'Roll Number, Registration Number, and Username are required.');
    END IF;

    -- 1. Check Username Uniqueness
    IF EXISTS (
        SELECT 1 FROM public.bs_student_profiles 
        WHERE LOWER(TRIM(username)) = v_clean_username
    ) THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'The username "' || p_username || '" is already taken. Please choose another.');
    END IF;

    -- 2. Check Roll Number Uniqueness
    IF EXISTS (
        SELECT 1 FROM public.bs_student_profiles 
        WHERE UPPER(TRIM(roll_number)) = v_clean_roll
    ) THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'BS Roll Number "' || p_roll_number || '" is already registered with an existing student account.');
    END IF;

    -- 3. Check Registration Number Uniqueness
    IF EXISTS (
        SELECT 1 FROM public.bs_student_profiles 
        WHERE UPPER(TRIM(registration_number)) = v_clean_reg
    ) THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'University Registration Number "' || p_registration_number || '" is already registered.');
    END IF;

    -- 4. Check if official registry record exists:
    SELECT * INTO v_record FROM public.official_bs_students
    WHERE UPPER(TRIM(roll_number)) = v_clean_roll
       OR UPPER(TRIM(registration_number)) = v_clean_reg;

    IF FOUND THEN
        IF NOT v_record.is_active THEN
            RETURN jsonb_build_object('eligible', false, 'error', 'This official student record is marked inactive.');
        END IF;

        IF v_record.is_claimed THEN
            RETURN jsonb_build_object('eligible', false, 'error', 'An account has already been claimed for this student record.');
        END IF;
    END IF;

    RETURN jsonb_build_object(
        'eligible', true,
        'message', 'BS student record is eligible for registration.'
    );
END;
$$;

-- ==============================================================================
-- 7. BS STUDENT ACCOUNT CLAIM / REGISTRATION
-- ==============================================================================
CREATE OR REPLACE FUNCTION public.claim_bs_student_account(
    p_roll_number TEXT,
    p_registration_number TEXT,
    p_program_name TEXT,
    p_username TEXT,
    p_first_name TEXT,
    p_last_name TEXT,
    p_semester_number INT DEFAULT 1
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_user_id UUID;
    v_clean_roll TEXT;
    v_clean_reg TEXT;
    v_clean_username TEXT;
    v_clean_program TEXT;
    v_clean_first TEXT;
    v_clean_last TEXT;
    v_record RECORD;
    v_official_id UUID := NULL;
BEGIN
    v_user_id := auth.uid();
    IF v_user_id IS NULL THEN
        RETURN jsonb_build_object('success', false, 'error', 'Not authenticated.');
    END IF;

    v_clean_roll := UPPER(TRIM(COALESCE(p_roll_number, '')));
    v_clean_reg := UPPER(TRIM(COALESCE(p_registration_number, '')));
    v_clean_username := LOWER(TRIM(COALESCE(p_username, '')));
    v_clean_program := TRIM(COALESCE(p_program_name, ''));
    v_clean_first := TRIM(COALESCE(p_first_name, ''));
    v_clean_last := TRIM(COALESCE(p_last_name, ''));

    IF EXISTS (
        SELECT 1 FROM public.bs_student_profiles 
        WHERE LOWER(TRIM(username)) = v_clean_username AND id <> v_user_id
    ) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Username "' || p_username || '" is already taken.');
    END IF;

    IF EXISTS (
        SELECT 1 FROM public.bs_student_profiles 
        WHERE UPPER(TRIM(roll_number)) = v_clean_roll AND id <> v_user_id
    ) THEN
        RETURN jsonb_build_object('success', false, 'error', 'BS Roll Number "' || p_roll_number || '" is already registered.');
    END IF;

    IF EXISTS (
        SELECT 1 FROM public.bs_student_profiles 
        WHERE UPPER(TRIM(registration_number)) = v_clean_reg AND id <> v_user_id
    ) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Registration Number "' || p_registration_number || '" is already registered.');
    END IF;

    SELECT * INTO v_record FROM public.official_bs_students
    WHERE UPPER(TRIM(roll_number)) = v_clean_roll
       OR UPPER(TRIM(registration_number)) = v_clean_reg
    FOR UPDATE;

    IF FOUND THEN
        IF v_record.is_claimed AND v_record.claimed_by_user_id <> v_user_id THEN
            RETURN jsonb_build_object('success', false, 'error', 'This official record has already been claimed.');
        END IF;

        v_official_id := v_record.id;

        UPDATE public.official_bs_students
        SET is_claimed = TRUE,
            claimed_by_user_id = v_user_id,
            claimed_at = NOW(),
            updated_at = NOW()
        WHERE id = v_record.id;
    END IF;

    INSERT INTO public.bs_student_profiles (
        id,
        username,
        first_name,
        last_name,
        roll_number,
        registration_number,
        program,
        semester_number,
        official_record_id,
        created_at
    ) VALUES (
        v_user_id,
        v_clean_username,
        v_clean_first,
        v_clean_last,
        v_clean_roll,
        v_clean_reg,
        v_clean_program,
        p_semester_number,
        v_official_id,
        NOW()
    )
    ON CONFLICT (id) DO UPDATE SET
        username = v_clean_username,
        first_name = v_clean_first,
        last_name = v_clean_last,
        roll_number = v_clean_roll,
        registration_number = v_clean_reg,
        program = v_clean_program,
        semester_number = p_semester_number,
        official_record_id = COALESCE(v_official_id, public.bs_student_profiles.official_record_id);

    INSERT INTO public.user_roles (user_id, role, department)
    VALUES (v_user_id, 'student_bs'::public.app_role, v_clean_program)
    ON CONFLICT (user_id) DO UPDATE SET 
        role = 'student_bs'::public.app_role, 
        department = v_clean_program;

    RETURN jsonb_build_object(
        'success', true,
        'message', 'BS student account created successfully.'
    );
END;
$$;

-- ==============================================================================
-- 8. GRANT PERMISSIONS
-- ==============================================================================
GRANT EXECUTE ON FUNCTION public.check_intermediate_student_eligibility(TEXT, TEXT, TEXT, TEXT) TO anon, authenticated, service_role;
GRANT EXECUTE ON FUNCTION public.claim_intermediate_student_account(TEXT, TEXT, TEXT, TEXT, TEXT, TEXT) TO authenticated, service_role;
GRANT EXECUTE ON FUNCTION public.check_bs_student_eligibility(TEXT, TEXT, TEXT, TEXT) TO anon, authenticated, service_role;
GRANT EXECUTE ON FUNCTION public.claim_bs_student_account(TEXT, TEXT, TEXT, TEXT, TEXT, TEXT, INT) TO authenticated, service_role;
