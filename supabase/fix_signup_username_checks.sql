-- ==============================================================================
-- GOVT. GRADUATE COLLEGE MANDI BAHAUDDIN (GGC M.B.DIN) - OFFICIAL APP
-- MIGRATION: SECURE USERNAME AVAILABILITY RPCS & SIGNUP ELIGIBILITY FIXES
-- ==============================================================================
-- This script adds lightweight, secure SECURITY DEFINER RPCs for username availability
-- pre-checks during signup, without requiring or allowing direct SELECT access to
-- student or faculty profile tables under Row Level Security (RLS).
-- ==============================================================================

-- 1. Check Intermediate Student Username Availability
CREATE OR REPLACE FUNCTION public.check_intermediate_username_available(
    p_username TEXT
)
RETURNS JSONB
LANGUAGE plpgsql
STABLE
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_clean TEXT;
    v_taken BOOLEAN;
BEGIN
    v_clean := LOWER(TRIM(COALESCE(p_username, '')));
    
    IF v_clean = '' OR LENGTH(v_clean) < 3 THEN
        RETURN jsonb_build_object('available', false, 'error', 'Username must be at least 3 characters long.');
    END IF;

    SELECT EXISTS(
        SELECT 1 FROM public.intermediate_student_profiles
        WHERE LOWER(TRIM(username)) = v_clean
    ) INTO v_taken;

    IF v_taken THEN
        RETURN jsonb_build_object('available', false);
    ELSE
        RETURN jsonb_build_object('available', true);
    END IF;
END;
$$;

-- 2. Check BS Student Username Availability
CREATE OR REPLACE FUNCTION public.check_bs_username_available(
    p_username TEXT
)
RETURNS JSONB
LANGUAGE plpgsql
STABLE
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_clean TEXT;
    v_taken BOOLEAN;
BEGIN
    v_clean := LOWER(TRIM(COALESCE(p_username, '')));
    
    IF v_clean = '' OR LENGTH(v_clean) < 3 THEN
        RETURN jsonb_build_object('available', false, 'error', 'Username must be at least 3 characters long.');
    END IF;

    SELECT EXISTS(
        SELECT 1 FROM public.bs_student_profiles
        WHERE LOWER(TRIM(username)) = v_clean
    ) INTO v_taken;

    IF v_taken THEN
        RETURN jsonb_build_object('available', false);
    ELSE
        RETURN jsonb_build_object('available', true);
    END IF;
END;
$$;

-- 3. Check Faculty Username Availability
CREATE OR REPLACE FUNCTION public.check_faculty_username_available(
    p_username TEXT
)
RETURNS JSONB
LANGUAGE plpgsql
STABLE
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_clean TEXT;
    v_taken BOOLEAN;
BEGIN
    v_clean := LOWER(TRIM(COALESCE(p_username, '')));
    
    IF v_clean = '' OR LENGTH(v_clean) < 3 THEN
        RETURN jsonb_build_object('available', false, 'error', 'Username must be at least 3 characters long.');
    END IF;

    SELECT EXISTS(
        SELECT 1 FROM public.faculty_profiles
        WHERE LOWER(TRIM(username)) = v_clean
    ) INTO v_taken;

    IF v_taken THEN
        RETURN jsonb_build_object('available', false);
    ELSE
        RETURN jsonb_build_object('available', true);
    END IF;
END;
$$;

-- 4. Overloaded / Flexible Signatures for Eligibility Functions (Supporting both p_program and p_program_name)

-- 4.1 BS Student Eligibility Check
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
    v_record RECORD;
    v_username_exists BOOLEAN;
BEGIN
    SELECT * INTO v_record FROM public.official_bs_students
    WHERE LOWER(TRIM(roll_number)) = LOWER(TRIM(p_roll_number))
      AND LOWER(TRIM(registration_number)) = LOWER(TRIM(p_registration_number));

    IF NOT FOUND THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'No official BS student record matches this Roll and Registration number.');
    END IF;

    IF NOT v_record.is_active THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'This official student record is marked inactive. Contact the administration.');
    END IF;

    IF v_record.is_claimed THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'An account has already been claimed for this student record.');
    END IF;

    IF p_program_name IS NOT NULL AND TRIM(p_program_name) <> '' THEN
        IF LOWER(TRIM(v_record.program_name)) <> LOWER(TRIM(p_program_name)) THEN
            RETURN jsonb_build_object('eligible', false, 'error', 'Selected program does not match official enrolled program (' || v_record.program_name || ').');
        END IF;
    END IF;

    SELECT EXISTS(
        SELECT 1 FROM public.bs_student_profiles 
        WHERE LOWER(TRIM(username)) = LOWER(TRIM(p_username))
    ) INTO v_username_exists;

    IF v_username_exists THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'Username is already taken. Please choose another.');
    END IF;

    RETURN jsonb_build_object(
        'eligible', true,
        'official_id', v_record.id,
        'student_name', v_record.student_name,
        'program_name', v_record.program_name,
        'session_year', v_record.session_year,
        'semester_number', v_record.semester_number
    );
END;
$$;

-- 4.2 Intermediate Student Eligibility Check
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
    v_record RECORD;
    v_username_exists BOOLEAN;
BEGIN
    SELECT * INTO v_record FROM public.official_intermediate_students
    WHERE LOWER(TRIM(roll_number)) = LOWER(TRIM(p_roll_number))
      AND LOWER(TRIM(registration_number)) = LOWER(TRIM(p_registration_number));

    IF NOT FOUND THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'No official Intermediate student record matches this Roll and Registration number.');
    END IF;

    IF NOT v_record.is_active THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'This official student record is marked inactive.');
    END IF;

    IF v_record.is_claimed THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'An account has already been claimed for this record.');
    END IF;

    IF p_program_name IS NOT NULL AND TRIM(p_program_name) <> '' THEN
        IF LOWER(TRIM(v_record.program_name)) <> LOWER(TRIM(p_program_name)) THEN
            RETURN jsonb_build_object('eligible', false, 'error', 'Selected program does not match official enrolled program (' || v_record.program_name || ').');
        END IF;
    END IF;

    SELECT EXISTS(
        SELECT 1 FROM public.intermediate_student_profiles 
        WHERE LOWER(TRIM(username)) = LOWER(TRIM(p_username))
    ) INTO v_username_exists;

    IF v_username_exists THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'Username is already taken.');
    END IF;

    RETURN jsonb_build_object(
        'eligible', true,
        'official_id', v_record.id,
        'student_name', v_record.student_name,
        'program_name', v_record.program_name,
        'session_year', v_record.session_year,
        'part_number', v_record.part_number
    );
END;
$$;

-- 5. Permissions: Grant Execute to anon & authenticated roles for signup pre-checks
GRANT EXECUTE ON FUNCTION public.check_intermediate_username_available(TEXT) TO anon, authenticated, service_role;
GRANT EXECUTE ON FUNCTION public.check_bs_username_available(TEXT) TO anon, authenticated, service_role;
GRANT EXECUTE ON FUNCTION public.check_faculty_username_available(TEXT) TO anon, authenticated, service_role;
GRANT EXECUTE ON FUNCTION public.check_bs_student_eligibility(TEXT, TEXT, TEXT, TEXT) TO anon, authenticated, service_role;
GRANT EXECUTE ON FUNCTION public.check_intermediate_student_eligibility(TEXT, TEXT, TEXT, TEXT) TO anon, authenticated, service_role;
GRANT EXECUTE ON FUNCTION public.check_faculty_eligibility(TEXT, TEXT, TEXT, TEXT) TO anon, authenticated, service_role;
GRANT EXECUTE ON FUNCTION public.claim_bs_student_account(TEXT, TEXT, TEXT, TEXT, TEXT, TEXT, INT) TO authenticated, service_role;
GRANT EXECUTE ON FUNCTION public.claim_intermediate_student_account(TEXT, TEXT, TEXT, TEXT, TEXT, TEXT) TO authenticated, service_role;
GRANT EXECUTE ON FUNCTION public.claim_faculty_account(TEXT, TEXT, TEXT, TEXT) TO authenticated, service_role;

-- Ensure supporting case-insensitive indexes exist on usernames
CREATE UNIQUE INDEX IF NOT EXISTS idx_bs_student_profiles_username_lower ON public.bs_student_profiles (LOWER(TRIM(username)));
CREATE UNIQUE INDEX IF NOT EXISTS idx_inter_student_profiles_username_lower ON public.intermediate_student_profiles (LOWER(TRIM(username)));
CREATE UNIQUE INDEX IF NOT EXISTS idx_faculty_profiles_username_lower ON public.faculty_profiles (LOWER(TRIM(username)));
