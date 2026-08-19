-- ==============================================================================
-- GGC M.B.Din Official Android App - Comprehensive Direct Simple Authentication
-- ==============================================================================
-- Run this script in Supabase SQL Editor:
-- https://supabase.com/dashboard/project/mhiudbdnrooipovvonfb/sql

-- Enable pgcrypto (Supabase standard)
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ==============================================================================
-- 1. TABLE STRUCTURE COMPATIBILITY & MIGRATION
-- ==============================================================================

-- 1.1 Intermediate Student Profiles Table
CREATE TABLE IF NOT EXISTS public.intermediate_student_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username TEXT UNIQUE NOT NULL,
    first_name TEXT,
    last_name TEXT,
    student_name TEXT,
    roll_number TEXT UNIQUE NOT NULL,
    registration_number TEXT UNIQUE NOT NULL,
    program TEXT,
    program_name TEXT,
    part_number INTEGER DEFAULT 1,
    session_year TEXT,
    password_hash TEXT,
    official_record_id UUID,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);

ALTER TABLE public.intermediate_student_profiles 
    ADD COLUMN IF NOT EXISTS password_hash TEXT,
    ADD COLUMN IF NOT EXISTS program TEXT,
    ADD COLUMN IF NOT EXISTS program_name TEXT,
    ADD COLUMN IF NOT EXISTS student_name TEXT,
    ADD COLUMN IF NOT EXISTS first_name TEXT,
    ADD COLUMN IF NOT EXISTS last_name TEXT,
    ADD COLUMN IF NOT EXISTS part_number INTEGER DEFAULT 1,
    ADD COLUMN IF NOT EXISTS session_year TEXT,
    ALTER COLUMN official_record_id DROP NOT NULL;

DO $$
BEGIN
    ALTER TABLE public.intermediate_student_profiles ALTER COLUMN student_name DROP NOT NULL;
    ALTER TABLE public.intermediate_student_profiles ALTER COLUMN program_name DROP NOT NULL;
    ALTER TABLE public.intermediate_student_profiles DROP CONSTRAINT IF EXISTS intermediate_student_profiles_id_fkey;
    ALTER TABLE public.intermediate_student_profiles DROP CONSTRAINT IF EXISTS intermediate_student_profiles_official_record_id_fkey;
    ALTER TABLE public.intermediate_student_profiles ALTER COLUMN id SET DEFAULT gen_random_uuid();
EXCEPTION WHEN OTHERS THEN
    NULL;
END $$;

-- 1.2 BS Student Profiles Table
CREATE TABLE IF NOT EXISTS public.bs_student_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username TEXT UNIQUE NOT NULL,
    first_name TEXT,
    last_name TEXT,
    student_name TEXT,
    roll_number TEXT UNIQUE NOT NULL,
    registration_number TEXT UNIQUE NOT NULL,
    program TEXT,
    program_name TEXT,
    semester TEXT,
    semester_number INTEGER DEFAULT 1,
    session_year TEXT,
    password_hash TEXT,
    official_record_id UUID,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);

ALTER TABLE public.bs_student_profiles 
    ADD COLUMN IF NOT EXISTS password_hash TEXT,
    ADD COLUMN IF NOT EXISTS program TEXT,
    ADD COLUMN IF NOT EXISTS program_name TEXT,
    ADD COLUMN IF NOT EXISTS student_name TEXT,
    ADD COLUMN IF NOT EXISTS first_name TEXT,
    ADD COLUMN IF NOT EXISTS last_name TEXT,
    ADD COLUMN IF NOT EXISTS semester TEXT,
    ADD COLUMN IF NOT EXISTS semester_number INTEGER DEFAULT 1,
    ADD COLUMN IF NOT EXISTS session_year TEXT,
    ALTER COLUMN official_record_id DROP NOT NULL;

DO $$
BEGIN
    ALTER TABLE public.bs_student_profiles ALTER COLUMN student_name DROP NOT NULL;
    ALTER TABLE public.bs_student_profiles ALTER COLUMN program_name DROP NOT NULL;
    ALTER TABLE public.bs_student_profiles DROP CONSTRAINT IF EXISTS bs_student_profiles_id_fkey;
    ALTER TABLE public.bs_student_profiles DROP CONSTRAINT IF EXISTS bs_student_profiles_official_record_id_fkey;
    ALTER TABLE public.bs_student_profiles ALTER COLUMN id SET DEFAULT gen_random_uuid();
EXCEPTION WHEN OTHERS THEN
    NULL;
END $$;

-- 1.3 Faculty Profiles Table
CREATE TABLE IF NOT EXISTS public.faculty_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username TEXT UNIQUE NOT NULL,
    full_name TEXT NOT NULL,
    faculty_id TEXT UNIQUE NOT NULL,
    department TEXT NOT NULL,
    designation TEXT,
    qualification TEXT,
    institutional_email TEXT,
    phone_number TEXT,
    password_hash TEXT,
    official_record_id UUID,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);

ALTER TABLE public.faculty_profiles 
    ADD COLUMN IF NOT EXISTS password_hash TEXT,
    ADD COLUMN IF NOT EXISTS full_name TEXT,
    ADD COLUMN IF NOT EXISTS qualification TEXT,
    ADD COLUMN IF NOT EXISTS institutional_email TEXT,
    ADD COLUMN IF NOT EXISTS designation TEXT,
    ADD COLUMN IF NOT EXISTS phone_number TEXT,
    ALTER COLUMN official_record_id DROP NOT NULL;

DO $$
BEGIN
    ALTER TABLE public.faculty_profiles ALTER COLUMN institutional_email DROP NOT NULL;
    ALTER TABLE public.faculty_profiles DROP CONSTRAINT IF EXISTS faculty_profiles_id_fkey;
    ALTER TABLE public.faculty_profiles DROP CONSTRAINT IF EXISTS faculty_profiles_official_record_id_fkey;
    ALTER TABLE public.faculty_profiles ALTER COLUMN id SET DEFAULT gen_random_uuid();
EXCEPTION WHEN OTHERS THEN
    NULL;
END $$;

-- Enable RLS
ALTER TABLE public.intermediate_student_profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.bs_student_profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.faculty_profiles ENABLE ROW LEVEL SECURITY;

-- Allow reading profiles safely
DO $$
BEGIN
    DROP POLICY IF EXISTS "Public read profiles" ON public.intermediate_student_profiles;
    CREATE POLICY "Public read profiles" ON public.intermediate_student_profiles FOR SELECT USING (true);
    
    DROP POLICY IF EXISTS "Public read bs profiles" ON public.bs_student_profiles;
    CREATE POLICY "Public read bs profiles" ON public.bs_student_profiles FOR SELECT USING (true);
    
    DROP POLICY IF EXISTS "Public read faculty profiles" ON public.faculty_profiles;
    CREATE POLICY "Public read faculty profiles" ON public.faculty_profiles FOR SELECT USING (true);
EXCEPTION WHEN OTHERS THEN
    NULL;
END $$;

-- ==============================================================================
-- 2. USERNAME AVAILABILITY RPC FUNCTIONS
-- ==============================================================================

CREATE OR REPLACE FUNCTION public.check_intermediate_username_available(p_username TEXT)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, extensions
AS $$
DECLARE
    v_clean TEXT := LOWER(TRIM(COALESCE(p_username, '')));
BEGIN
    IF v_clean = '' OR LENGTH(v_clean) < 3 THEN
        RETURN jsonb_build_object('available', false, 'error', 'Username must be at least 3 characters.');
    END IF;
    IF EXISTS (SELECT 1 FROM public.intermediate_student_profiles WHERE LOWER(TRIM(username)) = v_clean) THEN
        RETURN jsonb_build_object('available', false, 'error', 'Username is already taken.');
    END IF;
    RETURN jsonb_build_object('available', true);
END;
$$;

CREATE OR REPLACE FUNCTION public.check_bs_username_available(p_username TEXT)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, extensions
AS $$
DECLARE
    v_clean TEXT := LOWER(TRIM(COALESCE(p_username, '')));
BEGIN
    IF v_clean = '' OR LENGTH(v_clean) < 3 THEN
        RETURN jsonb_build_object('available', false, 'error', 'Username must be at least 3 characters.');
    END IF;
    IF EXISTS (SELECT 1 FROM public.bs_student_profiles WHERE LOWER(TRIM(username)) = v_clean) THEN
        RETURN jsonb_build_object('available', false, 'error', 'Username is already taken.');
    END IF;
    RETURN jsonb_build_object('available', true);
END;
$$;

CREATE OR REPLACE FUNCTION public.check_faculty_username_available(p_username TEXT)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, extensions
AS $$
DECLARE
    v_clean TEXT := LOWER(TRIM(COALESCE(p_username, '')));
BEGIN
    IF v_clean = '' OR LENGTH(v_clean) < 3 THEN
        RETURN jsonb_build_object('available', false, 'error', 'Username must be at least 3 characters.');
    END IF;
    IF EXISTS (SELECT 1 FROM public.faculty_profiles WHERE LOWER(TRIM(username)) = v_clean) THEN
        RETURN jsonb_build_object('available', false, 'error', 'Username is already taken.');
    END IF;
    RETURN jsonb_build_object('available', true);
END;
$$;

-- ==============================================================================
-- 3. INTERMEDIATE DIRECT AUTHENTICATION
-- ==============================================================================

CREATE OR REPLACE FUNCTION public.direct_register_intermediate_student(
    p_roll_number TEXT,
    p_registration_number TEXT,
    p_program_name TEXT,
    p_username TEXT,
    p_first_name TEXT,
    p_last_name TEXT,
    p_password TEXT
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, extensions
AS $$
DECLARE
    v_clean_roll TEXT := UPPER(TRIM(COALESCE(p_roll_number, '')));
    v_clean_reg TEXT := UPPER(TRIM(COALESCE(p_registration_number, '')));
    v_clean_username TEXT := LOWER(TRIM(COALESCE(p_username, '')));
    v_clean_program TEXT := TRIM(COALESCE(p_program_name, ''));
    v_clean_first TEXT := TRIM(COALESCE(p_first_name, ''));
    v_clean_last TEXT := TRIM(COALESCE(p_last_name, ''));
    v_full_name TEXT := TRIM(v_clean_first || ' ' || v_clean_last);
    v_new_id UUID := gen_random_uuid();
    v_pwd_hash TEXT;
    v_profile RECORD;
BEGIN
    IF v_clean_roll = '' OR v_clean_reg = '' OR v_clean_username = '' THEN
        RETURN jsonb_build_object('success', false, 'error', 'Roll Number, Registration Number, and Username are required.');
    END IF;

    IF LENGTH(v_clean_username) < 3 THEN
        RETURN jsonb_build_object('success', false, 'error', 'Username must be at least 3 characters.');
    END IF;

    IF COALESCE(p_password, '') = '' OR LENGTH(p_password) < 6 THEN
        RETURN jsonb_build_object('success', false, 'error', 'Password must be at least 6 characters.');
    END IF;

    -- Check Username Uniqueness
    IF EXISTS (
        SELECT 1 FROM public.intermediate_student_profiles 
        WHERE LOWER(TRIM(username)) = v_clean_username
    ) THEN
        RETURN jsonb_build_object('success', false, 'error', 'The username "' || p_username || '" is already taken. Please choose another.');
    END IF;

    -- Check Roll Number Uniqueness
    IF EXISTS (
        SELECT 1 FROM public.intermediate_student_profiles 
        WHERE UPPER(TRIM(roll_number)) = v_clean_roll
    ) THEN
        RETURN jsonb_build_object('success', false, 'error', 'College Roll Number "' || p_roll_number || '" is already registered.');
    END IF;

    -- Check Registration Number Uniqueness
    IF EXISTS (
        SELECT 1 FROM public.intermediate_student_profiles 
        WHERE UPPER(TRIM(registration_number)) = v_clean_reg
    ) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Registration Number "' || p_registration_number || '" is already registered.');
    END IF;

    -- Hash password safely
    BEGIN
        v_pwd_hash := crypt(p_password, gen_salt('bf'::text));
    EXCEPTION WHEN OTHERS THEN
        v_pwd_hash := md5(p_password || 'ggc_salt_2026');
    END;

    -- Insert student profile directly
    INSERT INTO public.intermediate_student_profiles (
        id, username, first_name, last_name, student_name, roll_number, registration_number, program, program_name, password_hash, created_at
    ) VALUES (
        v_new_id, v_clean_username, v_clean_first, v_clean_last, v_full_name, v_clean_roll, v_clean_reg, v_clean_program, v_clean_program, v_pwd_hash, NOW()
    )
    RETURNING id, username, first_name, last_name, roll_number, registration_number, COALESCE(program, program_name) AS program INTO v_profile;

    -- Assign user role if table exists
    BEGIN
        INSERT INTO public.user_roles (user_id, role, department)
        VALUES (v_new_id, 'student_intermediate'::public.app_role, v_clean_program)
        ON CONFLICT (user_id) DO NOTHING;
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;

    RETURN jsonb_build_object(
        'success', true,
        'message', 'Intermediate account created successfully!',
        'profile', jsonb_build_object(
            'id', v_profile.id,
            'username', v_profile.username,
            'first_name', v_profile.first_name,
            'last_name', v_profile.last_name,
            'roll_number', v_profile.roll_number,
            'registration_number', v_profile.registration_number,
            'program', v_profile.program
        )
    );
END;
$$;

CREATE OR REPLACE FUNCTION public.direct_login_intermediate_student(
    p_identifier TEXT,
    p_password TEXT
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, extensions
AS $$
DECLARE
    v_clean_id TEXT := TRIM(COALESCE(p_identifier, ''));
    v_profile RECORD;
    v_is_valid BOOLEAN := FALSE;
BEGIN
    IF v_clean_id = '' OR COALESCE(p_password, '') = '' THEN
        RETURN jsonb_build_object('success', false, 'error', 'Username/Roll Number and Password are required.');
    END IF;

    -- Find by username OR roll number
    SELECT *, COALESCE(program, program_name) AS user_program, COALESCE(first_name, student_name) AS user_first_name 
    INTO v_profile FROM public.intermediate_student_profiles
    WHERE LOWER(TRIM(username)) = LOWER(v_clean_id)
       OR UPPER(TRIM(roll_number)) = UPPER(v_clean_id);

    IF NOT FOUND THEN
        RETURN jsonb_build_object('success', false, 'error', 'Invalid username, roll number, or password.');
    END IF;

    -- Verify password
    IF v_profile.password_hash IS NOT NULL THEN
        BEGIN
            IF v_profile.password_hash = crypt(p_password, v_profile.password_hash) THEN
                v_is_valid := TRUE;
            ELSIF v_profile.password_hash = md5(p_password || 'ggc_salt_2026') THEN
                v_is_valid := TRUE;
            ELSIF v_profile.password_hash = p_password THEN
                v_is_valid := TRUE;
            END IF;
        EXCEPTION WHEN OTHERS THEN
            IF v_profile.password_hash = md5(p_password || 'ggc_salt_2026') OR v_profile.password_hash = p_password THEN
                v_is_valid := TRUE;
            END IF;
        END;

        IF NOT v_is_valid THEN
            RETURN jsonb_build_object('success', false, 'error', 'Incorrect password. Please try again.');
        END IF;
    END IF;

    RETURN jsonb_build_object(
        'success', true,
        'message', 'Login successful!',
        'profile', jsonb_build_object(
            'id', v_profile.id,
            'username', v_profile.username,
            'first_name', COALESCE(v_profile.first_name, v_profile.student_name),
            'last_name', COALESCE(v_profile.last_name, ''),
            'roll_number', v_profile.roll_number,
            'registration_number', v_profile.registration_number,
            'program', COALESCE(v_profile.program, v_profile.program_name)
        )
    );
END;
$$;

-- ==============================================================================
-- 4. BS STUDENT DIRECT AUTHENTICATION
-- ==============================================================================

CREATE OR REPLACE FUNCTION public.direct_register_bs_student(
    p_roll_number TEXT,
    p_registration_number TEXT,
    p_program_name TEXT,
    p_semester TEXT,
    p_username TEXT,
    p_first_name TEXT,
    p_last_name TEXT,
    p_password TEXT
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, extensions
AS $$
DECLARE
    v_clean_roll TEXT := UPPER(TRIM(COALESCE(p_roll_number, '')));
    v_clean_reg TEXT := UPPER(TRIM(COALESCE(p_registration_number, '')));
    v_clean_username TEXT := LOWER(TRIM(COALESCE(p_username, '')));
    v_clean_program TEXT := TRIM(COALESCE(p_program_name, ''));
    v_clean_semester TEXT := TRIM(COALESCE(p_semester, 'Semester 1'));
    v_clean_first TEXT := TRIM(COALESCE(p_first_name, ''));
    v_clean_last TEXT := TRIM(COALESCE(p_last_name, ''));
    v_full_name TEXT := TRIM(v_clean_first || ' ' || v_clean_last);
    v_new_id UUID := gen_random_uuid();
    v_pwd_hash TEXT;
    v_profile RECORD;
BEGIN
    IF v_clean_roll = '' OR v_clean_reg = '' OR v_clean_username = '' THEN
        RETURN jsonb_build_object('success', false, 'error', 'Roll Number, Registration Number, and Username are required.');
    END IF;

    IF LENGTH(v_clean_username) < 3 THEN
        RETURN jsonb_build_object('success', false, 'error', 'Username must be at least 3 characters.');
    END IF;

    IF COALESCE(p_password, '') = '' OR LENGTH(p_password) < 6 THEN
        RETURN jsonb_build_object('success', false, 'error', 'Password must be at least 6 characters.');
    END IF;

    IF EXISTS (
        SELECT 1 FROM public.bs_student_profiles 
        WHERE LOWER(TRIM(username)) = v_clean_username
    ) THEN
        RETURN jsonb_build_object('success', false, 'error', 'The username "' || p_username || '" is already taken.');
    END IF;

    IF EXISTS (
        SELECT 1 FROM public.bs_student_profiles 
        WHERE UPPER(TRIM(roll_number)) = v_clean_roll
    ) THEN
        RETURN jsonb_build_object('success', false, 'error', 'College Roll Number "' || p_roll_number || '" is already registered.');
    END IF;

    IF EXISTS (
        SELECT 1 FROM public.bs_student_profiles 
        WHERE UPPER(TRIM(registration_number)) = v_clean_reg
    ) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Registration Number "' || p_registration_number || '" is already registered.');
    END IF;

    BEGIN
        v_pwd_hash := crypt(p_password, gen_salt('bf'::text));
    EXCEPTION WHEN OTHERS THEN
        v_pwd_hash := md5(p_password || 'ggc_salt_2026');
    END;

    INSERT INTO public.bs_student_profiles (
        id, username, first_name, last_name, student_name, roll_number, registration_number, program, program_name, semester, password_hash, created_at
    ) VALUES (
        v_new_id, v_clean_username, v_clean_first, v_clean_last, v_full_name, v_clean_roll, v_clean_reg, v_clean_program, v_clean_program, v_clean_semester, v_pwd_hash, NOW()
    )
    RETURNING id, username, first_name, last_name, roll_number, registration_number, COALESCE(program, program_name) AS program, semester INTO v_profile;

    BEGIN
        INSERT INTO public.user_roles (user_id, role, department)
        VALUES (v_new_id, 'student_bs'::public.app_role, v_clean_program)
        ON CONFLICT (user_id) DO NOTHING;
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;

    RETURN jsonb_build_object(
        'success', true,
        'message', 'BS student account created successfully!',
        'profile', jsonb_build_object(
            'id', v_profile.id,
            'username', v_profile.username,
            'first_name', v_profile.first_name,
            'last_name', v_profile.last_name,
            'roll_number', v_profile.roll_number,
            'registration_number', v_profile.registration_number,
            'program', v_profile.program,
            'semester', v_profile.semester
        )
    );
END;
$$;

CREATE OR REPLACE FUNCTION public.direct_login_bs_student(
    p_identifier TEXT,
    p_password TEXT
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, extensions
AS $$
DECLARE
    v_clean_id TEXT := TRIM(COALESCE(p_identifier, ''));
    v_profile RECORD;
    v_is_valid BOOLEAN := FALSE;
BEGIN
    IF v_clean_id = '' OR COALESCE(p_password, '') = '' THEN
        RETURN jsonb_build_object('success', false, 'error', 'Username/Roll Number and Password are required.');
    END IF;

    SELECT *, COALESCE(program, program_name) AS user_program, COALESCE(first_name, student_name) AS user_first_name 
    INTO v_profile FROM public.bs_student_profiles
    WHERE LOWER(TRIM(username)) = LOWER(v_clean_id)
       OR UPPER(TRIM(roll_number)) = UPPER(v_clean_id);

    IF NOT FOUND THEN
        RETURN jsonb_build_object('success', false, 'error', 'Invalid username, roll number, or password.');
    END IF;

    IF v_profile.password_hash IS NOT NULL THEN
        BEGIN
            IF v_profile.password_hash = crypt(p_password, v_profile.password_hash) THEN
                v_is_valid := TRUE;
            ELSIF v_profile.password_hash = md5(p_password || 'ggc_salt_2026') THEN
                v_is_valid := TRUE;
            ELSIF v_profile.password_hash = p_password THEN
                v_is_valid := TRUE;
            END IF;
        EXCEPTION WHEN OTHERS THEN
            IF v_profile.password_hash = md5(p_password || 'ggc_salt_2026') OR v_profile.password_hash = p_password THEN
                v_is_valid := TRUE;
            END IF;
        END;

        IF NOT v_is_valid THEN
            RETURN jsonb_build_object('success', false, 'error', 'Incorrect password.');
        END IF;
    END IF;

    RETURN jsonb_build_object(
        'success', true,
        'message', 'Login successful!',
        'profile', jsonb_build_object(
            'id', v_profile.id,
            'username', v_profile.username,
            'first_name', COALESCE(v_profile.first_name, v_profile.student_name),
            'last_name', COALESCE(v_profile.last_name, ''),
            'roll_number', v_profile.roll_number,
            'registration_number', v_profile.registration_number,
            'program', COALESCE(v_profile.program, v_profile.program_name),
            'semester', COALESCE(v_profile.semester, 'Semester 1')
        )
    );
END;
$$;

-- ==============================================================================
-- 5. FACULTY DIRECT AUTHENTICATION
-- ==============================================================================

CREATE OR REPLACE FUNCTION public.direct_register_faculty(
    p_faculty_id TEXT,
    p_department TEXT,
    p_designation TEXT,
    p_qualification TEXT,
    p_username TEXT,
    p_full_name TEXT,
    p_password TEXT
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, extensions
AS $$
DECLARE
    v_clean_faculty_id TEXT := UPPER(TRIM(COALESCE(p_faculty_id, '')));
    v_clean_dept TEXT := TRIM(COALESCE(p_department, ''));
    v_clean_desig TEXT := TRIM(COALESCE(p_designation, ''));
    v_clean_qual TEXT := TRIM(COALESCE(p_qualification, ''));
    v_clean_username TEXT := LOWER(TRIM(COALESCE(p_username, '')));
    v_clean_name TEXT := TRIM(COALESCE(p_full_name, ''));
    v_new_id UUID := gen_random_uuid();
    v_pwd_hash TEXT;
    v_profile RECORD;
BEGIN
    IF v_clean_faculty_id = '' OR v_clean_username = '' THEN
        RETURN jsonb_build_object('success', false, 'error', 'Faculty ID and Username are required.');
    END IF;

    IF LENGTH(v_clean_username) < 3 THEN
        RETURN jsonb_build_object('success', false, 'error', 'Username must be at least 3 characters.');
    END IF;

    IF COALESCE(p_password, '') = '' OR LENGTH(p_password) < 6 THEN
        RETURN jsonb_build_object('success', false, 'error', 'Password must be at least 6 characters.');
    END IF;

    IF EXISTS (
        SELECT 1 FROM public.faculty_profiles 
        WHERE LOWER(TRIM(username)) = v_clean_username
    ) THEN
        RETURN jsonb_build_object('success', false, 'error', 'The username "' || p_username || '" is already taken.');
    END IF;

    IF EXISTS (
        SELECT 1 FROM public.faculty_profiles 
        WHERE UPPER(TRIM(faculty_id)) = v_clean_faculty_id
    ) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Faculty ID "' || p_faculty_id || '" is already registered.');
    END IF;

    BEGIN
        v_pwd_hash := crypt(p_password, gen_salt('bf'::text));
    EXCEPTION WHEN OTHERS THEN
        v_pwd_hash := md5(p_password || 'ggc_salt_2026');
    END;

    INSERT INTO public.faculty_profiles (
        id, username, full_name, faculty_id, department, designation, qualification, password_hash, created_at
    ) VALUES (
        v_new_id, v_clean_username, v_clean_name, v_clean_faculty_id, v_clean_dept, v_clean_desig, v_clean_qual, v_pwd_hash, NOW()
    )
    RETURNING id, username, full_name, faculty_id, department, designation, qualification INTO v_profile;

    BEGIN
        INSERT INTO public.user_roles (user_id, role, department)
        VALUES (v_new_id, 'teacher'::public.app_role, v_clean_dept)
        ON CONFLICT (user_id) DO NOTHING;
    EXCEPTION WHEN OTHERS THEN
        NULL;
    END;

    RETURN jsonb_build_object(
        'success', true,
        'message', 'Faculty account created successfully!',
        'profile', jsonb_build_object(
            'id', v_profile.id,
            'username', v_profile.username,
            'full_name', v_profile.full_name,
            'faculty_id', v_profile.faculty_id,
            'department', v_profile.department,
            'designation', v_profile.designation,
            'qualification', v_profile.qualification
        )
    );
END;
$$;

CREATE OR REPLACE FUNCTION public.direct_login_faculty(
    p_identifier TEXT,
    p_password TEXT
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, extensions
AS $$
DECLARE
    v_clean_id TEXT := TRIM(COALESCE(p_identifier, ''));
    v_profile RECORD;
    v_is_valid BOOLEAN := FALSE;
BEGIN
    IF v_clean_id = '' OR COALESCE(p_password, '') = '' THEN
        RETURN jsonb_build_object('success', false, 'error', 'Username/Faculty ID and Password are required.');
    END IF;

    SELECT * INTO v_profile FROM public.faculty_profiles
    WHERE LOWER(TRIM(username)) = LOWER(v_clean_id)
       OR UPPER(TRIM(faculty_id)) = UPPER(v_clean_id);

    IF NOT FOUND THEN
        RETURN jsonb_build_object('success', false, 'error', 'Invalid username, faculty ID, or password.');
    END IF;

    IF v_profile.password_hash IS NOT NULL THEN
        BEGIN
            IF v_profile.password_hash = crypt(p_password, v_profile.password_hash) THEN
                v_is_valid := TRUE;
            ELSIF v_profile.password_hash = md5(p_password || 'ggc_salt_2026') THEN
                v_is_valid := TRUE;
            ELSIF v_profile.password_hash = p_password THEN
                v_is_valid := TRUE;
            END IF;
        EXCEPTION WHEN OTHERS THEN
            IF v_profile.password_hash = md5(p_password || 'ggc_salt_2026') OR v_profile.password_hash = p_password THEN
                v_is_valid := TRUE;
            END IF;
        END;

        IF NOT v_is_valid THEN
            RETURN jsonb_build_object('success', false, 'error', 'Incorrect password.');
        END IF;
    END IF;

    RETURN jsonb_build_object(
        'success', true,
        'message', 'Login successful!',
        'profile', jsonb_build_object(
            'id', v_profile.id,
            'username', v_profile.username,
            'full_name', v_profile.full_name,
            'faculty_id', v_profile.faculty_id,
            'department', v_profile.department,
            'designation', v_profile.designation,
            'qualification', v_profile.qualification
        )
    );
END;
$$;

-- ==============================================================================
-- 6. GRANT PERMISSIONS TO ANON, AUTHENTICATED, AND SERVICE_ROLE
-- ==============================================================================

GRANT EXECUTE ON FUNCTION public.check_intermediate_username_available(TEXT) TO anon, authenticated, service_role;
GRANT EXECUTE ON FUNCTION public.check_bs_username_available(TEXT) TO anon, authenticated, service_role;
GRANT EXECUTE ON FUNCTION public.check_faculty_username_available(TEXT) TO anon, authenticated, service_role;

GRANT EXECUTE ON FUNCTION public.direct_register_intermediate_student(TEXT, TEXT, TEXT, TEXT, TEXT, TEXT, TEXT) TO anon, authenticated, service_role;
GRANT EXECUTE ON FUNCTION public.direct_login_intermediate_student(TEXT, TEXT) TO anon, authenticated, service_role;

GRANT EXECUTE ON FUNCTION public.direct_register_bs_student(TEXT, TEXT, TEXT, TEXT, TEXT, TEXT, TEXT, TEXT) TO anon, authenticated, service_role;
GRANT EXECUTE ON FUNCTION public.direct_login_bs_student(TEXT, TEXT) TO anon, authenticated, service_role;

GRANT EXECUTE ON FUNCTION public.direct_register_faculty(TEXT, TEXT, TEXT, TEXT, TEXT, TEXT, TEXT) TO anon, authenticated, service_role;
GRANT EXECUTE ON FUNCTION public.direct_login_faculty(TEXT, TEXT) TO anon, authenticated, service_role;
