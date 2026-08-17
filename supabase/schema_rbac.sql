-- ==============================================================================
-- GGC M.B.Din Official Android App - Role-Based Access Control (RBAC) Schema
-- ==============================================================================
-- Run this SQL in your Supabase Project SQL Editor (https://supabase.com/dashboard/project/mhiudbdnrooipovvonfb/sql)

-- 1. App Role Enum Type (Strict 5-Role Hierarchy)
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

-- 2. Unified User Roles Table (Server-Controlled Identity Mapping)
CREATE TABLE IF NOT EXISTS public.user_roles (
    user_id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    role public.app_role NOT NULL,
    department TEXT, -- Associated department (e.g. 'Information Technology', 'Mathematics') for HOD & Teacher scoping
    assigned_by UUID REFERENCES auth.users(id) ON DELETE SET NULL,
    assigned_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Index for fast authorization lookups
CREATE INDEX IF NOT EXISTS idx_user_roles_user_role ON public.user_roles (user_id, role);
CREATE INDEX IF NOT EXISTS idx_user_roles_dept ON public.user_roles (department) WHERE department IS NOT NULL;

-- 3. Enable Row Level Security (RLS)
ALTER TABLE public.user_roles ENABLE ROW LEVEL SECURITY;

-- 4. Secure Helper Functions (SECURITY DEFINER to avoid RLS recursion)

-- 4.1. Get Authenticated User's Current Role
CREATE OR REPLACE FUNCTION public.get_auth_user_role(p_user_id UUID DEFAULT auth.uid())
RETURNS public.app_role
LANGUAGE plpgsql
STABLE
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_role public.app_role;
BEGIN
    IF p_user_id IS NULL THEN
        RETURN NULL;
    END IF;

    SELECT role INTO v_role
    FROM public.user_roles
    WHERE user_id = p_user_id;

    RETURN v_role;
END;
$$;

-- 4.2. Get Authenticated User's Department
CREATE OR REPLACE FUNCTION public.get_auth_user_department(p_user_id UUID DEFAULT auth.uid())
RETURNS TEXT
LANGUAGE plpgsql
STABLE
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_dept TEXT;
BEGIN
    IF p_user_id IS NULL THEN
        RETURN NULL;
    END IF;

    SELECT department INTO v_dept
    FROM public.user_roles
    WHERE user_id = p_user_id;

    RETURN v_dept;
END;
$$;

-- 4.3. Is User Admin?
CREATE OR REPLACE FUNCTION public.is_admin(p_user_id UUID DEFAULT auth.uid())
RETURNS BOOLEAN
LANGUAGE plpgsql
STABLE
SECURITY DEFINER
SET search_path = public
AS $$
BEGIN
    IF p_user_id IS NULL THEN
        RETURN FALSE;
    END IF;

    RETURN EXISTS (
        SELECT 1
        FROM public.user_roles
        WHERE user_id = p_user_id AND role = 'admin'::public.app_role
    );
END;
$$;

-- 4.4. Is User HOD (Optionally within a specific department)?
CREATE OR REPLACE FUNCTION public.is_hod(p_user_id UUID DEFAULT auth.uid(), p_department TEXT DEFAULT NULL)
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

    -- Admins have super-admin privileges across all departments
    IF v_role = 'admin'::public.app_role THEN
        RETURN TRUE;
    END IF;

    IF v_role = 'hod'::public.app_role THEN
        IF p_department IS NULL OR p_department = '' THEN
            RETURN TRUE;
        END IF;
        RETURN LOWER(TRIM(COALESCE(v_dept, ''))) = LOWER(TRIM(p_department));
    END IF;

    RETURN FALSE;
END;
$$;

-- 4.5. Is User Teacher (Teacher, HOD, or Admin)?
CREATE OR REPLACE FUNCTION public.is_teacher(p_user_id UUID DEFAULT auth.uid())
RETURNS BOOLEAN
LANGUAGE plpgsql
STABLE
SECURITY DEFINER
SET search_path = public
AS $$
BEGIN
    IF p_user_id IS NULL THEN
        RETURN FALSE;
    END IF;

    RETURN EXISTS (
        SELECT 1
        FROM public.user_roles
        WHERE user_id = p_user_id 
          AND role IN ('teacher'::public.app_role, 'hod'::public.app_role, 'admin'::public.app_role)
    );
END;
$$;

-- 4.6. Is User Student (BS or Intermediate)?
CREATE OR REPLACE FUNCTION public.is_student(p_user_id UUID DEFAULT auth.uid())
RETURNS BOOLEAN
LANGUAGE plpgsql
STABLE
SECURITY DEFINER
SET search_path = public
AS $$
BEGIN
    IF p_user_id IS NULL THEN
        RETURN FALSE;
    END IF;

    RETURN EXISTS (
        SELECT 1
        FROM public.user_roles
        WHERE user_id = p_user_id 
          AND role IN ('student_bs'::public.app_role, 'student_intermediate'::public.app_role)
    );
END;
$$;

-- 4.7. Check if user has specific required role
CREATE OR REPLACE FUNCTION public.has_role(p_required_role public.app_role, p_user_id UUID DEFAULT auth.uid())
RETURNS BOOLEAN
LANGUAGE plpgsql
STABLE
SECURITY DEFINER
SET search_path = public
AS $$
BEGIN
    IF p_user_id IS NULL THEN
        RETURN FALSE;
    END IF;

    RETURN EXISTS (
        SELECT 1
        FROM public.user_roles
        WHERE user_id = p_user_id AND role = p_required_role
    );
END;
$$;

-- 4.8. Client-facing RPC to securely inspect current authenticated user's role
CREATE OR REPLACE FUNCTION public.get_my_role()
RETURNS JSONB
LANGUAGE plpgsql
STABLE
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_user_id UUID := auth.uid();
    v_record RECORD;
BEGIN
    IF v_user_id IS NULL THEN
        RETURN jsonb_build_object('authenticated', false, 'role', null);
    END IF;

    SELECT role, department, assigned_at INTO v_record
    FROM public.user_roles
    WHERE user_id = v_user_id;

    IF NOT FOUND THEN
        RETURN jsonb_build_object(
            'authenticated', true,
            'role', null,
            'department', null
        );
    END IF;

    RETURN jsonb_build_object(
        'authenticated', true,
        'user_id', v_user_id,
        'role', v_record.role::TEXT,
        'department', v_record.department,
        'assigned_at', v_record.assigned_at
    );
END;
$$;

-- 5. RLS Policies on `user_roles`
-- Prevent regular clients from directly modifying roles.
-- Standard users can only view their own assigned role.
-- Admins can view all roles.

DROP POLICY IF EXISTS "Users can read own role" ON public.user_roles;
CREATE POLICY "Users can read own role"
ON public.user_roles
FOR SELECT
USING (auth.uid() = user_id);

DROP POLICY IF EXISTS "Admins can read all roles" ON public.user_roles;
CREATE POLICY "Admins can read all roles"
ON public.user_roles
FOR SELECT
USING (public.is_admin());

DROP POLICY IF EXISTS "Admins can insert user roles" ON public.user_roles;
CREATE POLICY "Admins can insert user roles"
ON public.user_roles
FOR INSERT
WITH CHECK (public.is_admin());

DROP POLICY IF EXISTS "Admins can update user roles" ON public.user_roles;
CREATE POLICY "Admins can update user roles"
ON public.user_roles
FOR UPDATE
USING (public.is_admin())
WITH CHECK (public.is_admin());

DROP POLICY IF EXISTS "Admins can delete user roles" ON public.user_roles;
CREATE POLICY "Admins can delete user roles"
ON public.user_roles
FOR DELETE
USING (public.is_admin());

-- 6. Automatic Server-Side Role Provisioning Triggers
-- Ensures roles are created securely without client manipulation

-- 6.1. BS Student Trigger
CREATE OR REPLACE FUNCTION public.handle_bs_profile_created()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
BEGIN
    INSERT INTO public.user_roles (user_id, role, department, assigned_at, updated_at)
    VALUES (NEW.id, 'student_bs'::public.app_role, NEW.program, NOW(), NOW())
    ON CONFLICT (user_id) DO NOTHING;
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_bs_profile_created_role ON public.bs_student_profiles;
CREATE TRIGGER trg_bs_profile_created_role
AFTER INSERT ON public.bs_student_profiles
FOR EACH ROW
EXECUTE FUNCTION public.handle_bs_profile_created();

-- 6.2. Intermediate Student Trigger
CREATE OR REPLACE FUNCTION public.handle_intermediate_profile_created()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
BEGIN
    INSERT INTO public.user_roles (user_id, role, department, assigned_at, updated_at)
    VALUES (NEW.id, 'student_intermediate'::public.app_role, NEW.program, NOW(), NOW())
    ON CONFLICT (user_id) DO NOTHING;
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_intermediate_profile_created_role ON public.intermediate_student_profiles;
CREATE TRIGGER trg_intermediate_profile_created_role
AFTER INSERT ON public.intermediate_student_profiles
FOR EACH ROW
EXECUTE FUNCTION public.handle_intermediate_profile_created();

-- 6.3. Faculty Trigger (Teacher vs HOD designation detection)
CREATE OR REPLACE FUNCTION public.handle_faculty_profile_created()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_assigned_role public.app_role := 'teacher'::public.app_role;
BEGIN
    -- Check if designation contains HOD, Head of Department, Principal, or Vice Principal
    IF NEW.designation ILIKE '%HOD%' 
       OR NEW.designation ILIKE '%Head of Department%' 
       OR NEW.designation ILIKE '%Principal%' THEN
        v_assigned_role := 'hod'::public.app_role;
    END IF;

    INSERT INTO public.user_roles (user_id, role, department, assigned_at, updated_at)
    VALUES (NEW.id, v_assigned_role, NEW.department, NOW(), NOW())
    ON CONFLICT (user_id) DO UPDATE
    SET role = EXCLUDED.role,
        department = EXCLUDED.department,
        updated_at = NOW();

    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_faculty_profile_created_role ON public.faculty_profiles;
CREATE TRIGGER trg_faculty_profile_created_role
AFTER INSERT ON public.faculty_profiles
FOR EACH ROW
EXECUTE FUNCTION public.handle_faculty_profile_created();

-- 7. Protected Administrative Procedure for Trusted Role Assignment
CREATE OR REPLACE FUNCTION public.admin_assign_role(
    p_target_user_id UUID,
    p_new_role public.app_role,
    p_department TEXT DEFAULT NULL
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_caller_id UUID := auth.uid();
BEGIN
    -- Only existing admins can assign roles
    IF NOT public.is_admin(v_caller_id) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unauthorized: Only system administrators can assign or modify user roles.');
    END IF;

    -- Prevent modifying non-existent user
    IF NOT EXISTS (SELECT 1 FROM auth.users WHERE id = p_target_user_id) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Target user does not exist.');
    END IF;

    -- Upsert role securely
    INSERT INTO public.user_roles (
        user_id,
        role,
        department,
        assigned_by,
        assigned_at,
        updated_at
    ) VALUES (
        p_target_user_id,
        p_new_role,
        NULLIF(TRIM(p_department), ''),
        v_caller_id,
        NOW(),
        NOW()
    )
    ON CONFLICT (user_id) DO UPDATE
    SET role = EXCLUDED.role,
        department = EXCLUDED.department,
        assigned_by = EXCLUDED.assigned_by,
        updated_at = NOW();

    RETURN jsonb_build_object(
        'success', true,
        'message', 'User role updated successfully.',
        'user_id', p_target_user_id,
        'role', p_new_role::TEXT,
        'department', p_department
    );
END;
$$;

-- 8. Strengthen RLS on Official College Registries
-- Official registries can only be modified by Admins; Students & Teachers CANNOT insert, update, or delete.

-- 8.1. Official BS Students
DROP POLICY IF EXISTS "Admins have full access to official bs students" ON public.official_bs_students;
CREATE POLICY "Admins have full access to official bs students"
ON public.official_bs_students
FOR ALL
USING (public.is_admin())
WITH CHECK (public.is_admin());

-- 8.2. Official Intermediate Students
DROP POLICY IF EXISTS "Admins have full access to official intermediate students" ON public.official_intermediate_students;
CREATE POLICY "Admins have full access to official intermediate students"
ON public.official_intermediate_students
FOR ALL
USING (public.is_admin())
WITH CHECK (public.is_admin());

-- 8.3. Official Faculty
DROP POLICY IF EXISTS "Admins have full access to official faculty" ON public.official_faculty;
CREATE POLICY "Admins have full access to official faculty"
ON public.official_faculty
FOR ALL
USING (public.is_admin())
WITH CHECK (public.is_admin());
