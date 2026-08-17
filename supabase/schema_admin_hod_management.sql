-- ==============================================================================
-- GGC M.B.Din Official Android App - Admin & HOD Backend Authorization & Management
-- ==============================================================================
-- Run this SQL in your Supabase Project SQL Editor (https://supabase.com/dashboard/project/mhiudbdnrooipovvonfb/sql)
--
-- This schema establishes secure server-side RPC functions, management procedures,
-- and strict authorization boundaries for Admin and HOD users.
--
-- Role Scopes:
-- 1. `admin`   - Full college-wide administrative authority across all tables and registries.
-- 2. `hod`     - Scoped management privileges strictly restricted to their assigned department.
-- 3. `teacher` - Faculty-level access without registry modification or administrative powers.
-- 4. `students`- Read-only access to published college data. Zero write/management access.
-- ==============================================================================

-- ==============================================================================
-- 1. HOD DEPARTMENT-SCOPED PROCEDURES & RPCs
-- ==============================================================================

-- 1.1. Get HOD Department Overview
-- Returns department metadata, faculty counts, program counts, and document counts.
CREATE OR REPLACE FUNCTION public.hod_get_department_overview()
RETURNS JSONB
LANGUAGE plpgsql
STABLE
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_caller_id UUID := auth.uid();
    v_user_role public.app_role;
    v_dept_name TEXT;
    v_dept_record RECORD;
    v_faculty_count INT;
    v_programs_count INT;
    v_courses_count INT;
    v_announcements_count INT;
    v_documents_count INT;
BEGIN
    IF v_caller_id IS NULL THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unauthorized: Authentication required.');
    END IF;

    SELECT role, department INTO v_user_role, v_dept_name
    FROM public.user_roles
    WHERE user_id = v_caller_id;

    IF v_user_role NOT IN ('hod'::public.app_role, 'admin'::public.app_role) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Forbidden: Caller is not an HOD or Administrator.');
    END IF;

    IF v_dept_name IS NULL AND v_user_role != 'admin'::public.app_role THEN
        RETURN jsonb_build_object('success', false, 'error', 'HOD record has no assigned department.');
    END IF;

    -- Fetch department details
    SELECT * INTO v_dept_record
    FROM public.departments
    WHERE LOWER(TRIM(name)) = LOWER(TRIM(v_dept_name))
    LIMIT 1;

    -- Fetch counts scoped to department
    SELECT COUNT(*) INTO v_faculty_count
    FROM public.faculty_profiles
    WHERE LOWER(TRIM(department)) = LOWER(TRIM(v_dept_name));

    SELECT COUNT(*) INTO v_programs_count
    FROM public.academic_programs
    WHERE department_id = v_dept_record.id;

    SELECT COUNT(*) INTO v_courses_count
    FROM public.courses
    WHERE department_id = v_dept_record.id;

    SELECT COUNT(*) INTO v_announcements_count
    FROM public.announcements
    WHERE department_id = v_dept_record.id;

    SELECT COUNT(*) INTO v_documents_count
    FROM public.official_documents
    WHERE department_id = v_dept_record.id;

    RETURN jsonb_build_object(
        'success', true,
        'department_id', v_dept_record.id,
        'department_name', COALESCE(v_dept_record.name, v_dept_name),
        'department_code', v_dept_record.code,
        'category', v_dept_record.category,
        'hod_name', v_dept_record.hod_name,
        'faculty_count', v_faculty_count,
        'programs_count', v_programs_count,
        'courses_count', v_courses_count,
        'announcements_count', v_announcements_count,
        'documents_count', v_documents_count
    );
END;
$$;

-- 1.2. Get Department Faculty Members (HOD & Admin)
CREATE OR REPLACE FUNCTION public.hod_get_department_faculty()
RETURNS JSONB
LANGUAGE plpgsql
STABLE
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_caller_id UUID := auth.uid();
    v_user_role public.app_role;
    v_dept_name TEXT;
    v_faculty_list JSONB;
BEGIN
    IF v_caller_id IS NULL THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unauthorized: Authentication required.');
    END IF;

    SELECT role, department INTO v_user_role, v_dept_name
    FROM public.user_roles
    WHERE user_id = v_caller_id;

    IF v_user_role NOT IN ('hod'::public.app_role, 'admin'::public.app_role) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Forbidden: Access restricted to HOD or Administrator.');
    END IF;

    SELECT COALESCE(jsonb_agg(
        jsonb_build_object(
            'id', fp.id,
            'faculty_id', fp.faculty_id,
            'full_name', fp.full_name,
            'designation', fp.designation,
            'qualification', fp.qualification,
            'institutional_email', fp.institutional_email,
            'phone_number', fp.phone_number,
            'created_at', fp.created_at
        ) ORDER BY fp.designation, fp.full_name
    ), '[]'::jsonb) INTO v_faculty_list
    FROM public.faculty_profiles fp
    WHERE LOWER(TRIM(fp.department)) = LOWER(TRIM(v_dept_name));

    RETURN jsonb_build_object(
        'success', true,
        'department', v_dept_name,
        'faculty', v_faculty_list
    );
END;
$$;

-- 1.3. Manage Department Announcement (HOD Scoped)
CREATE OR REPLACE FUNCTION public.hod_manage_announcement(
    p_id UUID DEFAULT NULL,
    p_title TEXT DEFAULT NULL,
    p_content TEXT DEFAULT NULL,
    p_category TEXT DEFAULT 'Academic',
    p_is_pinned BOOLEAN DEFAULT FALSE,
    p_is_published BOOLEAN DEFAULT TRUE,
    p_attachment_storage_path TEXT DEFAULT NULL,
    p_attachment_name TEXT DEFAULT NULL,
    p_attachment_size_bytes BIGINT DEFAULT NULL
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_caller_id UUID := auth.uid();
    v_user_role public.app_role;
    v_dept_name TEXT;
    v_dept_id UUID;
    v_caller_name TEXT := 'Department Head';
    v_result_id UUID;
BEGIN
    IF v_caller_id IS NULL THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unauthorized: Authentication required.');
    END IF;

    SELECT role, department INTO v_user_role, v_dept_name
    FROM public.user_roles
    WHERE user_id = v_caller_id;

    IF v_user_role NOT IN ('hod'::public.app_role, 'admin'::public.app_role) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Forbidden: Only HOD or Admin can manage announcements.');
    END IF;

    -- Look up department ID
    SELECT id INTO v_dept_id
    FROM public.departments
    WHERE LOWER(TRIM(name)) = LOWER(TRIM(v_dept_name))
    LIMIT 1;

    IF v_dept_id IS NULL AND v_user_role != 'admin'::public.app_role THEN
        RETURN jsonb_build_object('success', false, 'error', 'HOD department not found in database.');
    END IF;

    -- Fetch caller full name for author attribution
    SELECT full_name INTO v_caller_name
    FROM public.faculty_profiles
    WHERE id = v_caller_id;

    IF p_id IS NULL THEN
        -- Insert new department announcement
        INSERT INTO public.announcements (
            title,
            content,
            category,
            department_id,
            author_id,
            author_name,
            is_pinned,
            is_published,
            attachment_storage_path,
            attachment_name,
            attachment_size_bytes,
            created_at,
            updated_at
        ) VALUES (
            TRIM(p_title),
            TRIM(p_content),
            p_category,
            v_dept_id,
            v_caller_id,
            COALESCE(v_caller_name, 'Department HOD'),
            p_is_pinned,
            p_is_published,
            p_attachment_storage_path,
            p_attachment_name,
            p_attachment_size_bytes,
            NOW(),
            NOW()
        ) RETURNING id INTO v_result_id;
    ELSE
        -- Update existing announcement (strictly verify department ownership)
        UPDATE public.announcements
        SET title = COALESCE(TRIM(p_title), title),
            content = COALESCE(TRIM(p_content), content),
            category = COALESCE(p_category, category),
            is_pinned = COALESCE(p_is_pinned, is_pinned),
            is_published = COALESCE(p_is_published, is_published),
            attachment_storage_path = COALESCE(p_attachment_storage_path, attachment_storage_path),
            attachment_name = COALESCE(p_attachment_name, attachment_name),
            attachment_size_bytes = COALESCE(p_attachment_size_bytes, attachment_size_bytes),
            updated_at = NOW()
        WHERE id = p_id 
          AND (v_user_role = 'admin'::public.app_role OR department_id = v_dept_id)
        RETURNING id INTO v_result_id;

        IF v_result_id IS NULL THEN
            RETURN jsonb_build_object('success', false, 'error', 'Announcement not found or cross-department modification denied.');
        END IF;
    END IF;

    RETURN jsonb_build_object(
        'success', true,
        'announcement_id', v_result_id,
        'message', 'Announcement saved successfully.'
    );
END;
$$;

-- 1.4. Delete Department Announcement (HOD Scoped)
CREATE OR REPLACE FUNCTION public.hod_delete_announcement(p_id UUID)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_caller_id UUID := auth.uid();
    v_user_role public.app_role;
    v_dept_name TEXT;
    v_dept_id UUID;
    v_deleted_count INT;
BEGIN
    IF v_caller_id IS NULL THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unauthorized: Authentication required.');
    END IF;

    SELECT role, department INTO v_user_role, v_dept_name
    FROM public.user_roles
    WHERE user_id = v_caller_id;

    IF v_user_role NOT IN ('hod'::public.app_role, 'admin'::public.app_role) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Forbidden: Only HOD or Admin can delete announcements.');
    END IF;

    SELECT id INTO v_dept_id
    FROM public.departments
    WHERE LOWER(TRIM(name)) = LOWER(TRIM(v_dept_name))
    LIMIT 1;

    DELETE FROM public.announcements
    WHERE id = p_id 
      AND (v_user_role = 'admin'::public.app_role OR department_id = v_dept_id);

    GET DIAGNOSTICS v_deleted_count = ROW_COUNT;

    IF v_deleted_count = 0 THEN
        RETURN jsonb_build_object('success', false, 'error', 'Announcement not found or belongs to another department.');
    END IF;

    RETURN jsonb_build_object('success', true, 'message', 'Announcement deleted successfully.');
END;
$$;

-- 1.5. Manage Department Course Outline (HOD Scoped)
CREATE OR REPLACE FUNCTION public.hod_manage_course_outline(
    p_id UUID DEFAULT NULL,
    p_course_id UUID DEFAULT NULL,
    p_title TEXT DEFAULT NULL,
    p_session_year TEXT DEFAULT NULL,
    p_semester_number INT DEFAULT 1,
    p_outline_content TEXT DEFAULT NULL,
    p_storage_path TEXT DEFAULT NULL,
    p_file_name TEXT DEFAULT NULL,
    p_file_size_bytes BIGINT DEFAULT NULL,
    p_is_published BOOLEAN DEFAULT TRUE
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_caller_id UUID := auth.uid();
    v_user_role public.app_role;
    v_dept_name TEXT;
    v_dept_id UUID;
    v_course_dept_id UUID;
    v_course_prog_id UUID;
    v_result_id UUID;
BEGIN
    IF v_caller_id IS NULL THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unauthorized: Authentication required.');
    END IF;

    SELECT role, department INTO v_user_role, v_dept_name
    FROM public.user_roles
    WHERE user_id = v_caller_id;

    IF v_user_role NOT IN ('hod'::public.app_role, 'admin'::public.app_role) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Forbidden: Only HOD or Admin can manage course outlines.');
    END IF;

    SELECT id INTO v_dept_id
    FROM public.departments
    WHERE LOWER(TRIM(name)) = LOWER(TRIM(v_dept_name))
    LIMIT 1;

    -- Validate that the target course belongs to the HOD's department
    SELECT department_id, program_id INTO v_course_dept_id, v_course_prog_id
    FROM public.courses
    WHERE id = p_course_id;

    IF v_course_dept_id IS NULL THEN
        RETURN jsonb_build_object('success', false, 'error', 'Specified course does not exist.');
    END IF;

    IF v_user_role != 'admin'::public.app_role AND v_course_dept_id != v_dept_id THEN
        RETURN jsonb_build_object('success', false, 'error', 'Cross-department violation: Cannot manage outline for other departments.');
    END IF;

    IF p_id IS NULL THEN
        INSERT INTO public.course_outlines (
            course_id,
            program_id,
            department_id,
            title,
            session_year,
            semester_number,
            outline_content,
            storage_path,
            file_name,
            file_size_bytes,
            is_published,
            created_by,
            created_at,
            updated_at
        ) VALUES (
            p_course_id,
            v_course_prog_id,
            v_course_dept_id,
            TRIM(p_title),
            TRIM(p_session_year),
            p_semester_number,
            p_outline_content,
            p_storage_path,
            p_file_name,
            p_file_size_bytes,
            p_is_published,
            v_caller_id,
            NOW(),
            NOW()
        )
        ON CONFLICT (course_id, session_year) DO UPDATE
        SET title = EXCLUDED.title,
            semester_number = EXCLUDED.semester_number,
            outline_content = EXCLUDED.outline_content,
            storage_path = EXCLUDED.storage_path,
            file_name = EXCLUDED.file_name,
            file_size_bytes = EXCLUDED.file_size_bytes,
            is_published = EXCLUDED.is_published,
            updated_at = NOW()
        RETURNING id INTO v_result_id;
    ELSE
        UPDATE public.course_outlines
        SET title = COALESCE(TRIM(p_title), title),
            session_year = COALESCE(TRIM(p_session_year), session_year),
            semester_number = COALESCE(p_semester_number, semester_number),
            outline_content = COALESCE(p_outline_content, outline_content),
            storage_path = COALESCE(p_storage_path, storage_path),
            file_name = COALESCE(p_file_name, file_name),
            file_size_bytes = COALESCE(p_file_size_bytes, file_size_bytes),
            is_published = COALESCE(p_is_published, is_published),
            updated_at = NOW()
        WHERE id = p_id
          AND (v_user_role = 'admin'::public.app_role OR department_id = v_dept_id)
        RETURNING id INTO v_result_id;

        IF v_result_id IS NULL THEN
            RETURN jsonb_build_object('success', false, 'error', 'Course outline not found or permission denied.');
        END IF;
    END IF;

    RETURN jsonb_build_object(
        'success', true,
        'course_outline_id', v_result_id,
        'message', 'Course outline updated successfully.'
    );
END;
$$;

-- ==============================================================================
-- 2. ADMIN COLLEGE-WIDE PROCEDURES & RPCs
-- ==============================================================================

-- 2.1. Admin System Overview
CREATE OR REPLACE FUNCTION public.admin_get_system_overview()
RETURNS JSONB
LANGUAGE plpgsql
STABLE
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_caller_id UUID := auth.uid();
    v_bs_students_count INT;
    v_inter_students_count INT;
    v_faculty_count INT;
    v_hods_count INT;
    v_admins_count INT;
    v_depts_count INT;
    v_programs_count INT;
    v_courses_count INT;
    v_announcements_count INT;
    v_documents_count INT;
    v_events_count INT;
    v_prospectus_count INT;
BEGIN
    IF NOT public.is_admin(v_caller_id) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unauthorized: Only system administrators can access system overview.');
    END IF;

    SELECT COUNT(*) INTO v_bs_students_count FROM public.bs_student_profiles;
    SELECT COUNT(*) INTO v_inter_students_count FROM public.intermediate_student_profiles;
    SELECT COUNT(*) INTO v_faculty_count FROM public.faculty_profiles;
    SELECT COUNT(*) INTO v_hods_count FROM public.user_roles WHERE role = 'hod'::public.app_role;
    SELECT COUNT(*) INTO v_admins_count FROM public.user_roles WHERE role = 'admin'::public.app_role;
    SELECT COUNT(*) INTO v_depts_count FROM public.departments;
    SELECT COUNT(*) INTO v_programs_count FROM public.academic_programs;
    SELECT COUNT(*) INTO v_courses_count FROM public.courses;
    SELECT COUNT(*) INTO v_announcements_count FROM public.announcements;
    SELECT COUNT(*) INTO v_documents_count FROM public.official_documents;
    SELECT COUNT(*) INTO v_events_count FROM public.college_events;
    SELECT COUNT(*) INTO v_prospectus_count FROM public.prospectus;

    RETURN jsonb_build_object(
        'success', true,
        'bs_students_count', v_bs_students_count,
        'intermediate_students_count', v_inter_students_count,
        'faculty_count', v_faculty_count,
        'hods_count', v_hods_count,
        'admins_count', v_admins_count,
        'departments_count', v_depts_count,
        'programs_count', v_programs_count,
        'courses_count', v_courses_count,
        'announcements_count', v_announcements_count,
        'documents_count', v_documents_count,
        'events_count', v_events_count,
        'prospectus_count', v_prospectus_count
    );
END;
$$;

-- 2.2. Admin Assign / Promote HOD
CREATE OR REPLACE FUNCTION public.admin_assign_hod(
    p_faculty_user_id UUID,
    p_department_name TEXT
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_caller_id UUID := auth.uid();
    v_dept_clean TEXT := TRIM(p_department_name);
    v_faculty_record RECORD;
BEGIN
    IF NOT public.is_admin(v_caller_id) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unauthorized: Only system administrators can assign HODs.');
    END IF;

    -- Verify faculty user exists
    SELECT id, full_name, designation INTO v_faculty_record
    FROM public.faculty_profiles
    WHERE id = p_faculty_user_id;

    IF v_faculty_record.id IS NULL THEN
        RETURN jsonb_build_object('success', false, 'error', 'Target user is not a verified faculty member.');
    END IF;

    -- Validate department exists
    IF NOT EXISTS (SELECT 1 FROM public.departments WHERE LOWER(TRIM(name)) = LOWER(v_dept_clean)) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Specified department does not exist.');
    END IF;

    -- Assign HOD role in user_roles
    INSERT INTO public.user_roles (
        user_id,
        role,
        department,
        assigned_by,
        assigned_at,
        updated_at
    ) VALUES (
        p_faculty_user_id,
        'hod'::public.app_role,
        v_dept_clean,
        v_caller_id,
        NOW(),
        NOW()
    )
    ON CONFLICT (user_id) DO UPDATE
    SET role = 'hod'::public.app_role,
        department = v_dept_clean,
        assigned_by = v_caller_id,
        updated_at = NOW();

    -- Update department table with HOD name
    UPDATE public.departments
    SET hod_name = v_faculty_record.full_name,
        updated_at = NOW()
    WHERE LOWER(TRIM(name)) = LOWER(v_dept_clean);

    RETURN jsonb_build_object(
        'success', true,
        'message', 'Faculty member assigned as Head of Department successfully.',
        'user_id', p_faculty_user_id,
        'department', v_dept_clean,
        'faculty_name', v_faculty_record.full_name
    );
END;
$$;

-- 2.3. Admin Revoke HOD Status (Revert to Teacher)
CREATE OR REPLACE FUNCTION public.admin_revoke_hod(
    p_target_user_id UUID
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_caller_id UUID := auth.uid();
    v_dept_name TEXT;
BEGIN
    IF NOT public.is_admin(v_caller_id) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unauthorized: Only system administrators can revoke HOD status.');
    END IF;

    SELECT department INTO v_dept_name
    FROM public.user_roles
    WHERE user_id = p_target_user_id;

    UPDATE public.user_roles
    SET role = 'teacher'::public.app_role,
        assigned_by = v_caller_id,
        updated_at = NOW()
    WHERE user_id = p_target_user_id
      AND role = 'hod'::public.app_role;

    IF NOT FOUND THEN
        RETURN jsonb_build_object('success', false, 'error', 'User is not currently an HOD.');
    END IF;

    RETURN jsonb_build_object(
        'success', true,
        'message', 'HOD status revoked; user reverted to Teacher role.',
        'user_id', p_target_user_id
    );
END;
$$;

-- 2.4. Admin Manage Official BS Student Registry
CREATE OR REPLACE FUNCTION public.admin_manage_official_bs_student(
    p_id UUID DEFAULT NULL,
    p_roll_number TEXT DEFAULT NULL,
    p_registration_number TEXT DEFAULT NULL,
    p_program TEXT DEFAULT NULL,
    p_session TEXT DEFAULT NULL,
    p_first_name TEXT DEFAULT NULL,
    p_last_name TEXT DEFAULT NULL
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_caller_id UUID := auth.uid();
    v_result_id UUID;
BEGIN
    IF NOT public.is_admin(v_caller_id) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unauthorized: Only system administrators can manage student registries.');
    END IF;

    IF p_id IS NULL THEN
        INSERT INTO public.official_bs_students (
            roll_number,
            registration_number,
            program,
            session,
            first_name,
            last_name,
            created_at
        ) VALUES (
            UPPER(TRIM(p_roll_number)),
            UPPER(TRIM(p_registration_number)),
            TRIM(p_program),
            TRIM(p_session),
            TRIM(p_first_name),
            TRIM(p_last_name),
            NOW()
        ) RETURNING id INTO v_result_id;
    ELSE
        UPDATE public.official_bs_students
        SET roll_number = COALESCE(UPPER(TRIM(p_roll_number)), roll_number),
            registration_number = COALESCE(UPPER(TRIM(p_registration_number)), registration_number),
            program = COALESCE(TRIM(p_program), program),
            session = COALESCE(TRIM(p_session), session),
            first_name = COALESCE(TRIM(p_first_name), first_name),
            last_name = COALESCE(TRIM(p_last_name), last_name)
        WHERE id = p_id
        RETURNING id INTO v_result_id;
    END IF;

    RETURN jsonb_build_object('success', true, 'record_id', v_result_id);
END;
$$;

-- 2.5. Admin Manage Official Faculty Registry
CREATE OR REPLACE FUNCTION public.admin_manage_official_faculty(
    p_id UUID DEFAULT NULL,
    p_faculty_id TEXT DEFAULT NULL,
    p_full_name TEXT DEFAULT NULL,
    p_department TEXT DEFAULT NULL,
    p_designation TEXT DEFAULT NULL,
    p_qualification TEXT DEFAULT NULL,
    p_institutional_email TEXT DEFAULT NULL
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_caller_id UUID := auth.uid();
    v_result_id UUID;
BEGIN
    IF NOT public.is_admin(v_caller_id) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unauthorized: Only system administrators can manage faculty registry.');
    END IF;

    IF p_id IS NULL THEN
        INSERT INTO public.official_faculty (
            faculty_id,
            full_name,
            department,
            designation,
            qualification,
            institutional_email,
            created_at
        ) VALUES (
            UPPER(TRIM(p_faculty_id)),
            TRIM(p_full_name),
            TRIM(p_department),
            TRIM(p_designation),
            TRIM(p_qualification),
            LOWER(TRIM(NULLIF(p_institutional_email, ''))),
            NOW()
        ) RETURNING id INTO v_result_id;
    ELSE
        UPDATE public.official_faculty
        SET faculty_id = COALESCE(UPPER(TRIM(p_faculty_id)), faculty_id),
            full_name = COALESCE(TRIM(p_full_name), full_name),
            department = COALESCE(TRIM(p_department), department),
            designation = COALESCE(TRIM(p_designation), designation),
            qualification = COALESCE(TRIM(p_qualification), qualification),
            institutional_email = COALESCE(LOWER(TRIM(NULLIF(p_institutional_email, ''))), institutional_email)
        WHERE id = p_id
        RETURNING id INTO v_result_id;
    END IF;

    RETURN jsonb_build_object('success', true, 'record_id', v_result_id);
END;
$$;

-- 2.6. Admin Manage Prospectus Metadata
CREATE OR REPLACE FUNCTION public.admin_manage_prospectus(
    p_id UUID DEFAULT NULL,
    p_title TEXT DEFAULT NULL,
    p_academic_session TEXT DEFAULT NULL,
    p_program_level TEXT DEFAULT 'Comprehensive',
    p_description TEXT DEFAULT NULL,
    p_storage_path TEXT DEFAULT NULL,
    p_file_name TEXT DEFAULT NULL,
    p_file_size_bytes BIGINT DEFAULT NULL,
    p_cover_image_storage_path TEXT DEFAULT NULL,
    p_is_current BOOLEAN DEFAULT FALSE,
    p_is_published BOOLEAN DEFAULT TRUE
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_caller_id UUID := auth.uid();
    v_result_id UUID;
BEGIN
    IF NOT public.is_admin(v_caller_id) THEN
        RETURN jsonb_build_object('success', false, 'error', 'Unauthorized: Only system administrators can manage prospectus records.');
    END IF;

    -- If setting is_current to true, unset is_current for other prospectus records
    IF p_is_current = TRUE THEN
        UPDATE public.prospectus SET is_current = FALSE WHERE is_current = TRUE;
    END IF;

    IF p_id IS NULL THEN
        INSERT INTO public.prospectus (
            title,
            academic_session,
            program_level,
            description,
            storage_path,
            file_name,
            file_size_bytes,
            cover_image_storage_path,
            is_current,
            is_published,
            uploaded_by,
            created_at,
            updated_at
        ) VALUES (
            TRIM(p_title),
            TRIM(p_academic_session),
            p_program_level,
            TRIM(p_description),
            TRIM(p_storage_path),
            TRIM(p_file_name),
            p_file_size_bytes,
            p_cover_image_storage_path,
            p_is_current,
            p_is_published,
            v_caller_id,
            NOW(),
            NOW()
        ) RETURNING id INTO v_result_id;
    ELSE
        UPDATE public.prospectus
        SET title = COALESCE(TRIM(p_title), title),
            academic_session = COALESCE(TRIM(p_academic_session), academic_session),
            program_level = COALESCE(p_program_level, program_level),
            description = COALESCE(TRIM(p_description), description),
            storage_path = COALESCE(TRIM(p_storage_path), storage_path),
            file_name = COALESCE(TRIM(p_file_name), file_name),
            file_size_bytes = COALESCE(p_file_size_bytes, file_size_bytes),
            cover_image_storage_path = COALESCE(p_cover_image_storage_path, cover_image_storage_path),
            is_current = COALESCE(p_is_current, is_current),
            is_published = COALESCE(p_is_published, is_published),
            updated_at = NOW()
        WHERE id = p_id
        RETURNING id INTO v_result_id;
    END IF;

    RETURN jsonb_build_object('success', true, 'prospectus_id', v_result_id);
END;
$$;
