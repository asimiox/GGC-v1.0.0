-- ==============================================================================
-- GGC M.B.DIN: DATABASE-DRIVEN PASSWORD & SINGLE-DEVICE SESSION MANAGEMENT
-- PostgreSQL / Supabase Schema, Indices, and Security Definer RPCs
-- ==============================================================================

-- 1. EXTENSIONS
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- 2. USER SESSIONS TABLE (WHATSAPP-STYLE SINGLE-DEVICE LOCKING)
CREATE TABLE IF NOT EXISTS public.user_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id TEXT NOT NULL,
    user_identifier TEXT NOT NULL,
    role TEXT NOT NULL,
    device_id TEXT NOT NULL,
    device_name TEXT,
    session_token_hash TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    last_seen_at TIMESTAMPTZ DEFAULT timezone('utc'::text, now()) NOT NULL,
    expires_at TIMESTAMPTZ,
    revoked_at TIMESTAMPTZ,
    active BOOLEAN DEFAULT TRUE NOT NULL
);

-- UNIQUE CONSTRAINT: Exactly ONE active session per user_identifier at the database level!
CREATE UNIQUE INDEX IF NOT EXISTS uq_user_sessions_active_user 
ON public.user_sessions (user_identifier) 
WHERE (active = TRUE);

CREATE INDEX IF NOT EXISTS idx_user_sessions_device ON public.user_sessions(device_id);
CREATE INDEX IF NOT EXISTS idx_user_sessions_active ON public.user_sessions(user_identifier, active);

ALTER TABLE public.user_sessions ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Public read-write for user_sessions" ON public.user_sessions;
CREATE POLICY "Public read-write for user_sessions" 
ON public.user_sessions 
FOR ALL 
USING (true) 
WITH CHECK (true);

GRANT ALL ON TABLE public.user_sessions TO anon, authenticated, service_role;

-- 3. ENSURE PASSWORD, ACCOUNT STATUS & FORCE PASSWORD CHANGE COLUMNS
ALTER TABLE public.bs_student_profiles 
    ADD COLUMN IF NOT EXISTS password_hash TEXT,
    ADD COLUMN IF NOT EXISTS force_password_change BOOLEAN DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS password_changed_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS account_status TEXT DEFAULT 'active';

ALTER TABLE public.intermediate_student_profiles 
    ADD COLUMN IF NOT EXISTS password_hash TEXT,
    ADD COLUMN IF NOT EXISTS force_password_change BOOLEAN DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS password_changed_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS account_status TEXT DEFAULT 'active';

ALTER TABLE public.faculty_profiles 
    ADD COLUMN IF NOT EXISTS password_hash TEXT,
    ADD COLUMN IF NOT EXISTS force_password_change BOOLEAN DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS password_changed_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS account_status TEXT DEFAULT 'active';

ALTER TABLE public.admin_profiles 
    ADD COLUMN IF NOT EXISTS password_hash TEXT,
    ADD COLUMN IF NOT EXISTS force_password_change BOOLEAN DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS password_changed_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS account_status TEXT DEFAULT 'active';

-- 4. RPC: DIRECT CHANGE PASSWORD
CREATE OR REPLACE FUNCTION public.direct_change_password(
    p_role TEXT,
    p_identifier TEXT,
    p_current_password TEXT,
    p_new_password TEXT,
    p_device_id TEXT DEFAULT NULL
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, extensions
AS $$
DECLARE
    v_clean_role TEXT := LOWER(TRIM(COALESCE(p_role, '')));
    v_clean_id TEXT := TRIM(COALESCE(p_identifier, ''));
    v_clean_curr TEXT := TRIM(COALESCE(p_current_password, ''));
    v_clean_new TEXT := TRIM(COALESCE(p_new_password, ''));
    v_stored_hash TEXT;
    v_profile_id UUID;
    v_is_valid BOOLEAN := FALSE;
    v_new_hash TEXT;
BEGIN
    IF v_clean_id = '' OR v_clean_curr = '' OR v_clean_new = '' THEN
        RETURN jsonb_build_object('success', false, 'error', 'Identifier, current password, and new password are required.');
    END IF;

    IF LENGTH(v_clean_new) < 4 THEN
        RETURN jsonb_build_object('success', false, 'error', 'New password must be at least 4 characters long.');
    END IF;

    IF v_clean_new = '00000' THEN
        RETURN jsonb_build_object('success', false, 'error', 'You cannot reuse the default password 00000. Please choose a personalized password.');
    END IF;

    -- Look up profile by role and identifier
    IF v_clean_role = 'student_bs' OR v_clean_role = 'bs' THEN
        SELECT id, password_hash INTO v_profile_id, v_stored_hash
        FROM public.bs_student_profiles
        WHERE LOWER(TRIM(username)) = LOWER(v_clean_id)
           OR UPPER(TRIM(roll_number)) = UPPER(v_clean_id)
           OR UPPER(TRIM(registration_number)) = UPPER(v_clean_id)
        LIMIT 1;
    ELSIF v_clean_role = 'student_intermediate' OR v_clean_role = 'intermediate' OR v_clean_role = 'inter' THEN
        SELECT id, password_hash INTO v_profile_id, v_stored_hash
        FROM public.intermediate_student_profiles
        WHERE LOWER(TRIM(username)) = LOWER(v_clean_id)
           OR UPPER(TRIM(roll_number)) = UPPER(v_clean_id)
           OR UPPER(TRIM(registration_number)) = UPPER(v_clean_id)
        LIMIT 1;
    ELSIF v_clean_role = 'teacher' OR v_clean_role = 'faculty' OR v_clean_role = 'hod' THEN
        SELECT id, password_hash INTO v_profile_id, v_stored_hash
        FROM public.faculty_profiles
        WHERE LOWER(TRIM(username)) = LOWER(v_clean_id)
           OR UPPER(TRIM(faculty_id)) = UPPER(v_clean_id)
           OR LOWER(TRIM(institutional_email)) = LOWER(v_clean_id)
        LIMIT 1;
    ELSIF v_clean_role = 'admin' OR v_clean_role = 'super_admin' THEN
        SELECT id, password_hash INTO v_profile_id, v_stored_hash
        FROM public.admin_profiles
        WHERE LOWER(TRIM(username)) = LOWER(v_clean_id)
           OR LOWER(TRIM(email)) = LOWER(v_clean_id)
        LIMIT 1;
    ELSE
        -- Fallback search across all tables if role is unknown
        SELECT id, password_hash INTO v_profile_id, v_stored_hash
        FROM public.bs_student_profiles
        WHERE LOWER(TRIM(username)) = LOWER(v_clean_id)
           OR UPPER(TRIM(roll_number)) = UPPER(v_clean_id)
        LIMIT 1;

        IF v_profile_id IS NOT NULL THEN
            v_clean_role := 'student_bs';
        ELSE
            SELECT id, password_hash INTO v_profile_id, v_stored_hash
            FROM public.faculty_profiles
            WHERE LOWER(TRIM(username)) = LOWER(v_clean_id)
               OR UPPER(TRIM(faculty_id)) = UPPER(v_clean_id)
            LIMIT 1;
            IF v_profile_id IS NOT NULL THEN
                v_clean_role := 'faculty';
            ELSE
                SELECT id, password_hash INTO v_profile_id, v_stored_hash
                FROM public.admin_profiles
                WHERE LOWER(TRIM(username)) = LOWER(v_clean_id)
                LIMIT 1;
                IF v_profile_id IS NOT NULL THEN
                    v_clean_role := 'admin';
                END IF;
            END IF;
        END IF;
    END IF;

    IF v_profile_id IS NULL THEN
        RETURN jsonb_build_object('success', false, 'error', 'Account not found.');
    END IF;

    -- Verify current password against stored database password_hash
    IF v_stored_hash IS NOT NULL THEN
        BEGIN
            IF v_stored_hash = crypt(v_clean_curr, v_stored_hash) THEN
                v_is_valid := TRUE;
            ELSIF v_stored_hash = md5(v_clean_curr || 'ggc_salt_2026') THEN
                v_is_valid := TRUE;
            ELSIF v_stored_hash = v_clean_curr THEN
                v_is_valid := TRUE;
            END IF;
        EXCEPTION WHEN OTHERS THEN
            IF v_stored_hash = md5(v_clean_curr || 'ggc_salt_2026') OR v_stored_hash = v_clean_curr THEN
                v_is_valid := TRUE;
            END IF;
        END;
    ELSE
        -- If no password_hash existed previously, check initial default '00000'
        IF v_clean_curr = '00000' THEN
            v_is_valid := TRUE;
        END IF;
    END IF;

    IF NOT v_is_valid THEN
        RETURN jsonb_build_object('success', false, 'error', 'Current password is incorrect.');
    END IF;

    -- Generate new bcrypt hash
    BEGIN
        v_new_hash := crypt(v_clean_new, gen_salt('bf'::text));
    EXCEPTION WHEN OTHERS THEN
        v_new_hash := md5(v_clean_new || 'ggc_salt_2026');
    END;

    -- Update database password hash and flags
    IF v_clean_role = 'student_bs' OR v_clean_role = 'bs' THEN
        UPDATE public.bs_student_profiles
        SET password_hash = v_new_hash,
            force_password_change = FALSE,
            password_changed_at = NOW(),
            updated_at = NOW()
        WHERE id = v_profile_id;
    ELSIF v_clean_role = 'student_intermediate' OR v_clean_role = 'intermediate' OR v_clean_role = 'inter' THEN
        UPDATE public.intermediate_student_profiles
        SET password_hash = v_new_hash,
            force_password_change = FALSE,
            password_changed_at = NOW(),
            updated_at = NOW()
        WHERE id = v_profile_id;
    ELSIF v_clean_role = 'teacher' OR v_clean_role = 'faculty' OR v_clean_role = 'hod' THEN
        UPDATE public.faculty_profiles
        SET password_hash = v_new_hash,
            force_password_change = FALSE,
            password_changed_at = NOW(),
            updated_at = NOW()
        WHERE id = v_profile_id;
    ELSIF v_clean_role = 'admin' OR v_clean_role = 'super_admin' THEN
        UPDATE public.admin_profiles
        SET password_hash = v_new_hash,
            force_password_change = FALSE,
            password_changed_at = NOW(),
            updated_at = NOW()
        WHERE id = v_profile_id;
    END IF;

    -- Touch session if device provided
    IF p_device_id IS NOT NULL AND p_device_id <> '' THEN
        UPDATE public.user_sessions
        SET last_seen_at = NOW()
        WHERE user_identifier = UPPER(v_clean_id)
          AND device_id = p_device_id
          AND active = TRUE;
    END IF;

    RETURN jsonb_build_object(
        'success', true,
        'message', 'Password changed successfully! Please use your new password for future logins.'
    );
END;
$$;

-- 5. RPC: DIRECT LOGOUT SESSION (RELEASES DATABASE ACTIVE SESSION LOCK)
CREATE OR REPLACE FUNCTION public.direct_logout_session(
    p_identifier TEXT,
    p_device_id TEXT
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, extensions
AS $$
DECLARE
    v_clean_id TEXT := UPPER(TRIM(COALESCE(p_identifier, '')));
    v_clean_dev TEXT := TRIM(COALESCE(p_device_id, ''));
BEGIN
    UPDATE public.user_sessions
    SET active = FALSE,
        revoked_at = NOW(),
        last_seen_at = NOW()
    WHERE user_identifier = v_clean_id
      AND device_id = v_clean_dev
      AND active = TRUE;

    RETURN jsonb_build_object('success', true, 'message', 'Session terminated successfully.');
END;
$$;

-- 6. RPC: DIRECT VERIFY SESSION
CREATE OR REPLACE FUNCTION public.direct_verify_session(
    p_identifier TEXT,
    p_device_id TEXT,
    p_session_token_hash TEXT DEFAULT NULL
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, extensions
AS $$
DECLARE
    v_clean_id TEXT := UPPER(TRIM(COALESCE(p_identifier, '')));
    v_clean_dev TEXT := TRIM(COALESCE(p_device_id, ''));
    v_session RECORD;
BEGIN
    SELECT * INTO v_session
    FROM public.user_sessions
    WHERE user_identifier = v_clean_id
      AND active = TRUE
    ORDER BY created_at DESC
    LIMIT 1;

    IF NOT FOUND THEN
        RETURN jsonb_build_object('valid', false, 'error', 'No active session found.');
    END IF;

    IF v_session.device_id <> v_clean_dev THEN
        RETURN jsonb_build_object(
            'valid', false,
            'blocked_by_other_device', true,
            'active_device_name', COALESCE(v_session.device_name, 'Another Device'),
            'error', 'Account is active on ' || COALESCE(v_session.device_name, 'Another Device') || '.'
        );
    END IF;

    UPDATE public.user_sessions
    SET last_seen_at = NOW()
    WHERE id = v_session.id;

    RETURN jsonb_build_object('valid', true, 'session_id', v_session.id);
END;
$$;

-- 7. RPC: ADMIN FORCE TERMINATE USER SESSION
CREATE OR REPLACE FUNCTION public.admin_force_terminate_user_session(
    p_identifier TEXT
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, extensions
AS $$
DECLARE
    v_clean_id TEXT := UPPER(TRIM(COALESCE(p_identifier, '')));
    v_count INT;
BEGIN
    UPDATE public.user_sessions
    SET active = FALSE,
        revoked_at = NOW(),
        last_seen_at = NOW()
    WHERE user_identifier = v_clean_id
      AND active = TRUE;

    GET DIAGNOSTICS v_count = ROW_COUNT;

    RETURN jsonb_build_object('success', true, 'terminated_sessions', v_count);
END;
$$;

-- 8. RPC: ATOMIC LOGIN USER (VERIFIES PASSWORD & ENFORCES SINGLE-DEVICE CONCURRENCY)
CREATE OR REPLACE FUNCTION public.atomic_login_user(
    p_role TEXT,
    p_identifier TEXT,
    p_password TEXT,
    p_device_id TEXT,
    p_device_name TEXT DEFAULT NULL,
    p_session_token_hash TEXT DEFAULT NULL
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, extensions
AS $$
DECLARE
    v_clean_role TEXT := LOWER(TRIM(COALESCE(p_role, '')));
    v_clean_id TEXT := TRIM(COALESCE(p_identifier, ''));
    v_clean_dev TEXT := TRIM(COALESCE(p_device_id, ''));
    v_clean_dev_name TEXT := TRIM(COALESCE(p_device_name, 'Android Device'));
    v_token_hash TEXT := COALESCE(p_session_token_hash, 'SESSION_TOKEN_HASH');
    v_session_user_key TEXT := UPPER(v_clean_id);
    
    v_profile RECORD;
    v_is_valid BOOLEAN := FALSE;
    v_profile_user_id TEXT;
    v_force_pwd_change BOOLEAN := FALSE;
    v_account_status TEXT := 'active';
    v_existing_session RECORD;
BEGIN
    IF v_clean_id = '' OR COALESCE(p_password, '') = '' THEN
        RETURN jsonb_build_object('success', false, 'error', 'Identifier and password are required.');
    END IF;

    IF v_clean_dev = '' THEN
        RETURN jsonb_build_object('success', false, 'error', 'Device identifier is required.');
    END IF;

    -- 1. Look up profile and verify password
    IF v_clean_role = 'student_bs' OR v_clean_role = 'bs' THEN
        SELECT *, COALESCE(program, program_name) AS user_program, COALESCE(first_name, student_name) AS user_first_name 
        INTO v_profile FROM public.bs_student_profiles
        WHERE LOWER(TRIM(username)) = LOWER(v_clean_id)
           OR UPPER(TRIM(roll_number)) = UPPER(v_clean_id)
           OR UPPER(TRIM(registration_number)) = UPPER(v_clean_id);

        IF NOT FOUND THEN
            -- Check official registry
            DECLARE
                v_official RECORD;
                v_new_id UUID := gen_random_uuid();
                v_pwd_hash TEXT;
            BEGIN
                SELECT * INTO v_official FROM public.official_bs_students
                WHERE UPPER(TRIM(roll_number)) = UPPER(v_clean_id)
                   OR UPPER(TRIM(registration_number)) = UPPER(v_clean_id);

                IF FOUND THEN
                    BEGIN
                        v_pwd_hash := crypt(p_password, gen_salt('bf'::text));
                    EXCEPTION WHEN OTHERS THEN
                        v_pwd_hash := md5(p_password || 'ggc_salt_2026');
                    END;

                    INSERT INTO public.bs_student_profiles (
                        id, username, first_name, last_name, student_name,
                        roll_number, registration_number, program, program_name, semester,
                        password_hash, force_password_change, account_status, created_at
                    ) VALUES (
                        v_new_id, LOWER(v_official.roll_number), COALESCE(v_official.first_name, v_official.student_name),
                        COALESCE(v_official.last_name, ''), COALESCE(v_official.student_name, v_official.first_name),
                        v_official.roll_number, v_official.registration_number,
                        COALESCE(v_official.program, v_official.program_name),
                        COALESCE(v_official.program_name, v_official.program),
                        'Semester 1', v_pwd_hash, (p_password = '00000'), 'active', NOW()
                    ) ON CONFLICT (roll_number) DO UPDATE
                    SET password_hash = EXCLUDED.password_hash
                    RETURNING *, COALESCE(program, program_name) AS user_program, COALESCE(first_name, student_name) AS user_first_name INTO v_profile;
                ELSE
                    RETURN jsonb_build_object('success', false, 'error', 'Invalid BS student credentials.');
                END IF;
            END;
        END IF;

        v_profile_user_id := v_profile.id::text;
        v_session_user_key := UPPER(COALESCE(v_profile.roll_number, v_clean_id));
        v_force_pwd_change := COALESCE(v_profile.force_password_change, FALSE);
        v_account_status := COALESCE(v_profile.account_status, 'active');

    ELSIF v_clean_role = 'student_intermediate' OR v_clean_role = 'intermediate' OR v_clean_role = 'inter' THEN
        SELECT *, COALESCE(program, program_name) AS user_program, COALESCE(first_name, student_name) AS user_first_name 
        INTO v_profile FROM public.intermediate_student_profiles
        WHERE LOWER(TRIM(username)) = LOWER(v_clean_id)
           OR UPPER(TRIM(roll_number)) = UPPER(v_clean_id)
           OR UPPER(TRIM(registration_number)) = UPPER(v_clean_id);

        IF NOT FOUND THEN
            -- Check official intermediate registry
            DECLARE
                v_official_inter RECORD;
                v_new_inter_id UUID := gen_random_uuid();
                v_pwd_hash TEXT;
            BEGIN
                SELECT * INTO v_official_inter FROM public.official_intermediate_students
                WHERE UPPER(TRIM(roll_number)) = UPPER(v_clean_id)
                   OR UPPER(TRIM(registration_number)) = UPPER(v_clean_id);

                IF FOUND THEN
                    BEGIN
                        v_pwd_hash := crypt(p_password, gen_salt('bf'::text));
                    EXCEPTION WHEN OTHERS THEN
                        v_pwd_hash := md5(p_password || 'ggc_salt_2026');
                    END;

                    INSERT INTO public.intermediate_student_profiles (
                        id, username, first_name, last_name, student_name,
                        roll_number, registration_number, program, program_name,
                        password_hash, force_password_change, account_status, created_at
                    ) VALUES (
                        v_new_inter_id, LOWER(v_official_inter.roll_number), COALESCE(v_official_inter.first_name, v_official_inter.student_name),
                        COALESCE(v_official_inter.last_name, ''), COALESCE(v_official_inter.student_name, v_official_inter.first_name),
                        v_official_inter.roll_number, v_official_inter.registration_number,
                        COALESCE(v_official_inter.program_name, 'Intermediate'),
                        COALESCE(v_official_inter.program_name, 'Intermediate'),
                        v_pwd_hash, (p_password = '00000'), 'active', NOW()
                    ) ON CONFLICT (roll_number) DO UPDATE
                    SET password_hash = EXCLUDED.password_hash
                    RETURNING *, COALESCE(program, program_name) AS user_program, COALESCE(first_name, student_name) AS user_first_name INTO v_profile;
                ELSE
                    RETURN jsonb_build_object('success', false, 'error', 'Invalid Intermediate student credentials.');
                END IF;
            END;
        END IF;

        v_profile_user_id := v_profile.id::text;
        v_session_user_key := UPPER(COALESCE(v_profile.roll_number, v_clean_id));
        v_force_pwd_change := COALESCE(v_profile.force_password_change, FALSE);
        v_account_status := COALESCE(v_profile.account_status, 'active');

    ELSIF v_clean_role = 'teacher' OR v_clean_role = 'faculty' OR v_clean_role = 'hod' THEN
        SELECT * INTO v_profile FROM public.faculty_profiles
        WHERE LOWER(TRIM(username)) = LOWER(v_clean_id)
           OR UPPER(TRIM(faculty_id)) = UPPER(v_clean_id)
           OR LOWER(TRIM(institutional_email)) = LOWER(v_clean_id);

        IF NOT FOUND THEN
            RETURN jsonb_build_object('success', false, 'error', 'Invalid Faculty credentials.');
        END IF;

        v_profile_user_id := v_profile.id::text;
        v_session_user_key := UPPER(COALESCE(v_profile.faculty_id, v_clean_id));
        v_force_pwd_change := COALESCE(v_profile.force_password_change, FALSE);
        v_account_status := COALESCE(v_profile.account_status, 'active');

    ELSIF v_clean_role = 'admin' OR v_clean_role = 'super_admin' THEN
        SELECT * INTO v_profile FROM public.admin_profiles
        WHERE LOWER(TRIM(username)) = LOWER(v_clean_id)
           OR LOWER(TRIM(email)) = LOWER(v_clean_id);

        IF NOT FOUND THEN
            RETURN jsonb_build_object('success', false, 'error', 'Invalid Administrator credentials.');
        END IF;

        v_profile_user_id := v_profile.id::text;
        v_session_user_key := 'ADMIN_CENTRAL';
        v_force_pwd_change := COALESCE(v_profile.force_password_change, FALSE);
        v_account_status := COALESCE(v_profile.account_status, 'active');
    ELSE
        RETURN jsonb_build_object('success', false, 'error', 'Unrecognized application role: ' || v_clean_role);
    END IF;

    -- 2. Verify password
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

    -- 3. Check account status
    IF v_account_status <> 'active' THEN
        RETURN jsonb_build_object('success', false, 'error', 'Your account is currently ' || v_account_status || '. Please contact College Administration.');
    END IF;

    -- 4. Check & Enforce Single Active Device Session ("WhatsApp Style")
    SELECT * INTO v_existing_session
    FROM public.user_sessions
    WHERE user_identifier = v_session_user_key
      AND active = TRUE
    FOR UPDATE;

    IF FOUND THEN
        IF v_existing_session.device_id <> v_clean_dev THEN
            -- User is active on another device! BLOCK LOGIN
            RETURN jsonb_build_object(
                'success', false,
                'session_blocked', true,
                'active_device_name', COALESCE(v_existing_session.device_name, 'Another Device'),
                'error', 'This account is currently active on ' || COALESCE(v_existing_session.device_name, 'Another Device') || '. You must log out from that device before logging in here.'
            );
        ELSE
            -- Re-authenticating on the same device: refresh session
            UPDATE public.user_sessions
            SET last_seen_at = NOW(),
                session_token_hash = v_token_hash,
                device_name = v_clean_dev_name
            WHERE id = v_existing_session.id;
        END IF;
    ELSE
        -- No active session exists: create new active session lock
        INSERT INTO public.user_sessions (
            user_id, user_identifier, role, device_id, device_name,
            session_token_hash, active, created_at, last_seen_at
        ) VALUES (
            v_profile_user_id, v_session_user_key, v_clean_role, v_clean_dev, v_clean_dev_name,
            v_token_hash, TRUE, NOW(), NOW()
        );
    END IF;

    -- 5. Return success with profile data and force_password_change flag
    IF v_clean_role = 'student_bs' OR v_clean_role = 'bs' THEN
        RETURN jsonb_build_object(
            'success', true,
            'message', 'BS student login successful!',
            'force_password_change', v_force_pwd_change,
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
    ELSIF v_clean_role = 'student_intermediate' OR v_clean_role = 'intermediate' OR v_clean_role = 'inter' THEN
        RETURN jsonb_build_object(
            'success', true,
            'message', 'Intermediate student login successful!',
            'force_password_change', v_force_pwd_change,
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
    ELSIF v_clean_role = 'teacher' OR v_clean_role = 'faculty' OR v_clean_role = 'hod' THEN
        RETURN jsonb_build_object(
            'success', true,
            'message', 'Faculty login successful!',
            'force_password_change', v_force_pwd_change,
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
    ELSIF v_clean_role = 'admin' OR v_clean_role = 'super_admin' THEN
        RETURN jsonb_build_object(
            'success', true,
            'message', 'Administrator identity verified. Super Control granted.',
            'force_password_change', v_force_pwd_change,
            'profile', jsonb_build_object(
                'id', v_profile.id,
                'username', v_profile.username,
                'full_name', v_profile.full_name,
                'email', v_profile.email,
                'role', v_profile.role,
                'department', v_profile.department
            )
        );
    END IF;
END;
$$;

-- Grant execute permissions to all application roles
GRANT EXECUTE ON FUNCTION public.direct_change_password TO anon, authenticated, service_role;
GRANT EXECUTE ON FUNCTION public.direct_logout_session TO anon, authenticated, service_role;
GRANT EXECUTE ON FUNCTION public.direct_verify_session TO anon, authenticated, service_role;
GRANT EXECUTE ON FUNCTION public.admin_force_terminate_user_session TO anon, authenticated, service_role;
GRANT EXECUTE ON FUNCTION public.atomic_login_user TO anon, authenticated, service_role;
