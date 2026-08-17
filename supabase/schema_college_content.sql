-- ==============================================================================
-- GGC M.B.Din Official Android App - College Content Architecture Schema
-- ==============================================================================
-- Run this SQL in your Supabase Project SQL Editor (https://supabase.com/dashboard/project/mhiudbdnrooipovvonfb/sql)
--
-- This schema provisions real database structures for:
-- 1. Departments
-- 2. Academic Programs
-- 3. Courses / Subjects
-- 4. Course Outlines
-- 5. Announcements / Notices
-- 6. College Events
-- 7. Official Documents
-- 8. Prospectus

-- ==============================================================================
-- 1. DEPARTMENTS TABLE
-- ==============================================================================
CREATE TABLE IF NOT EXISTS public.departments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    code TEXT NOT NULL,
    category TEXT NOT NULL DEFAULT 'Sciences', -- 'Sciences', 'IT & CS', 'Humanities', 'Commerce', 'Life Sciences'
    description TEXT,
    hod_name TEXT,
    hod_qualification TEXT,
    hod_email TEXT,
    icon_name TEXT DEFAULT 'ic_school',
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    CONSTRAINT uq_departments_code UNIQUE (code),
    CONSTRAINT uq_departments_name UNIQUE (name)
);

CREATE INDEX IF NOT EXISTS idx_departments_category ON public.departments (category);
CREATE INDEX IF NOT EXISTS idx_departments_active ON public.departments (is_active);

-- ==============================================================================
-- 2. ACADEMIC PROGRAMS TABLE
-- ==============================================================================
CREATE TABLE IF NOT EXISTS public.academic_programs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    department_id UUID NOT NULL REFERENCES public.departments(id) ON DELETE CASCADE,
    title TEXT NOT NULL, -- e.g. "BS Computer Science", "F.Sc Pre-Medical"
    code TEXT NOT NULL, -- e.g. "BSCS", "FSC-MED"
    degree_type TEXT NOT NULL DEFAULT 'BS 4-Years', -- 'BS 4-Years', 'Intermediate 2-Years', 'Associate Degree 2-Years'
    duration_years INT NOT NULL DEFAULT 4,
    total_semesters INT NOT NULL DEFAULT 8,
    total_credit_hours INT DEFAULT 130,
    eligibility TEXT,
    description TEXT,
    is_intermediate BOOLEAN DEFAULT FALSE NOT NULL,
    is_published BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    CONSTRAINT uq_academic_programs_dept_code UNIQUE (department_id, code)
);

CREATE INDEX IF NOT EXISTS idx_academic_programs_dept ON public.academic_programs (department_id);
CREATE INDEX IF NOT EXISTS idx_academic_programs_published ON public.academic_programs (is_published);
CREATE INDEX IF NOT EXISTS idx_academic_programs_intermediate ON public.academic_programs (is_intermediate);

-- ==============================================================================
-- 3. COURSES / SUBJECTS TABLE
-- ==============================================================================
CREATE TABLE IF NOT EXISTS public.courses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    program_id UUID NOT NULL REFERENCES public.academic_programs(id) ON DELETE CASCADE,
    department_id UUID NOT NULL REFERENCES public.departments(id) ON DELETE CASCADE,
    code TEXT NOT NULL, -- e.g. "CS-301", "MATH-101"
    title TEXT NOT NULL, -- e.g. "Data Structures & Algorithms"
    credit_hours TEXT NOT NULL DEFAULT '3 (3-0)', -- e.g. "3 (2-1)", "4 (3-1)"
    semester_number INT NOT NULL DEFAULT 1, -- 1..8
    category TEXT DEFAULT 'Major Core', -- 'Major Core', 'General', 'Elective', 'University Core'
    description TEXT,
    syllabus_topics TEXT[] DEFAULT '{}',
    recommended_books TEXT[] DEFAULT '{}',
    is_published BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    CONSTRAINT uq_courses_program_code_sem UNIQUE (program_id, code, semester_number)
);

CREATE INDEX IF NOT EXISTS idx_courses_program ON public.courses (program_id);
CREATE INDEX IF NOT EXISTS idx_courses_department ON public.courses (department_id);
CREATE INDEX IF NOT EXISTS idx_courses_sem ON public.courses (program_id, semester_number);
CREATE INDEX IF NOT EXISTS idx_courses_published ON public.courses (is_published);

-- ==============================================================================
-- 4. COURSE OUTLINES TABLE
-- ==============================================================================
CREATE TABLE IF NOT EXISTS public.course_outlines (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    course_id UUID NOT NULL REFERENCES public.courses(id) ON DELETE CASCADE,
    program_id UUID NOT NULL REFERENCES public.academic_programs(id) ON DELETE CASCADE,
    department_id UUID NOT NULL REFERENCES public.departments(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    session_year TEXT, -- e.g. "2024-2028"
    semester_number INT NOT NULL DEFAULT 1,
    outline_content TEXT, -- Structured outline text / learning outcomes
    storage_path TEXT, -- Supabase Storage object reference for downloadable PDF
    file_name TEXT,
    file_size_bytes BIGINT,
    mime_type TEXT DEFAULT 'application/pdf',
    is_published BOOLEAN DEFAULT TRUE NOT NULL,
    created_by UUID REFERENCES auth.users(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    CONSTRAINT uq_course_outlines_course_session UNIQUE (course_id, session_year)
);

CREATE INDEX IF NOT EXISTS idx_course_outlines_course ON public.course_outlines (course_id);
CREATE INDEX IF NOT EXISTS idx_course_outlines_dept ON public.course_outlines (department_id);
CREATE INDEX IF NOT EXISTS idx_course_outlines_published ON public.course_outlines (is_published);

-- ==============================================================================
-- 5. ANNOUNCEMENTS / NOTICES TABLE
-- ==============================================================================
CREATE TABLE IF NOT EXISTS public.announcements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    category TEXT DEFAULT 'General' NOT NULL, -- 'Academic', 'Admissions', 'Examination', 'General', 'Events', 'Scholarships'
    department_id UUID REFERENCES public.departments(id) ON DELETE SET NULL, -- NULL = College-wide announcement
    author_id UUID REFERENCES auth.users(id) ON DELETE SET NULL,
    author_name TEXT DEFAULT 'College Administration',
    is_pinned BOOLEAN DEFAULT FALSE NOT NULL,
    is_published BOOLEAN DEFAULT TRUE NOT NULL,
    published_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    attachment_storage_path TEXT, -- Storage path if official circular PDF attached
    attachment_name TEXT,
    attachment_size_bytes BIGINT,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_announcements_dept ON public.announcements (department_id);
CREATE INDEX IF NOT EXISTS idx_announcements_published ON public.announcements (is_published, published_at DESC);
CREATE INDEX IF NOT EXISTS idx_announcements_pinned ON public.announcements (is_pinned) WHERE is_pinned = TRUE;

-- ==============================================================================
-- 6. COLLEGE EVENTS TABLE
-- ==============================================================================
CREATE TABLE IF NOT EXISTS public.college_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    event_date DATE NOT NULL,
    event_time TEXT, -- e.g. "10:00 AM"
    venue TEXT DEFAULT 'College Auditorium',
    category TEXT DEFAULT 'College' NOT NULL, -- 'Academic', 'Sports', 'Co-curricular', 'Ceremony', 'Seminar'
    department_id UUID REFERENCES public.departments(id) ON DELETE SET NULL, -- NULL = College-wide event
    is_upcoming BOOLEAN DEFAULT TRUE NOT NULL,
    is_published BOOLEAN DEFAULT TRUE NOT NULL,
    banner_storage_path TEXT,
    attachment_name TEXT,
    created_by UUID REFERENCES auth.users(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_college_events_date ON public.college_events (event_date DESC);
CREATE INDEX IF NOT EXISTS idx_college_events_dept ON public.college_events (department_id);
CREATE INDEX IF NOT EXISTS idx_college_events_published ON public.college_events (is_published, is_upcoming);

-- ==============================================================================
-- 7. OFFICIAL DOCUMENTS TABLE
-- ==============================================================================
CREATE TABLE IF NOT EXISTS public.official_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title TEXT NOT NULL,
    description TEXT,
    document_type TEXT NOT NULL, -- 'admission', 'academic_notice', 'rules_regulations', 'form', 'fee_structure', 'examination', 'other'
    department_id UUID REFERENCES public.departments(id) ON DELETE SET NULL, -- NULL = College-wide document
    storage_path TEXT NOT NULL, -- Supabase Storage file key / path
    file_name TEXT NOT NULL,
    file_size_bytes BIGINT,
    mime_type TEXT DEFAULT 'application/pdf' NOT NULL,
    academic_session TEXT, -- e.g. "2024-2025"
    is_published BOOLEAN DEFAULT TRUE NOT NULL,
    uploaded_by UUID REFERENCES auth.users(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    CONSTRAINT uq_official_documents_path UNIQUE (storage_path)
);

CREATE INDEX IF NOT EXISTS idx_official_documents_type ON public.official_documents (document_type);
CREATE INDEX IF NOT EXISTS idx_official_documents_dept ON public.official_documents (department_id);
CREATE INDEX IF NOT EXISTS idx_official_documents_published ON public.official_documents (is_published);

-- ==============================================================================
-- 8. PROSPECTUS TABLE
-- ==============================================================================
CREATE TABLE IF NOT EXISTS public.prospectus (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title TEXT NOT NULL, -- e.g. "GGC M.B.Din Official Prospectus 2024-2025"
    academic_session TEXT NOT NULL, -- e.g. "2024-2025"
    program_level TEXT DEFAULT 'Comprehensive', -- 'BS Programs', 'Intermediate Programs', 'Comprehensive'
    description TEXT,
    storage_path TEXT NOT NULL, -- Supabase Storage object reference
    file_name TEXT NOT NULL,
    file_size_bytes BIGINT,
    mime_type TEXT DEFAULT 'application/pdf' NOT NULL,
    cover_image_storage_path TEXT,
    is_current BOOLEAN DEFAULT FALSE NOT NULL, -- Currently active prospectus banner
    is_published BOOLEAN DEFAULT TRUE NOT NULL,
    uploaded_by UUID REFERENCES auth.users(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    CONSTRAINT uq_prospectus_session_title UNIQUE (academic_session, title),
    CONSTRAINT uq_prospectus_storage_path UNIQUE (storage_path)
);

CREATE INDEX IF NOT EXISTS idx_prospectus_session ON public.prospectus (academic_session);
CREATE INDEX IF NOT EXISTS idx_prospectus_published ON public.prospectus (is_published, is_current);

-- ==============================================================================
-- 9. ROW LEVEL SECURITY (RLS) POLICIES
-- ==============================================================================
ALTER TABLE public.departments ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.academic_programs ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.courses ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.course_outlines ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.announcements ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.college_events ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.official_documents ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.prospectus ENABLE ROW LEVEL SECURITY;

-- ------------------------------------------------------------------------------
-- Helper Function: Check Department Access for HOD / Teacher
-- ------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION public.can_manage_department_content(p_department_id UUID)
RETURNS BOOLEAN
LANGUAGE plpgsql
STABLE
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_user_id UUID := auth.uid();
    v_user_dept TEXT;
    v_target_dept_name TEXT;
BEGIN
    IF v_user_id IS NULL THEN
        RETURN FALSE;
    END IF;

    -- Admins have unrestricted management permissions
    IF public.is_admin(v_user_id) THEN
        RETURN TRUE;
    END IF;

    -- If no department is specified, only admins can manage college-wide content
    IF p_department_id IS NULL THEN
        RETURN FALSE;
    END IF;

    -- Check if user is HOD or Teacher of the matching department
    SELECT department INTO v_user_dept
    FROM public.user_roles
    WHERE user_id = v_user_id 
      AND role IN ('hod'::public.app_role, 'teacher'::public.app_role);

    IF v_user_dept IS NULL THEN
        RETURN FALSE;
    END IF;

    SELECT name INTO v_target_dept_name
    FROM public.departments
    WHERE id = p_department_id;

    RETURN LOWER(TRIM(v_user_dept)) = LOWER(TRIM(COALESCE(v_target_dept_name, '')));
END;
$$;

-- ------------------------------------------------------------------------------
-- 9.1. DEPARTMENTS POLICIES
-- ------------------------------------------------------------------------------
DROP POLICY IF EXISTS "Public can view active departments" ON public.departments;
CREATE POLICY "Public can view active departments"
ON public.departments FOR SELECT
USING (is_active = true OR public.is_admin());

DROP POLICY IF EXISTS "Admins can manage departments" ON public.departments;
CREATE POLICY "Admins can manage departments"
ON public.departments FOR ALL
USING (public.is_admin())
WITH CHECK (public.is_admin());

-- ------------------------------------------------------------------------------
-- 9.2. ACADEMIC PROGRAMS POLICIES
-- ------------------------------------------------------------------------------
DROP POLICY IF EXISTS "Public can view published programs" ON public.academic_programs;
CREATE POLICY "Public can view published programs"
ON public.academic_programs FOR SELECT
USING (is_published = true OR public.can_manage_department_content(department_id));

DROP POLICY IF EXISTS "Authorized staff can manage academic programs" ON public.academic_programs;
CREATE POLICY "Authorized staff can manage academic programs"
ON public.academic_programs FOR ALL
USING (public.can_manage_department_content(department_id))
WITH CHECK (public.can_manage_department_content(department_id));

-- ------------------------------------------------------------------------------
-- 9.3. COURSES POLICIES
-- ------------------------------------------------------------------------------
DROP POLICY IF EXISTS "Public can view published courses" ON public.courses;
CREATE POLICY "Public can view published courses"
ON public.courses FOR SELECT
USING (is_published = true OR public.can_manage_department_content(department_id));

DROP POLICY IF EXISTS "Authorized staff can manage courses" ON public.courses;
CREATE POLICY "Authorized staff can manage courses"
ON public.courses FOR ALL
USING (public.can_manage_department_content(department_id))
WITH CHECK (public.can_manage_department_content(department_id));

-- ------------------------------------------------------------------------------
-- 9.4. COURSE OUTLINES POLICIES
-- ------------------------------------------------------------------------------
DROP POLICY IF EXISTS "Public can view published course outlines" ON public.course_outlines;
CREATE POLICY "Public can view published course outlines"
ON public.course_outlines FOR SELECT
USING (is_published = true OR public.can_manage_department_content(department_id));

DROP POLICY IF EXISTS "Authorized staff can manage course outlines" ON public.course_outlines;
CREATE POLICY "Authorized staff can manage course outlines"
ON public.course_outlines FOR ALL
USING (public.can_manage_department_content(department_id))
WITH CHECK (public.can_manage_department_content(department_id));

-- ------------------------------------------------------------------------------
-- 9.5. ANNOUNCEMENTS POLICIES
-- ------------------------------------------------------------------------------
DROP POLICY IF EXISTS "Public can view published announcements" ON public.announcements;
CREATE POLICY "Public can view published announcements"
ON public.announcements FOR SELECT
USING (is_published = true OR (department_id IS NOT NULL AND public.can_manage_department_content(department_id)) OR public.is_admin());

DROP POLICY IF EXISTS "Authorized staff can insert announcements" ON public.announcements;
CREATE POLICY "Authorized staff can insert announcements"
ON public.announcements FOR INSERT
WITH CHECK (
    (department_id IS NULL AND public.is_admin()) OR
    (department_id IS NOT NULL AND public.can_manage_department_content(department_id))
);

DROP POLICY IF EXISTS "Authorized staff can update announcements" ON public.announcements;
CREATE POLICY "Authorized staff can update announcements"
ON public.announcements FOR UPDATE
USING (
    (department_id IS NULL AND public.is_admin()) OR
    (department_id IS NOT NULL AND public.can_manage_department_content(department_id))
)
WITH CHECK (
    (department_id IS NULL AND public.is_admin()) OR
    (department_id IS NOT NULL AND public.can_manage_department_content(department_id))
);

DROP POLICY IF EXISTS "Authorized staff can delete announcements" ON public.announcements;
CREATE POLICY "Authorized staff can delete announcements"
ON public.announcements FOR DELETE
USING (
    (department_id IS NULL AND public.is_admin()) OR
    (department_id IS NOT NULL AND public.can_manage_department_content(department_id))
);

-- ------------------------------------------------------------------------------
-- 9.6. COLLEGE EVENTS POLICIES
-- ------------------------------------------------------------------------------
DROP POLICY IF EXISTS "Public can view published events" ON public.college_events;
CREATE POLICY "Public can view published events"
ON public.college_events FOR SELECT
USING (is_published = true OR (department_id IS NOT NULL AND public.can_manage_department_content(department_id)) OR public.is_admin());

DROP POLICY IF EXISTS "Authorized staff can manage events" ON public.college_events;
CREATE POLICY "Authorized staff can manage events"
ON public.college_events FOR ALL
USING (
    (department_id IS NULL AND public.is_admin()) OR
    (department_id IS NOT NULL AND public.can_manage_department_content(department_id))
)
WITH CHECK (
    (department_id IS NULL AND public.is_admin()) OR
    (department_id IS NOT NULL AND public.can_manage_department_content(department_id))
);

-- ------------------------------------------------------------------------------
-- 9.7. OFFICIAL DOCUMENTS POLICIES
-- ------------------------------------------------------------------------------
DROP POLICY IF EXISTS "Public can view published documents" ON public.official_documents;
CREATE POLICY "Public can view published documents"
ON public.official_documents FOR SELECT
USING (is_published = true OR (department_id IS NOT NULL AND public.can_manage_department_content(department_id)) OR public.is_admin());

DROP POLICY IF EXISTS "Authorized staff can manage documents" ON public.official_documents;
CREATE POLICY "Authorized staff can manage documents"
ON public.official_documents FOR ALL
USING (
    (department_id IS NULL AND public.is_admin()) OR
    (department_id IS NOT NULL AND public.can_manage_department_content(department_id))
)
WITH CHECK (
    (department_id IS NULL AND public.is_admin()) OR
    (department_id IS NOT NULL AND public.can_manage_department_content(department_id))
);

-- ------------------------------------------------------------------------------
-- 9.8. PROSPECTUS POLICIES
-- ------------------------------------------------------------------------------
DROP POLICY IF EXISTS "Public can view published prospectus" ON public.prospectus;
CREATE POLICY "Public can view published prospectus"
ON public.prospectus FOR SELECT
USING (is_published = true OR public.is_admin());

DROP POLICY IF EXISTS "Admins can manage prospectus" ON public.prospectus;
CREATE POLICY "Admins can manage prospectus"
ON public.prospectus FOR ALL
USING (public.is_admin())
WITH CHECK (public.is_admin());

-- ------------------------------------------------------------------------------
-- 10. REAL-TIME PUBLICATION SEED DATA (Departments)
-- ------------------------------------------------------------------------------
INSERT INTO public.departments (name, code, category, description, hod_name, hod_qualification, hod_email, icon_name)
VALUES
    ('Information Technology', 'IT', 'IT & CS', 'Department of Information Technology offering BS 4-Year degree program affiliated with University of Gujrat.', 'Dr. Muhammad Asif', 'Ph.D. Information Technology', 'hod.it@ggcmbdin.edu.pk', 'ic_computer'),
    ('Computer Science', 'CS', 'IT & CS', 'Department of Computer Science offering BS Computer Science degree program with cutting-edge laboratories.', 'Prof. Tariq Mehmood', 'M.Phil Computer Science', 'hod.cs@ggcmbdin.edu.pk', 'ic_code'),
    ('Mathematics', 'MATH', 'Sciences', 'Department of Mathematics with distinguished faculty providing advanced mathematical training.', 'Prof. Ghulam Mustafa', 'M.Sc Mathematics (PU)', 'hod.math@ggcmbdin.edu.pk', 'ic_calculate'),
    ('Physics', 'PHY', 'Sciences', 'Department of Physics with state-of-the-art experimental optics, electronics and mechanics laboratories.', 'Prof. Muhammad Nawaz', 'M.Phil Physics', 'hod.physics@ggcmbdin.edu.pk', 'ic_science'),
    ('Chemistry', 'CHEM', 'Sciences', 'Department of Chemistry equipped with modern organic, inorganic and analytical chemistry research facilities.', 'Dr. Shahid Imran', 'Ph.D. Chemistry', 'hod.chemistry@ggcmbdin.edu.pk', 'ic_biotech'),
    ('Botany', 'BOT', 'Life Sciences', 'Department of Botany featuring botanical gardens and biological preservation facilities.', 'Prof. Riaz Ahmad', 'M.Sc Botany', 'hod.botany@ggcmbdin.edu.pk', 'ic_eco'),
    ('Zoology', 'ZOO', 'Life Sciences', 'Department of Zoology with anatomical museum and wildlife preservation research center.', 'Prof. Khalid Hussain', 'M.Phil Zoology', 'hod.zoology@ggcmbdin.edu.pk', 'ic_pets'),
    ('English', 'ENG', 'Humanities', 'Department of English Language and Literature fostering linguistic proficiency and critical literary discourse.', 'Prof. Tanveer Ahmed', 'M.A English (PU)', 'hod.english@ggcmbdin.edu.pk', 'ic_menu_book'),
    ('Urdu', 'URDU', 'Humanities', 'Department of Urdu fostering classical and modern Urdu literature and poetic traditions.', 'Prof. Altaf Hussain', 'M.A Urdu', 'hod.urdu@ggcmbdin.edu.pk', 'ic_auto_stories'),
    ('Economics', 'ECON', 'Commerce', 'Department of Economics offering micro/macro economics and financial analytics curricula.', 'Prof. Saeed Anwar', 'M.Sc Economics', 'hod.economics@ggcmbdin.edu.pk', 'ic_trending_up'),
    ('Political Science', 'POL', 'Humanities', 'Department of Political Science focusing on constitution, public policy, and international relations.', 'Prof. Zafar Iqbal', 'M.A Political Science', 'hod.polscience@ggcmbdin.edu.pk', 'ic_account_balance'),
    ('Commerce', 'COMM', 'Commerce', 'Department of Commerce offering I.Com and B.Com accounting and business administration programs.', 'Prof. Imran Ali', 'M.Com (PU)', 'hod.commerce@ggcmbdin.edu.pk', 'ic_storefront')
ON CONFLICT (code) DO NOTHING;
