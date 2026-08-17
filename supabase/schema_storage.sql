-- ==============================================================================
-- GGC M.B.Din Official Android App - Supabase Storage Architecture & Security
-- ==============================================================================
-- Run this SQL in your Supabase Project SQL Editor (https://supabase.com/dashboard/project/mhiudbdnrooipovvonfb/sql)
--
-- This schema establishes the storage architecture, bucket configurations,
-- and Role-Based Access Control (RBAC) Row Level Security policies on `storage.objects`.
--
-- Buckets provisioned:
-- 1. `college-prospectus`       - Official session prospectus PDFs
-- 2. `official-documents`       - Admission docs, notices, fee structures, rules, forms
-- 3. `announcement-attachments` - Circular attachments, notices, circular images
-- 4. `course-outlines`          - Departmental course curricula and syllabus documents
-- 5. `profile-photos`           - Verified faculty and student avatars
-- 6. `college-media`            - Event banners, campus photography, and ceremony assets
-- ==============================================================================

-- ==============================================================================
-- 1. PROVISION STORAGE BUCKETS
-- ==============================================================================

-- 1.1 College Prospectus Bucket (Public Read, 50MB limit, PDF only)
INSERT INTO storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
VALUES (
    'college-prospectus',
    'college-prospectus',
    TRUE,
    52428800, -- 50 MB
    ARRAY['application/pdf']
)
ON CONFLICT (id) DO UPDATE SET
    public = EXCLUDED.public,
    file_size_limit = EXCLUDED.file_size_limit,
    allowed_mime_types = EXCLUDED.allowed_mime_types;

-- 1.2 Official Documents Bucket (Public Read, 30MB limit, PDF and Word docs)
INSERT INTO storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
VALUES (
    'official-documents',
    'official-documents',
    TRUE,
    31457280, -- 30 MB
    ARRAY[
        'application/pdf',
        'application/msword',
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
        'image/jpeg',
        'image/png'
    ]
)
ON CONFLICT (id) DO UPDATE SET
    public = EXCLUDED.public,
    file_size_limit = EXCLUDED.file_size_limit,
    allowed_mime_types = EXCLUDED.allowed_mime_types;

-- 1.3 Announcements Attachments Bucket (Public Read, 25MB limit)
INSERT INTO storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
VALUES (
    'announcement-attachments',
    'announcement-attachments',
    TRUE,
    26214400, -- 25 MB
    ARRAY[
        'application/pdf',
        'image/jpeg',
        'image/png',
        'image/webp'
    ]
)
ON CONFLICT (id) DO UPDATE SET
    public = EXCLUDED.public,
    file_size_limit = EXCLUDED.file_size_limit,
    allowed_mime_types = EXCLUDED.allowed_mime_types;

-- 1.4 Course Outlines Bucket (Public Read, 20MB limit, PDF only)
INSERT INTO storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
VALUES (
    'course-outlines',
    'course-outlines',
    TRUE,
    20971520, -- 20 MB
    ARRAY['application/pdf']
)
ON CONFLICT (id) DO UPDATE SET
    public = EXCLUDED.public,
    file_size_limit = EXCLUDED.file_size_limit,
    allowed_mime_types = EXCLUDED.allowed_mime_types;

-- 1.5 Profile Photos Bucket (Public Read, 5MB limit, Images only)
INSERT INTO storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
VALUES (
    'profile-photos',
    'profile-photos',
    TRUE,
    5242880, -- 5 MB
    ARRAY[
        'image/jpeg',
        'image/png',
        'image/webp'
    ]
)
ON CONFLICT (id) DO UPDATE SET
    public = EXCLUDED.public,
    file_size_limit = EXCLUDED.file_size_limit,
    allowed_mime_types = EXCLUDED.allowed_mime_types;

-- 1.6 College Media Bucket (Public Read, 30MB limit)
INSERT INTO storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
VALUES (
    'college-media',
    'college-media',
    TRUE,
    31457280, -- 30 MB
    ARRAY[
        'image/jpeg',
        'image/png',
        'image/webp',
        'application/pdf'
    ]
)
ON CONFLICT (id) DO UPDATE SET
    public = EXCLUDED.public,
    file_size_limit = EXCLUDED.file_size_limit,
    allowed_mime_types = EXCLUDED.allowed_mime_types;

-- ==============================================================================
-- 2. HELPER FUNCTIONS FOR STORAGE AUTHORIZATION
-- ==============================================================================

-- Helper: Extracts the top-level directory segment from a storage object name
-- Example: 'it/bsit/cs301.pdf' -> 'it'
CREATE OR REPLACE FUNCTION public.get_storage_root_folder(object_name TEXT)
RETURNS TEXT
LANGUAGE sql
IMMUTABLE
AS $$
    SELECT split_part(object_name, '/', 1);
$$;

-- Helper: Validates if the authenticated user has management rights over a departmental storage folder
CREATE OR REPLACE FUNCTION public.can_manage_storage_folder(p_folder_code_or_dept TEXT)
RETURNS BOOLEAN
LANGUAGE plpgsql
STABLE
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_user_id UUID := auth.uid();
    v_user_dept TEXT;
BEGIN
    IF v_user_id IS NULL THEN
        RETURN FALSE;
    END IF;

    -- Admins have global storage management permissions
    IF public.is_admin(v_user_id) THEN
        RETURN TRUE;
    END IF;

    IF p_folder_code_or_dept IS NULL OR TRIM(p_folder_code_or_dept) = '' THEN
        RETURN FALSE;
    END IF;

    -- Fetch user's department for HOD / Teacher
    SELECT department INTO v_user_dept
    FROM public.user_roles
    WHERE user_id = v_user_id 
      AND role IN ('hod'::public.app_role, 'teacher'::public.app_role);

    IF v_user_dept IS NULL THEN
        RETURN FALSE;
    END IF;

    -- Check if folder matches department name or code
    RETURN EXISTS (
        SELECT 1 FROM public.departments
        WHERE (LOWER(TRIM(name)) = LOWER(TRIM(p_folder_code_or_dept)) OR LOWER(TRIM(code)) = LOWER(TRIM(p_folder_code_or_dept)))
          AND LOWER(TRIM(name)) = LOWER(TRIM(v_user_dept))
    );
END;
$$;

-- ==============================================================================
-- 3. STORAGE ROW LEVEL SECURITY (RLS) POLICIES ON `storage.objects`
-- ==============================================================================

-- Ensure RLS is active on storage.objects
ALTER TABLE storage.objects ENABLE ROW LEVEL SECURITY;

-- ------------------------------------------------------------------------------
-- 3.1 PUBLIC & STUDENT READ POLICIES (SELECT)
-- ------------------------------------------------------------------------------

DROP POLICY IF EXISTS "Public can view college storage files" ON storage.objects;
CREATE POLICY "Public can view college storage files"
ON storage.objects FOR SELECT
USING (
    bucket_id IN (
        'college-prospectus',
        'official-documents',
        'announcement-attachments',
        'course-outlines',
        'profile-photos',
        'college-media'
    )
);

-- ------------------------------------------------------------------------------
-- 3.2 ADMIN GLOBAL STORAGE ACCESS (INSERT, UPDATE, DELETE)
-- ------------------------------------------------------------------------------

DROP POLICY IF EXISTS "Admins have full storage management access" ON storage.objects;
CREATE POLICY "Admins have full storage management access"
ON storage.objects FOR ALL
USING (public.is_admin())
WITH CHECK (public.is_admin());

-- ------------------------------------------------------------------------------
-- 3.3 COURSE OUTLINES POLICIES (Teachers & HODs for their department)
-- ------------------------------------------------------------------------------

DROP POLICY IF EXISTS "Faculty can upload course outlines for their department" ON storage.objects;
CREATE POLICY "Faculty can upload course outlines for their department"
ON storage.objects FOR INSERT
WITH CHECK (
    bucket_id = 'course-outlines' AND
    auth.role() = 'authenticated' AND
    public.can_manage_storage_folder(public.get_storage_root_folder(name))
);

DROP POLICY IF EXISTS "Faculty can update course outlines for their department" ON storage.objects;
CREATE POLICY "Faculty can update course outlines for their department"
ON storage.objects FOR UPDATE
USING (
    bucket_id = 'course-outlines' AND
    auth.role() = 'authenticated' AND
    public.can_manage_storage_folder(public.get_storage_root_folder(name))
)
WITH CHECK (
    bucket_id = 'course-outlines' AND
    auth.role() = 'authenticated' AND
    public.can_manage_storage_folder(public.get_storage_root_folder(name))
);

DROP POLICY IF EXISTS "Faculty can delete course outlines for their department" ON storage.objects;
CREATE POLICY "Faculty can delete course outlines for their department"
ON storage.objects FOR DELETE
USING (
    bucket_id = 'course-outlines' AND
    auth.role() = 'authenticated' AND
    public.can_manage_storage_folder(public.get_storage_root_folder(name))
);

-- ------------------------------------------------------------------------------
-- 3.4 ANNOUNCEMENT ATTACHMENTS POLICIES (Department-scoped uploads for Faculty/HOD)
-- ------------------------------------------------------------------------------

DROP POLICY IF EXISTS "Faculty can upload announcement attachments for their department" ON storage.objects;
CREATE POLICY "Faculty can upload announcement attachments for their department"
ON storage.objects FOR INSERT
WITH CHECK (
    bucket_id = 'announcement-attachments' AND
    auth.role() = 'authenticated' AND
    public.can_manage_storage_folder(public.get_storage_root_folder(name))
);

DROP POLICY IF EXISTS "Faculty can update announcement attachments for their department" ON storage.objects;
CREATE POLICY "Faculty can update announcement attachments for their department"
ON storage.objects FOR UPDATE
USING (
    bucket_id = 'announcement-attachments' AND
    auth.role() = 'authenticated' AND
    public.can_manage_storage_folder(public.get_storage_root_folder(name))
)
WITH CHECK (
    bucket_id = 'announcement-attachments' AND
    auth.role() = 'authenticated' AND
    public.can_manage_storage_folder(public.get_storage_root_folder(name))
);

DROP POLICY IF EXISTS "Faculty can delete announcement attachments for their department" ON storage.objects;
CREATE POLICY "Faculty can delete announcement attachments for their department"
ON storage.objects FOR DELETE
USING (
    bucket_id = 'announcement-attachments' AND
    auth.role() = 'authenticated' AND
    public.can_manage_storage_folder(public.get_storage_root_folder(name))
);

-- ------------------------------------------------------------------------------
-- 3.5 OFFICIAL DOCUMENTS POLICIES (HOD departmental docs, Admins handle college-wide)
-- ------------------------------------------------------------------------------

DROP POLICY IF EXISTS "HODs can upload departmental official documents" ON storage.objects;
CREATE POLICY "HODs can upload departmental official documents"
ON storage.objects FOR INSERT
WITH CHECK (
    bucket_id = 'official-documents' AND
    auth.role() = 'authenticated' AND
    (
        public.is_admin() OR
        (public.is_hod() AND public.can_manage_storage_folder(split_part(name, '/', 2)))
    )
);

DROP POLICY IF EXISTS "HODs can update departmental official documents" ON storage.objects;
CREATE POLICY "HODs can update departmental official documents"
ON storage.objects FOR UPDATE
USING (
    bucket_id = 'official-documents' AND
    auth.role() = 'authenticated' AND
    (
        public.is_admin() OR
        (public.is_hod() AND public.can_manage_storage_folder(split_part(name, '/', 2)))
    )
)
WITH CHECK (
    bucket_id = 'official-documents' AND
    auth.role() = 'authenticated' AND
    (
        public.is_admin() OR
        (public.is_hod() AND public.can_manage_storage_folder(split_part(name, '/', 2)))
    )
);

DROP POLICY IF EXISTS "HODs can delete departmental official documents" ON storage.objects;
CREATE POLICY "HODs can delete departmental official documents"
ON storage.objects FOR DELETE
USING (
    bucket_id = 'official-documents' AND
    auth.role() = 'authenticated' AND
    (
        public.is_admin() OR
        (public.is_hod() AND public.can_manage_storage_folder(split_part(name, '/', 2)))
    )
);

-- ------------------------------------------------------------------------------
-- 3.6 PROFILE PHOTOS POLICIES (Users manage their own user-scoped avatar folder)
-- Path format: 'faculty/{user_id}/avatar.jpg' or 'students/{user_id}/avatar.jpg'
-- ------------------------------------------------------------------------------

DROP POLICY IF EXISTS "Users can upload their own profile photo" ON storage.objects;
CREATE POLICY "Users can upload their own profile photo"
ON storage.objects FOR INSERT
WITH CHECK (
    bucket_id = 'profile-photos' AND
    auth.role() = 'authenticated' AND
    (
        public.is_admin() OR
        name LIKE 'faculty/' || auth.uid()::text || '/%' OR
        name LIKE 'students/' || auth.uid()::text || '/%'
    )
);

DROP POLICY IF EXISTS "Users can update their own profile photo" ON storage.objects;
CREATE POLICY "Users can update their own profile photo"
ON storage.objects FOR UPDATE
USING (
    bucket_id = 'profile-photos' AND
    auth.role() = 'authenticated' AND
    (
        public.is_admin() OR
        name LIKE 'faculty/' || auth.uid()::text || '/%' OR
        name LIKE 'students/' || auth.uid()::text || '/%'
    )
)
WITH CHECK (
    bucket_id = 'profile-photos' AND
    auth.role() = 'authenticated' AND
    (
        public.is_admin() OR
        name LIKE 'faculty/' || auth.uid()::text || '/%' OR
        name LIKE 'students/' || auth.uid()::text || '/%'
    )
);

DROP POLICY IF EXISTS "Users can delete their own profile photo" ON storage.objects;
CREATE POLICY "Users can delete their own profile photo"
ON storage.objects FOR DELETE
USING (
    bucket_id = 'profile-photos' AND
    auth.role() = 'authenticated' AND
    (
        public.is_admin() OR
        name LIKE 'faculty/' || auth.uid()::text || '/%' OR
        name LIKE 'students/' || auth.uid()::text || '/%'
    )
);

-- ------------------------------------------------------------------------------
-- 3.7 PROSPECTUS & MEDIA POLICIES (Admin Only for Writes)
-- ------------------------------------------------------------------------------
-- Prospectus and College Media writes are covered exclusively by Policy 3.2 ("Admins have full storage management access").
-- No student or unprivileged user can upload or mutate files in these buckets.
