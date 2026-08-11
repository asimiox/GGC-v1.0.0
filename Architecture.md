# Architecture — GGC Mandi Bahauddin

## Mode
Documentation Mode only.
Do NOT write production code until explicitly instructed:
"START PRODUCTION"

## Platform
- Android
- Kotlin
- Jetpack Compose
- Android SDK

## Architecture
Use a clean, maintainable architecture:

UI
↓
ViewModel
↓
Repository
↓
Data Source
↓
Supabase

## Backend
- Supabase
- PostgreSQL
- Supabase Auth
- Supabase Storage

## Notifications
- Firebase Cloud Messaging (FCM)

## Main Data
- Users
- Departments
- Programs
- Semesters
- Subjects
- Faculty
- Notices
- Events
- Timetables
- Exams
- Assignments
- Resources
- Notifications

## User Roles
- Student
- Faculty
- HOD
- Super Admin

## Security
- Supabase Row Level Security (RLS)
- Role-based permissions
- Never expose secret keys in the app
- Admin actions must be authenticated

## Files
Academic PDFs/images and other uploaded files must use
Supabase Storage. Database stores their metadata/references.

## Offline
Cache essential previously loaded content locally where practical.

## Release
- GitHub Actions
- Always build Release APK
- Never use assembleDebug for releases
- Production signing must use a proper release key
- Keep the same signing identity for future updates

## Important
Keep the architecture simple, stable and production-ready.
Do not add unnecessary technologies or dependencies.
