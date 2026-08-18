-- ==============================================================================
-- GOVT. GRADUATE COLLEGE MANDI BAHAUDDIN (GGC M.B.DIN) - OFFICIAL APP
-- COMPLETE SUPABASE MASTER BACKEND SETUP (ONE-CLICK DEPLOYMENT SCRIPT)
-- ==============================================================================
-- Instructions:
-- 1. Open your Supabase Dashboard: https://supabase.com/dashboard/project/mhiudbdnrooipovvonfb/sql
-- 2. Click "New query", paste this entire script, and click "RUN" (or Ctrl+Enter).
-- ==============================================================================

-- ==============================================================================
-- 1. EXTENSIONS
-- ==============================================================================
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ==============================================================================
-- 2. ENUM TYPES
-- ==============================================================================
DO $$ BEGIN
    CREATE TYPE public.app_role AS ENUM (
        'student_bs',
        'student_intermediate',
        'teacher',
        'hod',
        'admin'
    );
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- ==============================================================================
-- 3. UNIFIED RBAC & ROLES TABLE
-- ==============================================================================
CREATE TABLE IF NOT EXISTS public.user_roles (
    user_id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    role public.app_role NOT NULL,
    department TEXT,
    assigned_by UUID REFERENCES auth.users(id) ON DELETE SET NULL,
    assigned_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_user_roles_user_role ON public.user_roles (user_id, role);
CREATE INDEX IF NOT EXISTS idx_user_roles_dept ON public.user_roles (department) WHERE department IS NOT NULL;
ALTER TABLE public.user_roles ENABLE ROW LEVEL SECURITY;

-- Helper security functions
CREATE OR REPLACE FUNCTION public.get_auth_user_role(p_user_id UUID DEFAULT auth.uid())
RETURNS public.app_role
LANGUAGE plpgsql STABLE SECURITY DEFINER SET search_path = public
AS $$
DECLARE v_role public.app_role;
BEGIN
    IF p_user_id IS NULL THEN RETURN NULL; END IF;
    SELECT role INTO v_role FROM public.user_roles WHERE user_id = p_user_id;
    RETURN v_role;
END;
$$;

CREATE OR REPLACE FUNCTION public.get_auth_user_department(p_user_id UUID DEFAULT auth.uid())
RETURNS TEXT
LANGUAGE plpgsql STABLE SECURITY DEFINER SET search_path = public
AS $$
DECLARE v_dept TEXT;
BEGIN
    IF p_user_id IS NULL THEN RETURN NULL; END IF;
    SELECT department INTO v_dept FROM public.user_roles WHERE user_id = p_user_id;
    RETURN v_dept;
END;
$$;

CREATE OR REPLACE FUNCTION public.is_admin(p_user_id UUID DEFAULT auth.uid())
RETURNS BOOLEAN
LANGUAGE plpgsql STABLE SECURITY DEFINER SET search_path = public
AS $$
BEGIN
    IF p_user_id IS NULL THEN RETURN FALSE; END IF;
    RETURN EXISTS (SELECT 1 FROM public.user_roles WHERE user_id = p_user_id AND role = 'admin'::public.app_role);
END;
$$;

CREATE OR REPLACE FUNCTION public.is_hod(p_user_id UUID DEFAULT auth.uid(), p_department TEXT DEFAULT NULL)
RETURNS BOOLEAN
LANGUAGE plpgsql STABLE SECURITY DEFINER SET search_path = public
AS $$
DECLARE v_role public.app_role; v_dept TEXT;
BEGIN
    IF p_user_id IS NULL THEN RETURN FALSE; END IF;
    SELECT role, department INTO v_role, v_dept FROM public.user_roles WHERE user_id = p_user_id;
    IF v_role = 'admin'::public.app_role THEN RETURN TRUE; END IF;
    IF v_role = 'hod'::public.app_role THEN
        IF p_department IS NULL OR p_department = '' THEN RETURN TRUE; END IF;
        RETURN LOWER(TRIM(COALESCE(v_dept, ''))) = LOWER(TRIM(p_department));
    END IF;
    RETURN FALSE;
END;
$$;

CREATE OR REPLACE FUNCTION public.is_teacher(p_user_id UUID DEFAULT auth.uid())
RETURNS BOOLEAN
LANGUAGE plpgsql STABLE SECURITY DEFINER SET search_path = public
AS $$
BEGIN
    IF p_user_id IS NULL THEN RETURN FALSE; END IF;
    RETURN EXISTS (SELECT 1 FROM public.user_roles WHERE user_id = p_user_id AND role IN ('teacher'::public.app_role, 'hod'::public.app_role, 'admin'::public.app_role));
END;
$$;

-- ==============================================================================
-- 4. OFFICIAL REGISTRIES TABLES
-- ==============================================================================

-- 4.1 BS Student Registry
CREATE TABLE IF NOT EXISTS public.official_bs_students (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    roll_number TEXT NOT NULL,
    registration_number TEXT NOT NULL,
    student_name TEXT NOT NULL,
    father_name TEXT,
    program_name TEXT NOT NULL,
    session_year TEXT NOT NULL,
    semester_number INTEGER DEFAULT 1,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    is_claimed BOOLEAN DEFAULT FALSE NOT NULL,
    claimed_by_user_id UUID REFERENCES auth.users(id) ON DELETE SET NULL,
    claimed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS idx_official_bs_roll_lower ON public.official_bs_students (LOWER(TRIM(roll_number)));
CREATE UNIQUE INDEX IF NOT EXISTS idx_official_bs_reg_lower ON public.official_bs_students (LOWER(TRIM(registration_number)));
CREATE INDEX IF NOT EXISTS idx_official_bs_program ON public.official_bs_students (program_name);
ALTER TABLE public.official_bs_students ENABLE ROW LEVEL SECURITY;

-- 4.2 Intermediate Student Registry
CREATE TABLE IF NOT EXISTS public.official_intermediate_students (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    roll_number TEXT NOT NULL,
    registration_number TEXT NOT NULL,
    student_name TEXT NOT NULL,
    father_name TEXT,
    program_name TEXT NOT NULL,
    session_year TEXT NOT NULL,
    part_number INTEGER DEFAULT 1,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    is_claimed BOOLEAN DEFAULT FALSE NOT NULL,
    claimed_by_user_id UUID REFERENCES auth.users(id) ON DELETE SET NULL,
    claimed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS idx_official_inter_roll_lower ON public.official_intermediate_students (LOWER(TRIM(roll_number)));
CREATE UNIQUE INDEX IF NOT EXISTS idx_official_inter_reg_lower ON public.official_intermediate_students (LOWER(TRIM(registration_number)));
CREATE INDEX IF NOT EXISTS idx_official_inter_program ON public.official_intermediate_students (program_name);
ALTER TABLE public.official_intermediate_students ENABLE ROW LEVEL SECURITY;

-- 4.3 Faculty Registry
CREATE TABLE IF NOT EXISTS public.official_faculty (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    faculty_id TEXT NOT NULL,
    full_name TEXT NOT NULL,
    department TEXT NOT NULL,
    designation TEXT NOT NULL,
    institutional_email TEXT NOT NULL,
    phone_number TEXT,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    is_claimed BOOLEAN DEFAULT FALSE NOT NULL,
    claimed_by_user_id UUID REFERENCES auth.users(id) ON DELETE SET NULL,
    claimed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS idx_official_faculty_id_lower ON public.official_faculty (LOWER(TRIM(faculty_id)));
CREATE UNIQUE INDEX IF NOT EXISTS idx_official_faculty_email_lower ON public.official_faculty (LOWER(TRIM(institutional_email)));
CREATE INDEX IF NOT EXISTS idx_official_faculty_dept ON public.official_faculty (department);
ALTER TABLE public.official_faculty ENABLE ROW LEVEL SECURITY;

-- ==============================================================================
-- 5. USER PROFILES TABLES
-- ==============================================================================

-- 5.1 BS Student Profiles
CREATE TABLE IF NOT EXISTS public.bs_student_profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    official_record_id UUID REFERENCES public.official_bs_students(id) ON DELETE RESTRICT,
    username TEXT UNIQUE NOT NULL,
    roll_number TEXT UNIQUE NOT NULL,
    registration_number TEXT UNIQUE NOT NULL,
    first_name TEXT NOT NULL,
    last_name TEXT,
    student_name TEXT NOT NULL,
    program_name TEXT NOT NULL,
    semester_number INTEGER DEFAULT 1,
    session_year TEXT,
    profile_photo_url TEXT,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);
ALTER TABLE public.bs_student_profiles ENABLE ROW LEVEL SECURITY;

-- 5.2 Intermediate Student Profiles
CREATE TABLE IF NOT EXISTS public.intermediate_student_profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    official_record_id UUID REFERENCES public.official_intermediate_students(id) ON DELETE RESTRICT,
    username TEXT UNIQUE NOT NULL,
    roll_number TEXT UNIQUE NOT NULL,
    registration_number TEXT UNIQUE NOT NULL,
    first_name TEXT NOT NULL,
    last_name TEXT,
    student_name TEXT NOT NULL,
    program_name TEXT NOT NULL,
    part_number INTEGER DEFAULT 1,
    session_year TEXT,
    profile_photo_url TEXT,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);
ALTER TABLE public.intermediate_student_profiles ENABLE ROW LEVEL SECURITY;

-- 5.3 Faculty Profiles
CREATE TABLE IF NOT EXISTS public.faculty_profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    official_record_id UUID REFERENCES public.official_faculty(id) ON DELETE RESTRICT,
    username TEXT UNIQUE NOT NULL,
    faculty_id TEXT UNIQUE NOT NULL,
    full_name TEXT NOT NULL,
    department TEXT NOT NULL,
    designation TEXT NOT NULL,
    institutional_email TEXT UNIQUE NOT NULL,
    phone_number TEXT,
    qualification TEXT,
    specialization TEXT,
    profile_photo_url TEXT,
    is_hod BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);
ALTER TABLE public.faculty_profiles ENABLE ROW LEVEL SECURITY;

-- ==============================================================================
-- 6. AUTHENTICATION & ACCOUNT CLAIMING RPCs
-- ==============================================================================

-- 6.1 BS Student Eligibility Check
CREATE OR REPLACE FUNCTION public.check_bs_student_eligibility(
    p_roll_number TEXT,
    p_registration_number TEXT,
    p_program_name TEXT,
    p_username TEXT
)
RETURNS JSONB
LANGUAGE plpgsql SECURITY DEFINER SET search_path = public
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

    SELECT EXISTS(SELECT 1 FROM public.bs_student_profiles WHERE LOWER(TRIM(username)) = LOWER(TRIM(p_username))) INTO v_username_exists;
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

-- 6.2 Claim BS Student Account
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
LANGUAGE plpgsql SECURITY DEFINER SET search_path = public
AS $$
DECLARE
    v_user_id UUID;
    v_record RECORD;
BEGIN
    v_user_id := auth.uid();
    IF v_user_id IS NULL THEN
        RETURN jsonb_build_object('success', false, 'error', 'Not authenticated.');
    END IF;

    SELECT * INTO v_record FROM public.official_bs_students
    WHERE LOWER(TRIM(roll_number)) = LOWER(TRIM(p_roll_number))
      AND LOWER(TRIM(registration_number)) = LOWER(TRIM(p_registration_number))
    FOR UPDATE;

    IF NOT FOUND THEN
        RETURN jsonb_build_object('success', false, 'error', 'Official record not found.');
    END IF;

    IF v_record.is_claimed THEN
        RETURN jsonb_build_object('success', false, 'error', 'Record is already claimed.');
    END IF;

    -- Update registry
    UPDATE public.official_bs_students
    SET is_claimed = TRUE, claimed_by_user_id = v_user_id, claimed_at = now(), updated_at = now()
    WHERE id = v_record.id;

    -- Insert Profile
    INSERT INTO public.bs_student_profiles (
        id, official_record_id, username, roll_number, registration_number,
        first_name, last_name, student_name, program_name, semester_number, session_year
    ) VALUES (
        v_user_id, v_record.id, LOWER(TRIM(p_username)), v_record.roll_number, v_record.registration_number,
        p_first_name, p_last_name, v_record.student_name, v_record.program_name, COALESCE(p_semester_number, v_record.semester_number), v_record.session_year
    );

    -- Assign Role
    INSERT INTO public.user_roles (user_id, role, department)
    VALUES (v_user_id, 'student_bs'::public.app_role, v_record.program_name)
    ON CONFLICT (user_id) DO UPDATE SET role = 'student_bs'::public.app_role, department = v_record.program_name;

    RETURN jsonb_build_object('success', true, 'student_name', v_record.student_name, 'role', 'student_bs');
END;
$$;

-- 6.3 Intermediate Student Eligibility Check
CREATE OR REPLACE FUNCTION public.check_intermediate_student_eligibility(
    p_roll_number TEXT,
    p_registration_number TEXT,
    p_program_name TEXT,
    p_username TEXT
)
RETURNS JSONB
LANGUAGE plpgsql SECURITY DEFINER SET search_path = public
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

    SELECT EXISTS(SELECT 1 FROM public.intermediate_student_profiles WHERE LOWER(TRIM(username)) = LOWER(TRIM(p_username))) INTO v_username_exists;
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

-- 6.4 Claim Intermediate Student Account
CREATE OR REPLACE FUNCTION public.claim_intermediate_student_account(
    p_roll_number TEXT,
    p_registration_number TEXT,
    p_program_name TEXT,
    p_username TEXT,
    p_first_name TEXT,
    p_last_name TEXT
)
RETURNS JSONB
LANGUAGE plpgsql SECURITY DEFINER SET search_path = public
AS $$
DECLARE
    v_user_id UUID;
    v_record RECORD;
BEGIN
    v_user_id := auth.uid();
    IF v_user_id IS NULL THEN
        RETURN jsonb_build_object('success', false, 'error', 'Not authenticated.');
    END IF;

    SELECT * INTO v_record FROM public.official_intermediate_students
    WHERE LOWER(TRIM(roll_number)) = LOWER(TRIM(p_roll_number))
      AND LOWER(TRIM(registration_number)) = LOWER(TRIM(p_registration_number))
    FOR UPDATE;

    IF NOT FOUND THEN
        RETURN jsonb_build_object('success', false, 'error', 'Official record not found.');
    END IF;

    IF v_record.is_claimed THEN
        RETURN jsonb_build_object('success', false, 'error', 'Record is already claimed.');
    END IF;

    -- Update registry
    UPDATE public.official_intermediate_students
    SET is_claimed = TRUE, claimed_by_user_id = v_user_id, claimed_at = now(), updated_at = now()
    WHERE id = v_record.id;

    -- Insert Profile
    INSERT INTO public.intermediate_student_profiles (
        id, official_record_id, username, roll_number, registration_number,
        first_name, last_name, student_name, program_name, part_number, session_year
    ) VALUES (
        v_user_id, v_record.id, LOWER(TRIM(p_username)), v_record.roll_number, v_record.registration_number,
        p_first_name, p_last_name, v_record.student_name, v_record.program_name, v_record.part_number, v_record.session_year
    );

    -- Assign Role
    INSERT INTO public.user_roles (user_id, role, department)
    VALUES (v_user_id, 'student_intermediate'::public.app_role, v_record.program_name)
    ON CONFLICT (user_id) DO UPDATE SET role = 'student_intermediate'::public.app_role, department = v_record.program_name;

    RETURN jsonb_build_object('success', true, 'student_name', v_record.student_name, 'role', 'student_intermediate');
END;
$$;

-- 6.5 Faculty Eligibility Check
CREATE OR REPLACE FUNCTION public.check_faculty_eligibility(
    p_faculty_id TEXT,
    p_department TEXT,
    p_username TEXT,
    p_institutional_email TEXT
)
RETURNS JSONB
LANGUAGE plpgsql SECURITY DEFINER SET search_path = public
AS $$
DECLARE
    v_record RECORD;
    v_username_exists BOOLEAN;
BEGIN
    SELECT * INTO v_record FROM public.official_faculty
    WHERE LOWER(TRIM(faculty_id)) = LOWER(TRIM(p_faculty_id))
      AND LOWER(TRIM(institutional_email)) = LOWER(TRIM(p_institutional_email));

    IF NOT FOUND THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'No official faculty record matches this Faculty ID and Institutional Email.');
    END IF;

    IF NOT v_record.is_active THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'This faculty record is marked inactive.');
    END IF;

    IF v_record.is_claimed THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'An account has already been claimed for this faculty member.');
    END IF;

    SELECT EXISTS(SELECT 1 FROM public.faculty_profiles WHERE LOWER(TRIM(username)) = LOWER(TRIM(p_username))) INTO v_username_exists;
    IF v_username_exists THEN
        RETURN jsonb_build_object('eligible', false, 'error', 'Username is already taken.');
    END IF;

    RETURN jsonb_build_object(
        'eligible', true,
        'official_id', v_record.id,
        'full_name', v_record.full_name,
        'department', v_record.department,
        'designation', v_record.designation,
        'institutional_email', v_record.institutional_email
    );
END;
$$;

-- 6.6 Claim Faculty Account
CREATE OR REPLACE FUNCTION public.claim_faculty_account(
    p_faculty_id TEXT,
    p_department TEXT,
    p_username TEXT,
    p_phone_number TEXT DEFAULT NULL
)
RETURNS JSONB
LANGUAGE plpgsql SECURITY DEFINER SET search_path = public
AS $$
DECLARE
    v_user_id UUID;
    v_record RECORD;
    v_role public.app_role;
BEGIN
    v_user_id := auth.uid();
    IF v_user_id IS NULL THEN
        RETURN jsonb_build_object('success', false, 'error', 'Not authenticated.');
    END IF;

    SELECT * INTO v_record FROM public.official_faculty
    WHERE LOWER(TRIM(faculty_id)) = LOWER(TRIM(p_faculty_id))
    FOR UPDATE;

    IF NOT FOUND THEN
        RETURN jsonb_build_object('success', false, 'error', 'Official faculty record not found.');
    END IF;

    IF v_record.is_claimed THEN
        RETURN jsonb_build_object('success', false, 'error', 'Record is already claimed.');
    END IF;

    -- Update registry
    UPDATE public.official_faculty
    SET is_claimed = TRUE, claimed_by_user_id = v_user_id, claimed_at = now(), updated_at = now()
    WHERE id = v_record.id;

    -- Insert Profile
    INSERT INTO public.faculty_profiles (
        id, official_record_id, username, faculty_id, full_name,
        department, designation, institutional_email, phone_number
    ) VALUES (
        v_user_id, v_record.id, LOWER(TRIM(p_username)), v_record.faculty_id, v_record.full_name,
        v_record.department, v_record.designation, v_record.institutional_email, COALESCE(p_phone_number, v_record.phone_number)
    );

    v_role := CASE
        WHEN LOWER(v_record.designation) LIKE '%head%' OR LOWER(v_record.designation) LIKE '%hod%' THEN 'hod'::public.app_role
        ELSE 'teacher'::public.app_role
    END;

    -- Assign Role
    INSERT INTO public.user_roles (user_id, role, department)
    VALUES (v_user_id, v_role, v_record.department)
    ON CONFLICT (user_id) DO UPDATE SET role = v_role, department = v_record.department;

    RETURN jsonb_build_object('success', true, 'full_name', v_record.full_name, 'role', v_role::TEXT);
END;
$$;

-- ==============================================================================
-- 7. COLLEGE ACADEMIC & CONTENT TABLES
-- ==============================================================================

-- 7.1 Departments
CREATE TABLE IF NOT EXISTS public.departments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL UNIQUE,
    code TEXT NOT NULL UNIQUE,
    description TEXT,
    hod_id UUID REFERENCES auth.users(id) ON DELETE SET NULL,
    hod_name TEXT,
    contact_email TEXT,
    contact_phone TEXT,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);
ALTER TABLE public.departments ENABLE ROW LEVEL SECURITY;

-- 7.2 Academic Programs
CREATE TABLE IF NOT EXISTS public.academic_programs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    department_id UUID REFERENCES public.departments(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    code TEXT NOT NULL,
    level TEXT NOT NULL, -- 'BS', 'Intermediate', 'Postgraduate'
    duration_years INTEGER DEFAULT 4,
    total_semesters INTEGER DEFAULT 8,
    is_published BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    CONSTRAINT uq_dept_program_code UNIQUE (department_id, code)
);
ALTER TABLE public.academic_programs ENABLE ROW LEVEL SECURITY;

-- 7.3 Courses
CREATE TABLE IF NOT EXISTS public.courses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    program_id UUID REFERENCES public.academic_programs(id) ON DELETE CASCADE,
    department_id UUID REFERENCES public.departments(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    code TEXT NOT NULL,
    credit_hours INTEGER DEFAULT 3,
    semester_number INTEGER DEFAULT 1,
    description TEXT,
    is_published BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    CONSTRAINT uq_course_program_sem UNIQUE (program_id, code, semester_number)
);
ALTER TABLE public.courses ENABLE ROW LEVEL SECURITY;

-- 7.4 Course Outlines
CREATE TABLE IF NOT EXISTS public.course_outlines (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    course_id UUID REFERENCES public.courses(id) ON DELETE CASCADE,
    program_id UUID REFERENCES public.academic_programs(id) ON DELETE CASCADE,
    department_id UUID REFERENCES public.departments(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    session_year TEXT,
    semester_number INTEGER DEFAULT 1,
    outline_content TEXT,
    storage_path TEXT,
    file_name TEXT,
    file_size_bytes BIGINT,
    mime_type TEXT DEFAULT 'application/pdf',
    is_published BOOLEAN DEFAULT TRUE NOT NULL,
    created_by UUID REFERENCES auth.users(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);
ALTER TABLE public.course_outlines ENABLE ROW LEVEL SECURITY;

-- 7.5 Announcements / Notices
CREATE TABLE IF NOT EXISTS public.announcements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    category TEXT DEFAULT 'General',
    department_id UUID REFERENCES public.departments(id) ON DELETE SET NULL,
    author_id UUID REFERENCES auth.users(id) ON DELETE SET NULL,
    author_name TEXT DEFAULT 'College Administration',
    is_pinned BOOLEAN DEFAULT FALSE NOT NULL,
    is_published BOOLEAN DEFAULT TRUE NOT NULL,
    published_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()),
    attachment_storage_path TEXT,
    attachment_name TEXT,
    attachment_size_bytes BIGINT,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_announcements_published ON public.announcements (is_published, published_at DESC);
ALTER TABLE public.announcements ENABLE ROW LEVEL SECURITY;

-- 7.6 College Events
CREATE TABLE IF NOT EXISTS public.college_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    event_date TEXT NOT NULL,
    event_time TEXT,
    venue TEXT DEFAULT 'College Auditorium',
    category TEXT DEFAULT 'College',
    department_id UUID REFERENCES public.departments(id) ON DELETE SET NULL,
    is_upcoming BOOLEAN DEFAULT TRUE NOT NULL,
    is_published BOOLEAN DEFAULT TRUE NOT NULL,
    banner_storage_path TEXT,
    attachment_name TEXT,
    created_by UUID REFERENCES auth.users(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_events_published ON public.college_events (is_published, event_date);
ALTER TABLE public.college_events ENABLE ROW LEVEL SECURITY;

-- 7.7 Official Documents
CREATE TABLE IF NOT EXISTS public.official_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title TEXT NOT NULL,
    description TEXT,
    document_type TEXT NOT NULL, -- 'admission', 'fee_structure', 'examination', 'rules', 'form'
    department_id UUID REFERENCES public.departments(id) ON DELETE SET NULL,
    storage_path TEXT NOT NULL,
    file_name TEXT NOT NULL,
    file_size_bytes BIGINT,
    mime_type TEXT DEFAULT 'application/pdf',
    download_url TEXT,
    is_published BOOLEAN DEFAULT TRUE NOT NULL,
    uploaded_by UUID REFERENCES auth.users(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);
ALTER TABLE public.official_documents ENABLE ROW LEVEL SECURITY;

-- 7.8 Prospectus
CREATE TABLE IF NOT EXISTS public.prospectus (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title TEXT NOT NULL,
    academic_session TEXT NOT NULL,
    storage_path TEXT NOT NULL,
    file_name TEXT NOT NULL,
    file_size_bytes BIGINT,
    mime_type TEXT DEFAULT 'application/pdf',
    download_url TEXT,
    is_current BOOLEAN DEFAULT TRUE NOT NULL,
    is_published BOOLEAN DEFAULT TRUE NOT NULL,
    uploaded_by UUID REFERENCES auth.users(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);
ALTER TABLE public.prospectus ENABLE ROW LEVEL SECURITY;

-- 7.9 Notifications Table
CREATE TABLE IF NOT EXISTS public.notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE, -- null for audience broadcasts
    notification_type TEXT NOT NULL, -- 'announcement_new', 'announcement_priority', 'event_new', 'event_update', 'document_new', 'course_outline_new', 'prospectus_new'
    title TEXT NOT NULL,
    message TEXT NOT NULL,
    related_content_id TEXT,
    content_type TEXT DEFAULT 'announcement',
    department_id TEXT,
    department_name TEXT,
    target_role TEXT DEFAULT 'all',
    is_priority BOOLEAN DEFAULT FALSE NOT NULL,
    is_pinned BOOLEAN DEFAULT FALSE NOT NULL,
    is_read BOOLEAN DEFAULT FALSE NOT NULL,
    action_url TEXT,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_notifications_created ON public.notifications (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_notifications_user ON public.notifications (user_id) WHERE user_id IS NOT NULL;
ALTER TABLE public.notifications ENABLE ROW LEVEL SECURITY;

-- ==============================================================================
-- 8. ROW LEVEL SECURITY (RLS) POLICIES
-- ==============================================================================

-- 8.1 user_roles
DROP POLICY IF EXISTS "user_roles_read_self" ON public.user_roles;
CREATE POLICY "user_roles_read_self" ON public.user_roles FOR SELECT USING (auth.uid() = user_id OR public.is_admin(auth.uid()));

DROP POLICY IF EXISTS "user_roles_admin_all" ON public.user_roles;
CREATE POLICY "user_roles_admin_all" ON public.user_roles FOR ALL USING (public.is_admin(auth.uid()));

-- 8.2 official registries
DROP POLICY IF EXISTS "bs_registry_select" ON public.official_bs_students;
CREATE POLICY "bs_registry_select" ON public.official_bs_students FOR SELECT USING (true);

DROP POLICY IF EXISTS "bs_registry_admin_write" ON public.official_bs_students;
CREATE POLICY "bs_registry_admin_write" ON public.official_bs_students FOR ALL USING (public.is_admin(auth.uid()) OR public.is_hod(auth.uid()));

DROP POLICY IF EXISTS "inter_registry_select" ON public.official_intermediate_students;
CREATE POLICY "inter_registry_select" ON public.official_intermediate_students FOR SELECT USING (true);

DROP POLICY IF EXISTS "inter_registry_admin_write" ON public.official_intermediate_students;
CREATE POLICY "inter_registry_admin_write" ON public.official_intermediate_students FOR ALL USING (public.is_admin(auth.uid()));

DROP POLICY IF EXISTS "faculty_registry_select" ON public.official_faculty;
CREATE POLICY "faculty_registry_select" ON public.official_faculty FOR SELECT USING (true);

DROP POLICY IF EXISTS "faculty_registry_admin_write" ON public.official_faculty;
CREATE POLICY "faculty_registry_admin_write" ON public.official_faculty FOR ALL USING (public.is_admin(auth.uid()));

-- 8.3 student and faculty profiles
DROP POLICY IF EXISTS "bs_profiles_read_own" ON public.bs_student_profiles;
CREATE POLICY "bs_profiles_read_own" ON public.bs_student_profiles FOR SELECT USING (auth.uid() = id OR public.is_teacher(auth.uid()));

DROP POLICY IF EXISTS "bs_profiles_write_own" ON public.bs_student_profiles;
CREATE POLICY "bs_profiles_write_own" ON public.bs_student_profiles FOR ALL USING (auth.uid() = id OR public.is_admin(auth.uid()));

DROP POLICY IF EXISTS "inter_profiles_read_own" ON public.intermediate_student_profiles;
CREATE POLICY "inter_profiles_read_own" ON public.intermediate_student_profiles FOR SELECT USING (auth.uid() = id OR public.is_teacher(auth.uid()));

DROP POLICY IF EXISTS "inter_profiles_write_own" ON public.intermediate_student_profiles;
CREATE POLICY "inter_profiles_write_own" ON public.intermediate_student_profiles FOR ALL USING (auth.uid() = id OR public.is_admin(auth.uid()));

DROP POLICY IF EXISTS "faculty_profiles_read_public" ON public.faculty_profiles;
CREATE POLICY "faculty_profiles_read_public" ON public.faculty_profiles FOR SELECT USING (true);

DROP POLICY IF EXISTS "faculty_profiles_write_own" ON public.faculty_profiles;
CREATE POLICY "faculty_profiles_write_own" ON public.faculty_profiles FOR ALL USING (auth.uid() = id OR public.is_admin(auth.uid()));

-- 8.4 content tables
DROP POLICY IF EXISTS "departments_read" ON public.departments;
CREATE POLICY "departments_read" ON public.departments FOR SELECT USING (true);
DROP POLICY IF EXISTS "departments_write" ON public.departments;
CREATE POLICY "departments_write" ON public.departments FOR ALL USING (public.is_admin(auth.uid()));

DROP POLICY IF EXISTS "programs_read" ON public.academic_programs;
CREATE POLICY "programs_read" ON public.academic_programs FOR SELECT USING (is_published = true OR public.is_teacher(auth.uid()));
DROP POLICY IF EXISTS "programs_write" ON public.academic_programs;
CREATE POLICY "programs_write" ON public.academic_programs FOR ALL USING (public.is_admin(auth.uid()) OR public.is_hod(auth.uid()));

DROP POLICY IF EXISTS "courses_read" ON public.courses;
CREATE POLICY "courses_read" ON public.courses FOR SELECT USING (is_published = true OR public.is_teacher(auth.uid()));
DROP POLICY IF EXISTS "courses_write" ON public.courses;
CREATE POLICY "courses_write" ON public.courses FOR ALL USING (public.is_admin(auth.uid()) OR public.is_hod(auth.uid()));

DROP POLICY IF EXISTS "outlines_read" ON public.course_outlines;
CREATE POLICY "outlines_read" ON public.course_outlines FOR SELECT USING (is_published = true OR public.is_teacher(auth.uid()));
DROP POLICY IF EXISTS "outlines_write" ON public.course_outlines;
CREATE POLICY "outlines_write" ON public.course_outlines FOR ALL USING (public.is_admin(auth.uid()) OR public.is_teacher(auth.uid()));

DROP POLICY IF EXISTS "announcements_read" ON public.announcements;
CREATE POLICY "announcements_read" ON public.announcements FOR SELECT USING (is_published = true OR public.is_teacher(auth.uid()));
DROP POLICY IF EXISTS "announcements_write" ON public.announcements;
CREATE POLICY "announcements_write" ON public.announcements FOR ALL USING (public.is_admin(auth.uid()) OR public.is_hod(auth.uid()));

DROP POLICY IF EXISTS "events_read" ON public.college_events;
CREATE POLICY "events_read" ON public.college_events FOR SELECT USING (is_published = true OR public.is_teacher(auth.uid()));
DROP POLICY IF EXISTS "events_write" ON public.college_events;
CREATE POLICY "events_write" ON public.college_events FOR ALL USING (public.is_admin(auth.uid()) OR public.is_hod(auth.uid()));

DROP POLICY IF EXISTS "documents_read" ON public.official_documents;
CREATE POLICY "documents_read" ON public.official_documents FOR SELECT USING (is_published = true OR public.is_teacher(auth.uid()));
DROP POLICY IF EXISTS "documents_write" ON public.official_documents;
CREATE POLICY "documents_write" ON public.official_documents FOR ALL USING (public.is_admin(auth.uid()) OR public.is_hod(auth.uid()));

DROP POLICY IF EXISTS "prospectus_read" ON public.prospectus;
CREATE POLICY "prospectus_read" ON public.prospectus FOR SELECT USING (is_published = true OR public.is_admin(auth.uid()));
DROP POLICY IF EXISTS "prospectus_write" ON public.prospectus;
CREATE POLICY "prospectus_write" ON public.prospectus FOR ALL USING (public.is_admin(auth.uid()));

DROP POLICY IF EXISTS "notifications_read" ON public.notifications;
CREATE POLICY "notifications_read" ON public.notifications FOR SELECT USING (user_id IS NULL OR user_id = auth.uid() OR public.is_admin(auth.uid()));
DROP POLICY IF EXISTS "notifications_write" ON public.notifications;
CREATE POLICY "notifications_write" ON public.notifications FOR ALL USING (public.is_admin(auth.uid()) OR public.is_hod(auth.uid()) OR auth.uid() IS NOT NULL);

-- ==============================================================================
-- 9. ADMIN & HOD MANAGEMENT RPCs
-- ==============================================================================

-- 9.1 Registry Statistics
CREATE OR REPLACE FUNCTION public.admin_get_registry_stats()
RETURNS JSONB
LANGUAGE plpgsql SECURITY DEFINER SET search_path = public
AS $$
DECLARE
    v_bs_total INT; v_bs_claimed INT;
    v_inter_total INT; v_inter_claimed INT;
    v_faculty_total INT; v_faculty_claimed INT;
BEGIN
    IF NOT (public.is_admin(auth.uid()) OR public.is_hod(auth.uid())) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unauthorized access.');
    END IF;

    SELECT count(*), count(*) FILTER (WHERE is_claimed) INTO v_bs_total, v_bs_claimed FROM public.official_bs_students;
    SELECT count(*), count(*) FILTER (WHERE is_claimed) INTO v_inter_total, v_inter_claimed FROM public.official_intermediate_students;
    SELECT count(*), count(*) FILTER (WHERE is_claimed) INTO v_faculty_total, v_faculty_claimed FROM public.official_faculty;

    RETURN jsonb_build_object(
        'success', true,
        'bs_students', jsonb_build_object('total', v_bs_total, 'claimed', v_bs_claimed, 'unclaimed', v_bs_total - v_bs_claimed),
        'intermediate_students', jsonb_build_object('total', v_inter_total, 'claimed', v_inter_claimed, 'unclaimed', v_inter_total - v_inter_claimed),
        'faculty', jsonb_build_object('total', v_faculty_total, 'claimed', v_faculty_claimed, 'unclaimed', v_faculty_total - v_faculty_claimed)
    );
END;
$$;

-- 9.2 Add BS Registry Entry
CREATE OR REPLACE FUNCTION public.admin_add_bs_registry_entry(
    p_roll_number TEXT,
    p_registration_number TEXT,
    p_student_name TEXT,
    p_father_name TEXT,
    p_program_name TEXT,
    p_session_year TEXT,
    p_semester_number INT DEFAULT 1
)
RETURNS JSONB
LANGUAGE plpgsql SECURITY DEFINER SET search_path = public
AS $$
DECLARE v_id UUID;
BEGIN
    IF NOT (public.is_admin(auth.uid()) OR public.is_hod(auth.uid())) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unauthorized.');
    END IF;

    INSERT INTO public.official_bs_students (
        roll_number, registration_number, student_name, father_name,
        program_name, session_year, semester_number
    ) VALUES (
        TRIM(p_roll_number), TRIM(p_registration_number), TRIM(p_student_name), TRIM(p_father_name),
        TRIM(p_program_name), TRIM(p_session_year), COALESCE(p_semester_number, 1)
    ) RETURNING id INTO v_id;

    RETURN jsonb_build_object('success', true, 'id', v_id, 'message', 'BS student registry record added successfully.');
END;
$$;

-- 9.3 Add Intermediate Registry Entry
CREATE OR REPLACE FUNCTION public.admin_add_intermediate_registry_entry(
    p_roll_number TEXT,
    p_registration_number TEXT,
    p_student_name TEXT,
    p_father_name TEXT,
    p_program_name TEXT,
    p_session_year TEXT,
    p_part_number INT DEFAULT 1
)
RETURNS JSONB
LANGUAGE plpgsql SECURITY DEFINER SET search_path = public
AS $$
DECLARE v_id UUID;
BEGIN
    IF NOT public.is_admin(auth.uid()) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unauthorized.');
    END IF;

    INSERT INTO public.official_intermediate_students (
        roll_number, registration_number, student_name, father_name,
        program_name, session_year, part_number
    ) VALUES (
        TRIM(p_roll_number), TRIM(p_registration_number), TRIM(p_student_name), TRIM(p_father_name),
        TRIM(p_program_name), TRIM(p_session_year), COALESCE(p_part_number, 1)
    ) RETURNING id INTO v_id;

    RETURN jsonb_build_object('success', true, 'id', v_id, 'message', 'Intermediate student registry record added successfully.');
END;
$$;

-- 9.4 Add Faculty Registry Entry
CREATE OR REPLACE FUNCTION public.admin_add_faculty_registry_entry(
    p_faculty_id TEXT,
    p_full_name TEXT,
    p_department TEXT,
    p_designation TEXT,
    p_institutional_email TEXT,
    p_phone_number TEXT DEFAULT NULL
)
RETURNS JSONB
LANGUAGE plpgsql SECURITY DEFINER SET search_path = public
AS $$
DECLARE v_id UUID;
BEGIN
    IF NOT public.is_admin(auth.uid()) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unauthorized.');
    END IF;

    INSERT INTO public.official_faculty (
        faculty_id, full_name, department, designation, institutional_email, phone_number
    ) VALUES (
        TRIM(p_faculty_id), TRIM(p_full_name), TRIM(p_department), TRIM(p_designation),
        LOWER(TRIM(p_institutional_email)), TRIM(p_phone_number)
    ) RETURNING id INTO v_id;

    RETURN jsonb_build_object('success', true, 'id', v_id, 'message', 'Faculty registry record added successfully.');
END;
$$;

-- 9.5 Reset Claimed Account (Emergency Unlink)
CREATE OR REPLACE FUNCTION public.admin_reset_claimed_account(
    p_registry_type TEXT, -- 'bs', 'intermediate', 'faculty'
    p_record_id UUID,
    p_reason TEXT DEFAULT NULL
)
RETURNS JSONB
LANGUAGE plpgsql SECURITY DEFINER SET search_path = public
AS $$
DECLARE
    v_claimed_user_id UUID;
BEGIN
    IF NOT public.is_admin(auth.uid()) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unauthorized.');
    END IF;

    IF p_registry_type = 'bs' THEN
        SELECT claimed_by_user_id INTO v_claimed_user_id FROM public.official_bs_students WHERE id = p_record_id;
        IF v_claimed_user_id IS NOT NULL THEN
            DELETE FROM public.bs_student_profiles WHERE id = v_claimed_user_id;
            DELETE FROM public.user_roles WHERE user_id = v_claimed_user_id;
        END IF;
        UPDATE public.official_bs_students SET is_claimed = FALSE, claimed_by_user_id = NULL, claimed_at = NULL, updated_at = now() WHERE id = p_record_id;
    ELSIF p_registry_type = 'intermediate' THEN
        SELECT claimed_by_user_id INTO v_claimed_user_id FROM public.official_intermediate_students WHERE id = p_record_id;
        IF v_claimed_user_id IS NOT NULL THEN
            DELETE FROM public.intermediate_student_profiles WHERE id = v_claimed_user_id;
            DELETE FROM public.user_roles WHERE user_id = v_claimed_user_id;
        END IF;
        UPDATE public.official_intermediate_students SET is_claimed = FALSE, claimed_by_user_id = NULL, claimed_at = NULL, updated_at = now() WHERE id = p_record_id;
    ELSIF p_registry_type = 'faculty' THEN
        SELECT claimed_by_user_id INTO v_claimed_user_id FROM public.official_faculty WHERE id = p_record_id;
        IF v_claimed_user_id IS NOT NULL THEN
            DELETE FROM public.faculty_profiles WHERE id = v_claimed_user_id;
            DELETE FROM public.user_roles WHERE user_id = v_claimed_user_id;
        END IF;
        UPDATE public.official_faculty SET is_claimed = FALSE, claimed_by_user_id = NULL, claimed_at = NULL, updated_at = now() WHERE id = p_record_id;
    END IF;

    RETURN jsonb_build_object('success', true, 'message', 'Registry claim successfully reset.');
END;
$$;

-- ==============================================================================
-- 10. SUPABASE STORAGE BUCKETS & STORAGE POLICIES
-- ==============================================================================
INSERT INTO storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
VALUES 
    ('college-prospectus', 'college-prospectus', true, 52428800, ARRAY['application/pdf']),
    ('official-documents', 'official-documents', true, 31457280, ARRAY['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 'image/jpeg', 'image/png']),
    ('announcement-attachments', 'announcement-attachments', true, 26214400, ARRAY['application/pdf', 'image/jpeg', 'image/png', 'image/webp']),
    ('course-outlines', 'course-outlines', true, 20971520, ARRAY['application/pdf']),
    ('profile-photos', 'profile-photos', true, 5242880, ARRAY['image/jpeg', 'image/png', 'image/webp']),
    ('college-media', 'college-media', true, 31457280, ARRAY['image/jpeg', 'image/png', 'image/webp', 'image/gif'])
ON CONFLICT (id) DO UPDATE SET 
    public = EXCLUDED.public,
    file_size_limit = EXCLUDED.file_size_limit,
    allowed_mime_types = EXCLUDED.allowed_mime_types;

-- Storage RLS
DROP POLICY IF EXISTS "Public Storage Read" ON storage.objects;
CREATE POLICY "Public Storage Read" ON storage.objects FOR SELECT USING (bucket_id IN ('college-prospectus', 'official-documents', 'announcement-attachments', 'course-outlines', 'profile-photos', 'college-media'));

DROP POLICY IF EXISTS "Profile Photo Upload" ON storage.objects;
CREATE POLICY "Profile Photo Upload" ON storage.objects FOR INSERT WITH CHECK (bucket_id = 'profile-photos' AND auth.uid() IS NOT NULL);

DROP POLICY IF EXISTS "Admin and Teacher Storage Upload" ON storage.objects;
CREATE POLICY "Admin and Teacher Storage Upload" ON storage.objects FOR ALL USING (bucket_id IN ('college-prospectus', 'official-documents', 'announcement-attachments', 'course-outlines', 'college-media') AND (public.is_teacher(auth.uid()) OR public.is_admin(auth.uid())));

-- ==============================================================================
-- 11. SUPABASE REALTIME REPLICATION PUBLICATION
-- ==============================================================================
DO $$ BEGIN
    ALTER PUBLICATION supabase_realtime ADD TABLE public.notifications;
EXCEPTION WHEN others THEN null; END $$;

DO $$ BEGIN
    ALTER PUBLICATION supabase_realtime ADD TABLE public.announcements;
EXCEPTION WHEN others THEN null; END $$;

DO $$ BEGIN
    ALTER PUBLICATION supabase_realtime ADD TABLE public.college_events;
EXCEPTION WHEN others THEN null; END $$;

DO $$ BEGIN
    ALTER PUBLICATION supabase_realtime ADD TABLE public.official_documents;
EXCEPTION WHEN others THEN null; END $$;

DO $$ BEGIN
    ALTER PUBLICATION supabase_realtime ADD TABLE public.course_outlines;
EXCEPTION WHEN others THEN null; END $$;

DO $$ BEGIN
    ALTER PUBLICATION supabase_realtime ADD TABLE public.prospectus;
EXCEPTION WHEN others THEN null; END $$;

-- ==============================================================================
-- 12. INITIAL SEED DATA (OFFICIAL DEPARTMENTS, PROGRAMS & SAMPLE REGISTRY)
-- ==============================================================================
INSERT INTO public.departments (id, name, code, description) VALUES
    ('00000000-0000-0000-0000-000000000001', 'Information Technology', 'IT', 'Department of Information Technology and Computer Sciences'),
    ('00000000-0000-0000-0000-000000000002', 'English Literature', 'ENG', 'Department of English Language and Literature'),
    ('00000000-0000-0000-0000-000000000003', 'Physics', 'PHY', 'Department of Physics'),
    ('00000000-0000-0000-0000-000000000004', 'Chemistry', 'CHEM', 'Department of Chemistry'),
    ('00000000-0000-0000-0000-000000000005', 'Mathematics', 'MATH', 'Department of Mathematics'),
    ('00000000-0000-0000-0000-000000000006', 'Economics', 'ECON', 'Department of Economics')
ON CONFLICT (name) DO NOTHING;

INSERT INTO public.academic_programs (id, department_id, name, code, level, duration_years, total_semesters) VALUES
    ('00000000-0000-0000-0001-000000000001', '00000000-0000-0000-0000-000000000001', 'BS Information Technology', 'BS-IT', 'BS', 4, 8),
    ('00000000-0000-0000-0001-000000000002', '00000000-0000-0000-0000-000000000001', 'BS Computer Science', 'BS-CS', 'BS', 4, 8),
    ('00000000-0000-0000-0001-000000000003', '00000000-0000-0000-0000-000000000002', 'BS English', 'BS-ENG', 'BS', 4, 8),
    ('00000000-0000-0000-0001-000000000004', '00000000-0000-0000-0000-000000000003', 'FSc Pre-Engineering', 'FSC-PE', 'Intermediate', 2, 2),
    ('00000000-0000-0000-0001-000000000005', '00000000-0000-0000-0000-000000000004', 'FSc Pre-Medical', 'FSC-PM', 'Intermediate', 2, 2),
    ('00000000-0000-0000-0001-000000000006', '00000000-0000-0000-0000-000000000001', 'ICS Computer Science', 'ICS', 'Intermediate', 2, 2)
ON CONFLICT (department_id, code) DO NOTHING;

-- Sample official BS Student for immediate testing
INSERT INTO public.official_bs_students (roll_number, registration_number, student_name, father_name, program_name, session_year, semester_number)
VALUES ('BSIT-2022-01', '22-GGC-IT-001', 'Muhammad Ahmad', 'Muhammad Tariq', 'BS Information Technology', '2022-2026', 4)
ON CONFLICT DO NOTHING;

-- Sample official Intermediate Student for immediate testing
INSERT INTO public.official_intermediate_students (roll_number, registration_number, student_name, father_name, program_name, session_year, part_number)
VALUES ('ICS-2023-01', '23-GGC-ICS-001', 'Ali Hassan', 'Hassan Raza', 'ICS Computer Science', '2023-2025', 2)
ON CONFLICT DO NOTHING;

-- Sample official Faculty Member for immediate testing
INSERT INTO public.official_faculty (faculty_id, full_name, department, designation, institutional_email, phone_number)
VALUES ('FAC-IT-101', 'Prof. Dr. Tariq Mehmood', 'Information Technology', 'Head of Department (HOD)', 'tariq.mehmood@ggc.edu.pk', '03001234567')
ON CONFLICT DO NOTHING;

-- Sample college announcement
INSERT INTO public.announcements (title, content, category, is_pinned, is_published, author_name)
VALUES ('Welcome to Academic Session 2026', 'Welcome all BS and Intermediate students to the new academic session at Govt. Graduate College Mandi Bahauddin. Classes commence as per the published timetable.', 'General', true, true, 'College Administration')
ON CONFLICT DO NOTHING;

-- ==============================================================================
-- SETUP COMPLETE! ALL TABLES, RPCS, POLICIES, STORAGE, REALTIME ARE INITIALIZED.
-- ==============================================================================
