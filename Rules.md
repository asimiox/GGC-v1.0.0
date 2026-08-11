# Rules — GGC Mandi Bahauddin

## Documentation Mode
- Currently, only documentation is being created.
- Do NOT write production code yet.
- Wait for: "START PRODUCTION".

## Development
- Follow PRD, Architecture, Phases and Design strictly.
- Do not remove existing working functionality.
- Do not make unnecessary changes.
- Do not add unnecessary dependencies or technologies.
- Keep the code clean, stable and maintainable.
- Test changes before considering them complete.

## College Content
- Never invent college information.
- Use only verified/approved information.
- Do not publish unapproved notices, faculty information or announcements.
- Keep official branding accurate.

## UI/UX
- Follow the official college website's branding and color palette.
- Keep the app professional, clean and consistent with the website.
- No random colors, flashy themes or unrelated visual styles.
- Do not redesign the visual identity without instruction.
- Prioritize usability and accessibility.

## Security
- Never expose API keys, passwords or secrets.
- Use proper authentication and authorization.
- Apply Supabase RLS.
- Students must never access admin-only operations.
- Keep admin roles properly protected.

## Database
- Do not hard-code dynamic college data.
- Use the database for content that administrators need to update.
- Do not delete production data without explicit instruction.

## Files
- Store uploaded PDFs/images in Supabase Storage.
- Store only required metadata/references in the database.

## Releases
- ALWAYS build a Release APK.
- NEVER use Debug APK for production.
- GitHub Actions must use `assembleRelease`.
- Never use `debug.keystore` for production.
- Keep the production signing identity unchanged for future updates.
- Increase Version Code for every release.
- Update Version Name appropriately.

## AI Behavior
- Before making major changes, understand the existing project.
- Do not assume missing requirements.
- If something is unclear or risky, ask before implementing.
- Keep responses and implementation focused and concise.
