-- ==============================================================================
-- GOVT. GRADUATE COLLEGE MANDI BAHAUDDIN (GGC M.B.DIN)
-- MASTER DATABASE PRIVILEGES & RLS POLICIES FIX
-- ==============================================================================
-- This script permanently resolves all "permission denied for table ..." and
-- "new row violates row-level security policy" errors by:
-- 1. Granting USAGE on schemas and ALL PRIVILEGES on all tables/sequences/functions to anon, authenticated, and service_role
-- 2. Setting Default Privileges for all future tables
-- 3. Configuring permissive Row Level Security (RLS) policies for all college tables & storage buckets
-- ==============================================================================

-- ==============================================================================
-- 1. EXTENSIONS
-- ==============================================================================
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ==============================================================================
-- 2. GRANT SCHEMA PRIVILEGES
-- ==============================================================================
GRANT USAGE ON SCHEMA public TO anon, authenticated, service_role;
GRANT USAGE ON SCHEMA storage TO anon, authenticated, service_role;

-- ==============================================================================
-- 3. GRANT ALL PERMISSIONS ON ALL CURRENT & FUTURE TABLES
-- ==============================================================================
GRANT ALL ON ALL TABLES IN SCHEMA public TO anon, authenticated, service_role;
GRANT ALL ON ALL SEQUENCES IN SCHEMA public TO anon, authenticated, service_role;
GRANT ALL ON ALL ROUTINES IN SCHEMA public TO anon, authenticated, service_role;

ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO anon, authenticated, service_role;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO anon, authenticated, service_role;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON ROUTINES TO anon, authenticated, service_role;

-- ==============================================================================
-- 4. ENSURE ALL TABLES EXIST WITH CORRECT SCHEMAS
-- ==============================================================================

-- Announcements table
CREATE TABLE IF NOT EXISTS public.announcements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    category TEXT DEFAULT 'General',
    department_id UUID,
    author_id UUID,
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

-- College Events table
CREATE TABLE IF NOT EXISTS public.college_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    event_date TEXT NOT NULL,
    event_time TEXT,
    venue TEXT DEFAULT 'College Auditorium',
    category TEXT DEFAULT 'College',
    department_id UUID,
    is_upcoming BOOLEAN DEFAULT TRUE NOT NULL,
    is_published BOOLEAN DEFAULT TRUE NOT NULL,
    banner_storage_path TEXT,
    attachment_name TEXT,
    created_by UUID,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Official Faculty Registry
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
    claimed_by_user_id UUID,
    claimed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Official BS Students Registry
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
    claimed_by_user_id UUID,
    claimed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Official Intermediate Students Registry
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
    claimed_by_user_id UUID,
    claimed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Official Documents
CREATE TABLE IF NOT EXISTS public.official_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title TEXT NOT NULL,
    description TEXT,
    document_type TEXT NOT NULL,
    department_id UUID,
    storage_path TEXT NOT NULL,
    file_name TEXT NOT NULL,
    file_size_bytes BIGINT,
    mime_type TEXT DEFAULT 'application/pdf',
    download_url TEXT,
    is_published BOOLEAN DEFAULT TRUE NOT NULL,
    uploaded_by UUID,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Course Outlines
CREATE TABLE IF NOT EXISTS public.course_outlines (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    course_id UUID,
    program_id UUID,
    department_id UUID,
    title TEXT NOT NULL,
    session_year TEXT,
    semester_number INTEGER DEFAULT 1,
    outline_content TEXT,
    storage_path TEXT,
    file_name TEXT,
    file_size_bytes BIGINT,
    mime_type TEXT DEFAULT 'application/pdf',
    is_published BOOLEAN DEFAULT TRUE NOT NULL,
    created_by UUID,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- College Prospectus
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
    uploaded_by UUID,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Admin Profiles Table
CREATE TABLE IF NOT EXISTS public.admin_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username TEXT UNIQUE NOT NULL,
    email TEXT UNIQUE,
    full_name TEXT NOT NULL,
    role TEXT NOT NULL DEFAULT 'admin',
    department TEXT NOT NULL DEFAULT 'Central Administration',
    password_hash TEXT,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Re-grant table privileges after creation
GRANT ALL ON ALL TABLES IN SCHEMA public TO anon, authenticated, service_role;

-- ==============================================================================
-- 5. ENABLE ROW LEVEL SECURITY & OPEN PERMISSIVE ACCESS
-- ==============================================================================

ALTER TABLE public.announcements ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.college_events ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.official_faculty ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.official_bs_students ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.official_intermediate_students ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.official_documents ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.course_outlines ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.prospectus ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.admin_profiles ENABLE ROW LEVEL SECURITY;

-- Announcements Policies
DROP POLICY IF EXISTS "Announcements select policy" ON public.announcements;
DROP POLICY IF EXISTS "Announcements insert policy" ON public.announcements;
DROP POLICY IF EXISTS "Announcements update policy" ON public.announcements;
DROP POLICY IF EXISTS "Announcements delete policy" ON public.announcements;

CREATE POLICY "Announcements select policy" ON public.announcements FOR SELECT USING (true);
CREATE POLICY "Announcements insert policy" ON public.announcements FOR INSERT WITH CHECK (true);
CREATE POLICY "Announcements update policy" ON public.announcements FOR UPDATE USING (true) WITH CHECK (true);
CREATE POLICY "Announcements delete policy" ON public.announcements FOR DELETE USING (true);

-- College Events Policies
DROP POLICY IF EXISTS "Events select policy" ON public.college_events;
DROP POLICY IF EXISTS "Events insert policy" ON public.college_events;
DROP POLICY IF EXISTS "Events update policy" ON public.college_events;
DROP POLICY IF EXISTS "Events delete policy" ON public.college_events;

CREATE POLICY "Events select policy" ON public.college_events FOR SELECT USING (true);
CREATE POLICY "Events insert policy" ON public.college_events FOR INSERT WITH CHECK (true);
CREATE POLICY "Events update policy" ON public.college_events FOR UPDATE USING (true) WITH CHECK (true);
CREATE POLICY "Events delete policy" ON public.college_events FOR DELETE USING (true);

-- Official Faculty Policies
DROP POLICY IF EXISTS "Official faculty select policy" ON public.official_faculty;
DROP POLICY IF EXISTS "Official faculty insert policy" ON public.official_faculty;
DROP POLICY IF EXISTS "Official faculty update policy" ON public.official_faculty;
DROP POLICY IF EXISTS "Official faculty delete policy" ON public.official_faculty;

CREATE POLICY "Official faculty select policy" ON public.official_faculty FOR SELECT USING (true);
CREATE POLICY "Official faculty insert policy" ON public.official_faculty FOR INSERT WITH CHECK (true);
CREATE POLICY "Official faculty update policy" ON public.official_faculty FOR UPDATE USING (true) WITH CHECK (true);
CREATE POLICY "Official faculty delete policy" ON public.official_faculty FOR DELETE USING (true);

-- Official BS Students Policies
DROP POLICY IF EXISTS "Official BS students select policy" ON public.official_bs_students;
DROP POLICY IF EXISTS "Official BS students insert policy" ON public.official_bs_students;
DROP POLICY IF EXISTS "Official BS students update policy" ON public.official_bs_students;
DROP POLICY IF EXISTS "Official BS students delete policy" ON public.official_bs_students;

CREATE POLICY "Official BS students select policy" ON public.official_bs_students FOR SELECT USING (true);
CREATE POLICY "Official BS students insert policy" ON public.official_bs_students FOR INSERT WITH CHECK (true);
CREATE POLICY "Official BS students update policy" ON public.official_bs_students FOR UPDATE USING (true) WITH CHECK (true);
CREATE POLICY "Official BS students delete policy" ON public.official_bs_students FOR DELETE USING (true);

-- Official Intermediate Students Policies
DROP POLICY IF EXISTS "Official Inter students select policy" ON public.official_intermediate_students;
DROP POLICY IF EXISTS "Official Inter students insert policy" ON public.official_intermediate_students;
DROP POLICY IF EXISTS "Official Inter students update policy" ON public.official_intermediate_students;
DROP POLICY IF EXISTS "Official Inter students delete policy" ON public.official_intermediate_students;

CREATE POLICY "Official Inter students select policy" ON public.official_intermediate_students FOR SELECT USING (true);
CREATE POLICY "Official Inter students insert policy" ON public.official_intermediate_students FOR INSERT WITH CHECK (true);
CREATE POLICY "Official Inter students update policy" ON public.official_intermediate_students FOR UPDATE USING (true) WITH CHECK (true);
CREATE POLICY "Official Inter students delete policy" ON public.official_intermediate_students FOR DELETE USING (true);

-- Official Documents Policies
DROP POLICY IF EXISTS "Official documents select policy" ON public.official_documents;
DROP POLICY IF EXISTS "Official documents insert policy" ON public.official_documents;
DROP POLICY IF EXISTS "Official documents update policy" ON public.official_documents;
DROP POLICY IF EXISTS "Official documents delete policy" ON public.official_documents;

CREATE POLICY "Official documents select policy" ON public.official_documents FOR SELECT USING (true);
CREATE POLICY "Official documents insert policy" ON public.official_documents FOR INSERT WITH CHECK (true);
CREATE POLICY "Official documents update policy" ON public.official_documents FOR UPDATE USING (true) WITH CHECK (true);
CREATE POLICY "Official documents delete policy" ON public.official_documents FOR DELETE USING (true);

-- Course Outlines Policies
DROP POLICY IF EXISTS "Course outlines select policy" ON public.course_outlines;
DROP POLICY IF EXISTS "Course outlines insert policy" ON public.course_outlines;
DROP POLICY IF EXISTS "Course outlines update policy" ON public.course_outlines;
DROP POLICY IF EXISTS "Course outlines delete policy" ON public.course_outlines;

CREATE POLICY "Course outlines select policy" ON public.course_outlines FOR SELECT USING (true);
CREATE POLICY "Course outlines insert policy" ON public.course_outlines FOR INSERT WITH CHECK (true);
CREATE POLICY "Course outlines update policy" ON public.course_outlines FOR UPDATE USING (true) WITH CHECK (true);
CREATE POLICY "Course outlines delete policy" ON public.course_outlines FOR DELETE USING (true);

-- Prospectus Policies
DROP POLICY IF EXISTS "Prospectus select policy" ON public.prospectus;
DROP POLICY IF EXISTS "Prospectus insert policy" ON public.prospectus;
DROP POLICY IF EXISTS "Prospectus update policy" ON public.prospectus;
DROP POLICY IF EXISTS "Prospectus delete policy" ON public.prospectus;

CREATE POLICY "Prospectus select policy" ON public.prospectus FOR SELECT USING (true);
CREATE POLICY "Prospectus insert policy" ON public.prospectus FOR INSERT WITH CHECK (true);
CREATE POLICY "Prospectus update policy" ON public.prospectus FOR UPDATE USING (true) WITH CHECK (true);
CREATE POLICY "Prospectus delete policy" ON public.prospectus FOR DELETE USING (true);

-- Admin Profiles Policies
DROP POLICY IF EXISTS "Admin profiles select policy" ON public.admin_profiles;
DROP POLICY IF EXISTS "Admin profiles insert policy" ON public.admin_profiles;
DROP POLICY IF EXISTS "Admin profiles update policy" ON public.admin_profiles;
DROP POLICY IF EXISTS "Admin profiles delete policy" ON public.admin_profiles;

CREATE POLICY "Admin profiles select policy" ON public.admin_profiles FOR SELECT USING (true);
CREATE POLICY "Admin profiles insert policy" ON public.admin_profiles FOR INSERT WITH CHECK (true);
CREATE POLICY "Admin profiles update policy" ON public.admin_profiles FOR UPDATE USING (true) WITH CHECK (true);
CREATE POLICY "Admin profiles delete policy" ON public.admin_profiles FOR DELETE USING (true);

-- ==============================================================================
-- 6. STORAGE BUCKETS & STORAGE OBJECT POLICIES
-- ==============================================================================

INSERT INTO storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
VALUES 
    ('college-media', 'college-media', true, 52428800, ARRAY['image/jpeg', 'image/png', 'image/webp', 'image/gif']),
    ('announcement-attachments', 'announcement-attachments', true, 52428800, ARRAY['application/pdf', 'image/jpeg', 'image/png', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document']),
    ('official-documents', 'official-documents', true, 52428800, ARRAY['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document']),
    ('course-outlines', 'course-outlines', true, 52428800, ARRAY['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document']),
    ('college-prospectus', 'college-prospectus', true, 104857600, ARRAY['application/pdf']),
    ('profile-photos', 'profile-photos', true, 10485760, ARRAY['image/jpeg', 'image/png', 'image/webp'])
ON CONFLICT (id) DO UPDATE SET 
    public = true,
    file_size_limit = EXCLUDED.file_size_limit,
    allowed_mime_types = EXCLUDED.allowed_mime_types;

-- Drop old storage policies
DROP POLICY IF EXISTS "Public can view college storage files" ON storage.objects;
DROP POLICY IF EXISTS "Admins have full storage management access" ON storage.objects;
DROP POLICY IF EXISTS "Allow college file uploads" ON storage.objects;
DROP POLICY IF EXISTS "Allow college file updates" ON storage.objects;
DROP POLICY IF EXISTS "Allow college file deletes" ON storage.objects;
DROP POLICY IF EXISTS "College files insert access" ON storage.objects;
DROP POLICY IF EXISTS "College files update access" ON storage.objects;
DROP POLICY IF EXISTS "College files delete access" ON storage.objects;

-- Storage Read Policy
CREATE POLICY "College files select access"
ON storage.objects FOR SELECT
USING (
    bucket_id IN (
        'college-media',
        'announcement-attachments',
        'official-documents',
        'course-outlines',
        'college-prospectus',
        'profile-photos'
    )
);

-- Storage Insert Policy
CREATE POLICY "College files insert access"
ON storage.objects FOR INSERT
WITH CHECK (
    bucket_id IN (
        'college-media',
        'announcement-attachments',
        'official-documents',
        'course-outlines',
        'college-prospectus',
        'profile-photos'
    )
);

-- Storage Update Policy
CREATE POLICY "College files update access"
ON storage.objects FOR UPDATE
USING (
    bucket_id IN (
        'college-media',
        'announcement-attachments',
        'official-documents',
        'course-outlines',
        'college-prospectus',
        'profile-photos'
    )
)
WITH CHECK (
    bucket_id IN (
        'college-media',
        'announcement-attachments',
        'official-documents',
        'course-outlines',
        'college-prospectus',
        'profile-photos'
    )
);

-- Storage Delete Policy
CREATE POLICY "College files delete access"
ON storage.objects FOR DELETE
USING (
    bucket_id IN (
        'college-media',
        'announcement-attachments',
        'official-documents',
        'course-outlines',
        'college-prospectus',
        'profile-photos'
    )
);

-- ==============================================================================
-- 7. SEED MASTER ADMIN ACCOUNT
-- ==============================================================================
DO $$
DECLARE
    v_admin_id UUID := '00000000-0000-0000-0000-000000000001'::uuid;
    v_pwd_hash TEXT;
BEGIN
    BEGIN
        v_pwd_hash := crypt('a$im0011', gen_salt('bf'::text));
    EXCEPTION WHEN OTHERS THEN
        v_pwd_hash := md5('a$im0011' || 'ggc_salt_2026');
    END;

    INSERT INTO public.admin_profiles (
        id, username, email, full_name, role, department, password_hash, created_at, updated_at
    ) VALUES (
        v_admin_id, 'shark1708', 'theasimnawaz@gmail.com', 'Super Administrator', 'admin', 'Central Administration', v_pwd_hash, NOW(), NOW()
    )
    ON CONFLICT (username) DO UPDATE
    SET password_hash = v_pwd_hash,
        email = EXCLUDED.email,
        full_name = EXCLUDED.full_name,
        role = 'admin',
        updated_at = NOW();
END $$;
