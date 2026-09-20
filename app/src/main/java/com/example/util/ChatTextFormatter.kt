package com.example.util

/**
 * Utility for formatting and sanitizing Gemini AI chat responses.
 *
 * Enforces clean, minimal, human-readable text by:
 * - Eliminating markdown heading hashes (e.g. #, ##, ###, ####)
 * - Eliminating all bolding, italic, and emphasis asterisks (*, **, ***)
 * - Converting markdown list markers (*, -) into clean, elegant unicode bullets (•)
 * - Removing stray markdown noise (horizontal rules, dangling stars, backticks)
 * - Normalizing spacing and blank lines for minimal, readable reading flow.
 */
object ChatTextFormatter {

    fun clean(raw: String): String {
        if (raw.isBlank()) return ""

        val lines = raw.lines()
        val cleanedLines = mutableListOf<String>()

        for (line in lines) {
            var l = line.trimEnd()

            // Remove markdown horizontal rules (e.g. ---, ***, ___)
            if (l.trim().matches(Regex("""^[-*_]{3,}$"""))) {
                continue
            }

            // 1. Strip markdown header hashes at start of line:
            // e.g. "### 1. Computing & IT" -> "1. Computing & IT", "## Overview" -> "Overview"
            l = l.replace(Regex("""^\s*#{1,6}\s*"""), "")

            // 2. Convert list asterisks, hyphens, and pluses into clean unicode bullets
            // Preserves indentation for nested bullets
            val bulletMatch = Regex("""^(\s*)([\*\-\+])\s+(.*)$""").find(l)
            if (bulletMatch != null) {
                val indent = bulletMatch.groupValues[1]
                val content = bulletMatch.groupValues[3]
                l = "$indent• $content"
            }

            // 3. Strip bold/italic asterisks:
            // Triple asterisks: ***text*** -> text
            l = l.replace(Regex("""\*{3}(.+?)\*{3}"""), "$1")
            // Double asterisks: **text** -> text
            l = l.replace(Regex("""\*{2}(.+?)\*{2}"""), "$1")
            // Single asterisks: *text* -> text
            l = l.replace(Regex("""\*(.+?)\*"""), "$1")

            // 4. Strip markdown bold/italic underscores: __text__ -> text, _text_ -> text
            l = l.replace(Regex("""_{2}(.+?)_{2}"""), "$1")
            l = l.replace(Regex("""(?<=\s|^)_(.+?)_(?=\s|$)"""), "$1")

            // 5. Strip inline code backticks: `code` -> code
            l = l.replace(Regex("""`([^`]+)`"""), "$1")

            // 6. Strip code fence lines like ```kotlin or ```
            if (l.trim().startsWith("```")) {
                continue
            }

            // 7. Strip blockquote prefixes: > Text -> Text
            l = l.replace(Regex("""^\s*>\s*"""), "")

            // 8. Strip any remaining solitary asterisks (e.g. stray * or **)
            l = l.replace("*", "")

            // 9. Strip any remaining heading hashes that might have slipped through
            l = l.replace(Regex("""^\s*#{1,6}\s*"""), "")

            cleanedLines.add(l)
        }

        // Rejoin and collapse excessive consecutive blank lines
        val joined = cleanedLines.joinToString("\n")
        return joined
            .replace(Regex("""\n{3,}"""), "\n\n")
            .trim()
    }
}
