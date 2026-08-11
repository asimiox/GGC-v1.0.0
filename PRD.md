# PRD — GGC Mandi Bahauddin

## Purpose
A professional official companion app for Government Graduate College
Mandi Bahauddin, providing students and administration with academic
resources, notices, events, faculty information and college services.

## Target Users
- Students
- Faculty
- HODs
- College Administration

## Core Features

### Student
- Home Dashboard
- Programs & Departments
- Subjects & Course Outlines
- Notes, PDFs & Past Papers
- Timetable
- Exam Schedule
- Assignments
- Notices & Announcements
- Events
- Faculty Directory
- GPA/CGPA Calculator
- College Information
- Notifications
- Profile

### Administration
- Secure Admin Login
- Manage Departments & Programs
- Manage Subjects & Faculty
- Publish Notices & Announcements
- Manage Events
- Upload Academic Resources
- Manage Timetables & Exam Schedules
- Send Notifications
- Approval Workflow
- Activity/Audit Logs

## Roles
- Super Admin — Full control
- HOD — Manage own department
- Faculty — Manage assigned academic content
- Student — Access permitted student features

## Design Requirements
- Official and professional appearance
- Follow the existing GGC Mandi Bahauddin website branding
- Use the website's color palette and visual identity
- No unrelated colors or flashy themes
- Clean, accessible and consistent UI
- College logo and official branding must be used appropriately

## Technical Requirements
- Kotlin
- Android SDK
- Jetpack Compose
- Supabase Backend
- PostgreSQL Database
- Supabase Storage
- Secure Authentication
- Push Notifications
- Offline caching for essential content

## Important
- Do not invent official college information.
- Only approved/verified content may be published.
- App must remain stable and production-ready.
- Release APK must always be built through GitHub Actions.
- Never use a Debug APK for release.
