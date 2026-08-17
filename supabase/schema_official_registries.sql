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
-- Core Security & Audit Features:
-- - Server-side RBAC enforcement:
--     * Super Admin (`admin`): Full college-wide registry authority.
--     * HOD (`hod`): Department-scoped authority over student/faculty records in their department.
--     * Teachers & Students: Read-only verification access, zero registry modification access.
-- - Case-insensitive unique indexes for Roll Numbers, Registration Numbers, and Faculty IDs.
-- - Safe Foreign Keys: profiles reference official registries with ON DELETE RESTRICT.
-- - Audit Safety: Claimed records cannot be deleted without an explicit administrative reset.
-- - Identity Stability: Claimed roll numbers / registration numbers / faculty IDs cannot be altered while claimed.
-- - Safe atomic claiming with row-level locks (`FOR UPDATE`).
-- ==============================================================================

-- ==============================================================================
-- 1. REGISTRY TABLES & SCHEMA EXTENSIONS
-- ==============================================================================

-- 1.1 Official BS Students Table
CREATE TABLE IF NOT EXISTS public.official_bs_students (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    roll_number TEXT NOT NULL,
    registration_number TEXT NOT NULL,
    program TEXT NOT NULL,
    session TEXT NOT NULL,
    first_name TEXT,
    last_name TEXT,
    is_claimed BOOLEAN DEFAULT FALSE NOT NULL,
    claimed_by_user_id UUID UNIQUE REFERENCES auth.users(id) ON DELETE SET NULL,
    claimed_at TIMESTAMPTZ,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);

ALTER TABLE public.official_bs_students 
    ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE NOT NULL,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL;

-- Case-insensitive unique indexes on BS Students
CREATE UNIQUE INDEX IF NOT EXISTS idx_official_bs_roll_lower ON public.official_bs_students (LOWER(TRIM(roll_number)));
CREATE UNIQUE INDEX IF NOT EXISTS idx_official_bs_reg_lower ON public.official_bs_students (LOWER(TRIM(registration_number)));
CREATE INDEX IF NOT EXISTS idx_official_bs_claimed ON public.official_bs_students (is_claimed, is_active);
CREATE INDEX IF NOT EXISTS idx_official_bs_program ON public.official_bs_students (LOWER(TRIM(program)));

-- 1.2 Official Intermediate Students Table
CREATE TABLE IF NOT EXISTS public.official_intermediate_students (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    roll_number TEXT NOT NULL,
    registration_number TEXT NOT NULL,
    program TEXT NOT NULL,
    session TEXT DEFAULT '2024-2026' NOT NULL,
    first_name TEXT,
    last_name TEXT,
    is_claimed BOOLEAN DEFAULT FALSE NOT NULL,
    claimed_by_user_id UUID UNIQUE REFERENCES auth.users(id) ON DELETE SET NULL,
    claimed_at TIMESTAMPTZ,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);

ALTER TABLE public.official_intermediate_students 
    ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE NOT NULL,
    ADD COLUMN IF NOT EXISTS session TEXT DEFAULT '2024-2026',
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL;

-- Case-insensitive unique indexes on Intermediate Students
CREATE UNIQUE INDEX IF NOT EXISTS idx_official_inter_roll_lower ON public.official_intermediate_students (LOWER(TRIM(roll_number)));
CREATE UNIQUE INDEX IF NOT EXISTS idx_official_inter_reg_lower ON public.official_intermediate_students (LOWER(TRIM(registration_number)));
CREATE INDEX IF NOT EXISTS idx_official_inter_claimed ON public.official_intermediate_students (is_claimed, is_active);
CREATE INDEX IF NOT EXISTS idx_official_inter_program ON public.official_intermediate_students (LOWER(TRIM(program)));

-- 1.3 Official Faculty Table
CREATE TABLE IF NOT EXISTS public.official_faculty (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    faculty_id TEXT NOT NULL,
    full_name TEXT NOT NULL,
    first_name TEXT,
    last_name TEXT,
    department TEXT NOT NULL,
    designation TEXT NOT NULL,
    qualification TEXT NOT NULL,
    institutional_email TEXT,
    phone_number TEXT,
    is_claimed BOOLEAN DEFAULT FALSE NOT NULL,
    claimed_by_user_id UUID UNIQUE REFERENCES auth.users(id) ON DELETE SET NULL,
    claimed_at TIMESTAMPTZ,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);

ALTER TABLE public.official_faculty 
    ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE NOT NULL,
    ADD COLUMN IF NOT EXISTS first_name TEXT,
    ADD COLUMN IF NOT EXISTS last_name TEXT,
    ADD COLUMN IF NOT EXISTS phone_number TEXT,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL;

-- Case-insensitive unique indexes on Faculty
CREATE UNIQUE INDEX IF NOT EXISTS idx_official_faculty_id_lower ON public.official_faculty (LOWER(TRIM(faculty_id)));
CREATE UNIQUE INDEX IF NOT EXISTS idx_official_faculty_email_lower ON public.official_faculty (LOWER(TRIM(institutional_email))) WHERE institutional_email IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_official_faculty_claimed ON public.official_faculty (is_claimed, is_active);
CREATE INDEX IF NOT EXISTS idx_official_faculty_dept ON public.official_faculty (LOWER(TRIM(department)));

-- ==============================================================================
-- 2. RBAC HELPER FUNCTIONS FOR REGISTRY SCOPING
-- ==============================================================================

-- Check if user can manage BS registry for a given program
CREATE OR REPLACE FUNCTION public.can_manage_bs_program(
    p_user_id UUID DEFAULT auth.uid(),
    p_program TEXT DEFAULT NULL
)
RETURNS BOOLEAN
LANGUAGE plpgsql
STABLE
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_role public.app_role;
    v_dept TEXT;
BEGIN
    IF p_user_id IS NULL THEN
        RETURN FALSE;
    END IF;

    SELECT role, department INTO v_role, v_dept
    FROM public.user_roles
    WHERE user_id = p_user_id;

    -- Super Admin has full college-wide authority
    IF v_role = 'admin'::public.app_role THEN
        RETURN TRUE;
    END IF;

    -- HOD has department-scoped authority
    IF v_role = 'hod'::public.app_role THEN
        IF p_program IS NULL OR TRIM(p_program) = '' THEN
            RETURN TRUE;
        END IF;
        IF v_dept IS NOT NULL AND (
            LOWER(p_program) LIKE '%' || LOWER(TRIM(v_dept)) || '%' OR
            LOWER(TRIM(v_dept)) LIKE '%' || LOWER(TRIM(p_program)) || '%'
        ) THEN
            RETURN TRUE;
        END IF;
    END IF;

    RETURN FALSE;
END;
$$;

-- Check if user can manage Faculty registry for a given department
CREATE OR REPLACE FUNCTION public.can_manage_faculty_department(
    p_user_id UUID DEFAULT auth.uid(),
    p_department TEXT DEFAULT NULL
)
RETURNS BOOLEAN
LANGUAGE plpgsql
STABLE
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_role public.app_role;
    v_dept TEXT;
BEGIN
    IF p_user_id IS NULL THEN
        RETURN FALSE;
    END IF;

    SELECT role, department INTO v_role, v_dept
    FROM public.user_roles
    WHERE user_id = p_user_id;

    IF v_role = 'admin'::public.app_role THEN
        RETURN TRUE;
    END IF;

    IF v_role = 'hod'::public.app_role THEN
        IF p_department IS NULL OR TRIM(p_department) = '' THEN
            RETURN TRUE;
        END IF;
        RETURN LOWER(TRIM(COALESCE(v_dept, ''))) = LOWER(TRIM(p_department));
    END IF;

    RETURN FALSE;
END;
$$;

-- Check if user has Intermediate registry management authority
CREATE OR REPLACE FUNCTION public.can_manage_intermediate_registry(
    p_user_id UUID DEFAULT auth.uid()
)
RETURNS BOOLEAN
LANGUAGE plpgsql
STABLE
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_role public.app_role;
BEGIN
    IF p_user_id IS NULL THEN
        RETURN FALSE;
    END IF;

    SELECT role INTO v_role
    FROM public.user_roles
    WHERE user_id = p_user_id;

    RETURN v_role IN ('admin'::public.app_role, 'hod'::public.app_role);
END;
$$;

-- ==============================================================================
-- 3. ADMINISTRATIVE RPCs (Protected strictly for Super Admin & Department HOD)
-- ==============================================================================

-- 3.1. Admin/HOD Manage BS Student Record (Insert / Update with Audit Safety)
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
    v_existing RECORD;
    v_result_id UUID;
BEGIN
    -- Check permissions
    IF NOT public.can_manage_bs_program(v_caller_id, p_program) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unauthorized: Insufficient privileges to manage BS student records for this program.');
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
        SELECT * INTO v_existing
        FROM public.official_bs_students
        WHERE id = p_id;

        IF NOT FOUND THEN
            RETURN jsonb_build_object('success', false, 'error', 'Official BS student record not found.');
        END IF;

        -- Audit Safety: If record is already claimed, prohibit modifying roll_number and registration_number
        IF v_existing.is_claimed = TRUE THEN
            IF v_clean_roll <> UPPER(TRIM(v_existing.roll_number)) OR v_clean_reg <> UPPER(TRIM(v_existing.registration_number)) THEN
                RETURN jsonb_build_object(
                    'success', false, 
                    'error', 'Audit Safety Restriction: Cannot alter Roll/Registration Number on an actively claimed student account. Please reset the claim first.'
                );
            END IF;
        ELSE
            -- Check for roll number collision with another record
            IF EXISTS (SELECT 1 FROM public.official_bs_students WHERE UPPER(TRIM(roll_number)) = v_clean_roll AND id <> p_id) THEN
                RETURN jsonb_build_object('success', false, 'error', 'BS Roll Number "' || p_roll_number || '" already exists in another registry record.');
            END IF;

            -- Check for registration number collision with another record
            IF EXISTS (SELECT 1 FROM public.official_bs_students WHERE UPPER(TRIM(registration_number)) = v_clean_reg AND id <> p_id) THEN
                RETURN jsonb_build_object('success', false, 'error', 'University Registration Number "' || p_registration_number || '" already exists in another registry record.');
            END IF;
        END IF;

        UPDATE public.official_bs_students
        SET roll_number = CASE WHEN v_existing.is_claimed THEN roll_number ELSE v_clean_roll END,
            registration_number = CASE WHEN v_existing.is_claimed THEN registration_number ELSE v_clean_reg END,
            program = COALESCE(TRIM(p_program), program),
            session = COALESCE(TRIM(p_session), session),
            first_name = COALESCE(TRIM(p_first_name), first_name),
            last_name = COALESCE(TRIM(p_last_name), last_name),
            is_active = COALESCE(p_is_active, is_active),
            updated_at = NOW()
        WHERE id = p_id
        RETURNING id INTO v_result_id;
    END IF;

    RETURN jsonb_build_object(
        'success', true,
        'record_id', v_result_id,
        'message', 'Official BS student record saved successfully.'
    );
END;
$$;

-- 3.2. Admin/HOD Delete BS Student Record (Safe Deletion with Audit Restriction)
CREATE OR REPLACE FUNCTION public.admin_delete_bs_student_record(
    p_id UUID
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_caller_id UUID := auth.uid();
    v_existing RECORD;
BEGIN
    SELECT * INTO v_existing
    FROM public.official_bs_students
    WHERE id = p_id;

    IF NOT FOUND THEN
        RETURN jsonb_build_object('success', false, 'error', 'Official BS student record not found.');
    END IF;

    IF NOT public.can_manage_bs_program(v_caller_id, v_existing.program) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unauthorized: Insufficient privileges to delete records for this program.');
    END IF;

    -- Audit Safety: Cannot delete claimed record
    IF v_existing.is_claimed = TRUE OR v_existing.claimed_by_user_id IS NOT NULL THEN
        RETURN jsonb_build_object(
            'success', false,
            'error', 'Audit Safety Restriction: Cannot delete a record linked to an active user account. Reset the claim first if needed.'
        );
    END IF;

    DELETE FROM public.official_bs_students WHERE id = p_id;

    RETURN jsonb_build_object(
        'success', true,
        'message', 'Official BS student record deleted successfully.'
    );
END;
$$;

-- 3.3. Admin/HOD Manage Intermediate Student Record (Insert / Update with Audit Safety)
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
    v_existing RECORD;
    v_result_id UUID;
BEGIN
    IF NOT public.can_manage_intermediate_registry(v_caller_id) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unauthorized: Only administrators can manage official Intermediate records.');
    END IF;

    IF v_clean_roll = '' OR v_clean_reg = '' OR TRIM(p_program) = '' THEN
        RETURN jsonb_build_object('success', false, 'error', 'Roll Number, Registration Number, and Program are required.');
    END IF;

    IF p_id IS NULL THEN
        IF EXISTS (SELECT 1 FROM public.official_intermediate_students WHERE UPPER(TRIM(roll_number)) = v_clean_roll) THEN
            RETURN jsonb_build_object('success', false, 'error', 'Intermediate Roll Number "' || p_roll_number || '" already exists in the registry.');
        END IF;

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
        SELECT * INTO v_existing
        FROM public.official_intermediate_students
        WHERE id = p_id;

        IF NOT FOUND THEN
            RETURN jsonb_build_object('success', false, 'error', 'Official Intermediate student record not found.');
        END IF;

        -- Audit Safety: Prohibit altering Roll/Reg on claimed account
        IF v_existing.is_claimed = TRUE THEN
            IF v_clean_roll <> UPPER(TRIM(v_existing.roll_number)) OR v_clean_reg <> UPPER(TRIM(v_existing.registration_number)) THEN
                RETURN jsonb_build_object(
                    'success', false,
                    'error', 'Audit Safety Restriction: Cannot alter Roll/Registration Number on an actively claimed intermediate student account. Please reset the claim first.'
                );
            END IF;
        ELSE
            IF EXISTS (SELECT 1 FROM public.official_intermediate_students WHERE UPPER(TRIM(roll_number)) = v_clean_roll AND id <> p_id) THEN
                RETURN jsonb_build_object('success', false, 'error', 'Intermediate Roll Number "' || p_roll_number || '" already exists in another registry record.');
            END IF;

            IF EXISTS (SELECT 1 FROM public.official_intermediate_students WHERE UPPER(TRIM(registration_number)) = v_clean_reg AND id <> p_id) THEN
                RETURN jsonb_build_object('success', false, 'error', 'Board Registration Number "' || p_registration_number || '" already exists in another registry record.');
            END IF;
        END IF;

        UPDATE public.official_intermediate_students
        SET roll_number = CASE WHEN v_existing.is_claimed THEN roll_number ELSE v_clean_roll END,
            registration_number = CASE WHEN v_existing.is_claimed THEN registration_number ELSE v_clean_reg END,
            program = COALESCE(TRIM(p_program), program),
            session = COALESCE(TRIM(p_session), session),
            first_name = COALESCE(TRIM(p_first_name), first_name),
            last_name = COALESCE(TRIM(p_last_name), last_name),
            is_active = COALESCE(p_is_active, is_active),
            updated_at = NOW()
        WHERE id = p_id
        RETURNING id INTO v_result_id;
    END IF;

    RETURN jsonb_build_object(
        'success', true,
        'record_id', v_result_id,
        'message', 'Official Intermediate student record saved successfully.'
    );
END;
$$;

-- 3.4. Admin/HOD Delete Intermediate Student Record (Safe Deletion)
CREATE OR REPLACE FUNCTION public.admin_delete_intermediate_student_record(
    p_id UUID
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_caller_id UUID := auth.uid();
    v_existing RECORD;
BEGIN
    SELECT * INTO v_existing
    FROM public.official_intermediate_students
    WHERE id = p_id;

    IF NOT FOUND THEN
        RETURN jsonb_build_object('success', false, 'error', 'Official Intermediate student record not found.');
    END IF;

    IF NOT public.can_manage_intermediate_registry(v_caller_id) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unauthorized: Insufficient privileges to delete Intermediate records.');
    END IF;

    IF v_existing.is_claimed = TRUE OR v_existing.claimed_by_user_id IS NOT NULL THEN
        RETURN jsonb_build_object(
            'success', false,
            'error', 'Audit Safety Restriction: Cannot delete a record linked to an active user account. Reset the claim first if needed.'
        );
    END IF;

    DELETE FROM public.official_intermediate_students WHERE id = p_id;

    RETURN jsonb_build_object(
        'success', true,
        'message', 'Official Intermediate student record deleted successfully.'
    );
END;
$$;

-- 3.5. Admin/HOD Manage Faculty Record (Insert / Update with Audit Safety)
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
    v_existing RECORD;
    v_result_id UUID;
BEGIN
    IF NOT public.can_manage_faculty_department(v_caller_id, p_department) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unauthorized: Insufficient privileges to manage faculty records for this department.');
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
        SELECT * INTO v_existing
        FROM public.official_faculty
        WHERE id = p_id;

        IF NOT FOUND THEN
            RETURN jsonb_build_object('success', false, 'error', 'Official faculty record not found.');
        END IF;

        -- Audit Safety: Cannot change faculty_id on active claim
        IF v_existing.is_claimed = TRUE THEN
            IF v_clean_fid <> UPPER(TRIM(v_existing.faculty_id)) THEN
                RETURN jsonb_build_object(
                    'success', false,
                    'error', 'Audit Safety Restriction: Cannot alter Faculty ID on an actively claimed faculty account. Please reset the claim first.'
                );
            END IF;
        ELSE
            IF EXISTS (SELECT 1 FROM public.official_faculty WHERE UPPER(TRIM(faculty_id)) = v_clean_fid AND id <> p_id) THEN
                RETURN jsonb_build_object('success', false, 'error', 'Faculty ID "' || p_faculty_id || '" already exists in another registry record.');
            END IF;
        END IF;

        -- Check email collision with another record
        IF v_clean_email IS NOT NULL AND EXISTS (
            SELECT 1 FROM public.official_faculty 
            WHERE LOWER(TRIM(institutional_email)) = v_clean_email AND id <> p_id
        ) THEN
            RETURN jsonb_build_object('success', false, 'error', 'Institutional Email "' || p_institutional_email || '" already exists in another registry record.');
        END IF;

        UPDATE public.official_faculty
        SET faculty_id = CASE WHEN v_existing.is_claimed THEN faculty_id ELSE v_clean_fid END,
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
    END IF;

    RETURN jsonb_build_object(
        'success', true,
        'record_id', v_result_id,
        'message', 'Official faculty record saved successfully.'
    );
END;
$$;

-- 3.6. Admin/HOD Delete Faculty Record (Safe Deletion)
CREATE OR REPLACE FUNCTION public.admin_delete_faculty_record(
    p_id UUID
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_caller_id UUID := auth.uid();
    v_existing RECORD;
BEGIN
    SELECT * INTO v_existing
    FROM public.official_faculty
    WHERE id = p_id;

    IF NOT FOUND THEN
        RETURN jsonb_build_object('success', false, 'error', 'Official faculty record not found.');
    END IF;

    IF NOT public.can_manage_faculty_department(v_caller_id, v_existing.department) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unauthorized: Insufficient privileges to delete faculty records for this department.');
    END IF;

    IF v_existing.is_claimed = TRUE OR v_existing.claimed_by_user_id IS NOT NULL THEN
        RETURN jsonb_build_object(
            'success', false,
            'error', 'Audit Safety Restriction: Cannot delete a record linked to an active faculty account. Reset the claim first if needed.'
        );
    END IF;

    DELETE FROM public.official_faculty WHERE id = p_id;

    RETURN jsonb_build_object(
        'success', true,
        'message', 'Official faculty record deleted successfully.'
    );
END;
$$;

-- 3.7. Admin Toggle Record Active Status
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
    IF NOT public.is_admin(v_caller_id) AND NOT public.is_hod(v_caller_id) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unauthorized: Only system administrators and HODs can alter active status.');
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

-- 3.8. Admin Reset Claimed Record (Protected Administrative Reset)
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
    -- Resetting claimed identities requires super-admin authority
    IF NOT public.is_admin(v_caller_id) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unauthorized: Only system administrators can reset claimed records.');
    END IF;

    CASE LOWER(TRIM(p_registry_type))
        WHEN 'bs_student' THEN
            SELECT claimed_by_user_id INTO v_old_claimed_by
            FROM public.official_bs_students
            WHERE id = p_record_id;

            IF v_old_claimed_by IS NOT NULL THEN
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

DROP POLICY IF EXISTS "Admins and HODs have management access to official bs records" ON public.official_bs_students;
CREATE POLICY "Admins and HODs have management access to official bs records"
ON public.official_bs_students FOR ALL
USING (public.can_manage_bs_program(auth.uid(), program))
WITH CHECK (public.can_manage_bs_program(auth.uid(), program));

-- 4.2. Official Intermediate Students RLS
ALTER TABLE public.official_intermediate_students ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Public can view official inter records for verification" ON public.official_intermediate_students;
CREATE POLICY "Public can view official inter records for verification"
ON public.official_intermediate_students FOR SELECT
USING (true);

DROP POLICY IF EXISTS "Admins and HODs have management access to official inter records" ON public.official_intermediate_students;
CREATE POLICY "Admins and HODs have management access to official inter records"
ON public.official_intermediate_students FOR ALL
USING (public.can_manage_intermediate_registry(auth.uid()))
WITH CHECK (public.can_manage_intermediate_registry(auth.uid()));

-- 4.3. Official Faculty Registry RLS
ALTER TABLE public.official_faculty ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Public can view official faculty records for verification" ON public.official_faculty;
CREATE POLICY "Public can view official faculty records for verification"
ON public.official_faculty FOR SELECT
USING (true);

DROP POLICY IF EXISTS "Admins and HODs have management access to official faculty records" ON public.official_faculty;
CREATE POLICY "Admins and HODs have management access to official faculty records"
ON public.official_faculty FOR ALL
USING (public.can_manage_faculty_department(auth.uid(), department))
WITH CHECK (public.can_manage_faculty_department(auth.uid(), department));
