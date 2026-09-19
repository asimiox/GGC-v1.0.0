package com.example.data.model

import kotlinx.serialization.Serializable

/**
 * Gemini model types supported per requirements:
 * - gemini-3.1-pro-preview: for particularly complex tasks
 * - gemini-3.5-flash: for general tasks
 * - gemini-3.1-flash-lite: for tasks that should happen fast
 */
enum class GeminiModelType(
    val modelId: String,
    val displayName: String,
    val shortName: String,
    val badgeIcon: String,
    val description: String
) {
    FAST(
        modelId = "gemini-3.1-flash-lite",
        displayName = "Gemini 3.1 Flash Lite",
        shortName = "Flash Lite",
        badgeIcon = "⚡",
        description = "Fastest response for quick campus FAQs, rapid lookups & short queries"
    ),
    GENERAL(
        modelId = "gemini-3.5-flash",
        displayName = "Gemini 3.5 Flash",
        shortName = "Flash 3.5",
        badgeIcon = "✨",
        description = "Balanced intelligence & speed for general campus queries, admissions & chat"
    ),
    COMPLEX(
        modelId = "gemini-3.1-pro-preview",
        displayName = "Gemini 3.1 Pro Preview",
        shortName = "Pro 3.1",
        badgeIcon = "🧠",
        description = "Deep reasoning for complex STEM problem solving, academic research & study plans"
    );

    companion object {
        val default = GENERAL
    }
}

enum class ChatRole {
    USER,
    MODEL
}

/**
 * ChatBot Persona Roles providing specialized system instructions.
 */
enum class ChatBotRole(
    val id: String,
    val title: String,
    val iconEmoji: String,
    val tagline: String,
    val systemInstruction: String
) {
    CAMPUS_ADVISOR(
        id = "campus_advisor",
        title = "Campus Advisor",
        iconEmoji = "🎓",
        tagline = "Official Guide for GGC M.B.Din Admissions, Programs & Facilities",
        systemInstruction = """
            You are the official AI Campus Advisor for Government Graduate College (GGC) Mandi Bahauddin, Punjab, Pakistan.
            Your role is to assist students, parents, faculty, and visitors with accurate, helpful, and courteous guidance.

            College Profile:
            - Institution: Government Graduate College (GGC) Mandi Bahauddin (formerly Govt College Mandi Bahauddin).
            - Affiliation & Board: Affiliated with University of Sargodha / Gujrat and BISE Gujranwala.
            - BS (4-Year) Programs: Computer Science, Information Technology, Physics, Chemistry, Mathematics, Botany, Zoology, English Literature, Urdu, Economics, Islamic Studies, Political Science, and B.Com.
            - Intermediate Programs: FSc Pre-Medical, FSc Pre-Engineering, ICS (Computer Science), I.Com, and F.A.
            - Facilities: Central Academic Library, State-of-the-Art Computer & AI Labs, Physics & Chemistry Labs, Sports Complex (Cricket, Football, Badminton), Examination Hall, College Canteen, and Student Societies.
            
            Guidelines:
            1. Be warm, professional, encouraging, and respectful.
            2. Format responses with clear headings, bullet points, and steps where helpful.
            3. For fees, admission dates, and official queries, provide general college timelines (Fall/Spring admissions) and advise checking the College Notice Board or Admin Office.
            4. Support queries in both English and Urdu as preferred by the user.
        """.trimIndent()
    ),
    STUDY_TUTOR(
        id = "study_tutor",
        title = "Study Tutor",
        iconEmoji = "📖",
        tagline = "Concepts, STEM Solutions, Coding & Exam Preparation",
        systemInstruction = """
            You are an expert Academic Tutor and Study Mentor for students at Government Graduate College (GGC) Mandi Bahauddin.
            Your role is to help students excel academically across all subjects, particularly Computer Science, Physics, Chemistry, Mathematics, English, and Economics.

            Guidelines:
            1. Explain complex concepts clearly using step-by-step reasoning, intuitive analogies, and real-world examples.
            2. If solving a math or science problem, show the full formula, step-by-step calculation, and final result.
            3. If discussing programming, provide clean, idiomatic code snippets with brief explanations.
            4. Provide helpful study tips, memory mnemonics, and exam preparation strategies.
            5. Encourage curiosity and deep understanding rather than mere memorization.
        """.trimIndent()
    ),
    QUICK_HELPER(
        id = "quick_helper",
        title = "Quick FAQ",
        iconEmoji = "⚡",
        tagline = "Fast & Direct Answers to Campus Inquiries",
        systemInstruction = """
            You are a fast, concise Campus Assistant for GGC Mandi Bahauddin.
            Your role is to provide quick, direct, and factual answers without unnecessary greetings or lengthy introductions.
            Answer in 2 to 4 crisp sentences or quick bullet points. Focus purely on the requested information.
        """.trimIndent()
    ),
    CAREER_COUNSELOR(
        id = "career_counselor",
        title = "Career Mentor",
        iconEmoji = "💼",
        tagline = "Career Roadmaps, Scholarships & Post-Graduate Admissions",
        systemInstruction = """
            You are a Career and Higher Education Counselor for students and alumni of GGC Mandi Bahauddin.
            Your role is to assist students with post-graduation roadmaps, university admissions (MS/MPhil/PhD), scholarship programs (HEC, Ehsaas, PEEF, Fulbright, Erasmus), competitive exams (CSS, PMS), IT/freelancing careers, and resume development.
            Provide actionable, motivating, and realistic career guidance.
        """.trimIndent()
    );

    companion object {
        val default = CAMPUS_ADVISOR
    }
}

@Serializable
data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val role: ChatRole,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val modelType: GeminiModelType? = null,
    val isError: Boolean = false,
    val roleUsedName: String? = null
)

// --- Gemini REST API Request & Response DTOs ---

@Serializable
data class GeminiGenerateRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null,
    val generationConfig: GeminiGenerationConfig? = null
)

@Serializable
data class GeminiContent(
    val role: String? = null, // "user" or "model"
    val parts: List<GeminiPart>
)

@Serializable
data class GeminiPart(
    val text: String
)

@Serializable
data class GeminiGenerationConfig(
    val temperature: Float? = 0.7f,
    val topP: Float? = 0.95f,
    val topK: Int? = 40
)

@Serializable
data class GeminiGenerateResponse(
    val candidates: List<GeminiCandidate>? = null,
    val error: GeminiApiError? = null
)

@Serializable
data class GeminiCandidate(
    val content: GeminiContent? = null,
    val finishReason: String? = null
)

@Serializable
data class GeminiApiError(
    val code: Int? = null,
    val message: String? = null,
    val status: String? = null
)
