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

        var text = raw

        // Remove horizontal rules (---, ***, ___)
        text = text.replace(Regex("""(?m)^[-*_]{3,}\s*$"""), "")

        // Strip markdown header hashes at start of lines (e.g. #, ##, ###, ####)
        text = text.replace(Regex("""(?m)^\s*#{1,6}\s*"""), "")

        // Convert list bullets (*, -, +) at start of lines to clean unicode bullet (•)
        text = text.replace(Regex("""(?m)^(\s*)[\*\-\+]\s+"""), "$1• ")

        // Strip bold/italic asterisks (***text***, **text**, *text*)
        text = text.replace(Regex("""\*{1,3}(.+?)\*{1,3}"""), "$1")

        // Strip any residual standalone or grouped asterisks anywhere (*, **, ***)
        text = text.replace("*", "")

        // Strip any residual hashes anywhere
        text = text.replace("#", "")

        // Strip markdown bold/italic underscores
        text = text.replace(Regex("""_{1,2}(.+?)_{1,2}"""), "$1")

        // Strip blockquotes
        text = text.replace(Regex("""(?m)^\s*>\s*"""), "")

        // Strip backticks
        text = text.replace("`", "")

        // Clean up empty lines and normalize spacing
        return text
            .lines()
            .joinToString("\n") { it.trimEnd() }
            .replace(Regex("""\n{3,}"""), "\n\n")
            .trim()
    }
}
