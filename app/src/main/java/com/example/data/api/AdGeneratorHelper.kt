package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AdGeneratorHelper {
    private const val TAG = "AdGeneratorHelper"

    /**
     * Checks if a valid-looking Gemini API key is configured.
     */
    fun isApiKeyConfigured(): Boolean {
        val key = BuildConfig.GEMINI_API_KEY
        return key.isNotEmpty() && key != "MY_GEMINI_API_KEY" && !key.contains("PLACEHOLDER")
    }

    /**
     * Generates ad copy. Uses Gemini API if configured, otherwise falls back to local generation.
     */
    suspend fun generateAdCopy(
        brandName: String,
        productDetails: String,
        objective: String,
        theme: String
    ): Result<AdGenerationResult> = withContext(Dispatchers.IO) {
        if (!isApiKeyConfigured()) {
            Log.w(TAG, "Gemini API Key is not configured. Using local rule-based generation.")
            return@withContext Result.success(generateFallbackCopy(brandName, productDetails, objective, theme))
        }

        val prompt = """
            You are an expert digital marketing copywriter. Create a highly engaging, high-converting website display ad copy based on these details:
            Brand Name: $brandName
            Product/Service Details: $productDetails
            Campaign Goal / Objective: $objective
            Design Aesthetic / Vibe: $theme

            Generate a single JSON object containing:
            - headline: A short, extremely punchy, attention-grabbing headline (maximum 40 characters) that fits perfectly on standard web banner ads (leaderboard, rectangle, skyscraper).
            - description: A persuasive, clear, value-driven description (maximum 90 characters) highlighting the primary benefit.
            - ctaText: A strong action-oriented button text (maximum 15 characters, e.g. 'Shop Now', 'Join Free', 'Learn More', 'Get 20% Off').
        """.trimIndent()

        // Configure the JSON schema response format
        val schema = ResponseSchema(
            type = "OBJECT",
            properties = mapOf(
                "headline" to SchemaProperty("STRING", "Punchy ad headline under 40 characters"),
                "description" to SchemaProperty("STRING", "Persuasive description under 90 characters"),
                "ctaText" to SchemaProperty("STRING", "Action button text under 15 characters")
            ),
            required = listOf("headline", "description", "ctaText")
        )

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(
                responseMimeType = "application/json",
                responseSchema = schema,
                temperature = 0.8f
            ),
            systemInstruction = Content(
                parts = listOf(Part(text = "You are a professional conversion copywriter. You write short, high-impact ad text for display banners. You strictly return JSON matching the requested schema."))
            )
        )

        try {
            val response = RetrofitClient.service.generateContent(BuildConfig.GEMINI_API_KEY, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (jsonText != null) {
                val parsed = RetrofitClient.adResultAdapter.fromJson(jsonText)
                if (parsed != null && parsed.headline.isNotEmpty()) {
                    return@withContext Result.success(parsed)
                }
            }
            throw Exception("Failed to parse Gemini response or empty result.")
        } catch (e: Exception) {
            Log.e(TAG, "Gemini API call failed, falling back to local copy.", e)
            return@withContext Result.success(generateFallbackCopy(brandName, productDetails, objective, theme))
        }
    }

    /**
     * Local fallback generator to guarantee the app remains functional.
     */
    private fun generateFallbackCopy(
        brandName: String,
        productDetails: String,
        objective: String,
        theme: String
    ): AdGenerationResult {
        val name = brandName.ifBlank { "Our Brand" }
        val details = productDetails.ifBlank { "our premium product line" }

        return when (objective.lowercase()) {
            "brand awareness" -> {
                when (theme.lowercase()) {
                    "modern minimalist" -> AdGenerationResult(
                        headline = "Simply $name.",
                        description = "Discover a refined approach to $details. Pure craftsmanship.",
                        ctaText = "Explore More"
                    )
                    "premium luxury" -> AdGenerationResult(
                        headline = "Experience $name",
                        description = "The ultimate standard of luxury for $details. Elevate your everyday.",
                        ctaText = "Indulge Now"
                    )
                    "playful & vibrant" -> AdGenerationResult(
                        headline = "Say Hello to $name!",
                        description = "Brighten your life with $details! Fun, fresh, and made for you.",
                        ctaText = "Get Happy"
                    )
                    else -> AdGenerationResult(
                        headline = "The Future is $name.",
                        description = "Cutting-edge solutions in $details. Stay ahead of the curve.",
                        ctaText = "Enter Future"
                    )
                }
            }
            "product sale" -> {
                when (theme.lowercase()) {
                    "modern minimalist" -> AdGenerationResult(
                        headline = "Essential Deal: $name",
                        description = "Save 20% on $details. Clean design meets daily necessity.",
                        ctaText = "Shop Sale"
                    )
                    "premium luxury" -> AdGenerationResult(
                        headline = "$name: Exclusive Event",
                        description = "Limited release of our premium $details. Complimentary shipping included.",
                        ctaText = "Acquire Now"
                    )
                    "playful & vibrant" -> AdGenerationResult(
                        headline = "Huge Sale! 30% Off $name",
                        description = "Don't miss out on $details! Grab yours before they're gone!",
                        ctaText = "Buy & Save"
                    )
                    else -> AdGenerationResult(
                        headline = "Upgrade Today: $name",
                        description = "Unlock 25% off $details. Instant deployment, maximum value.",
                        ctaText = "Claim Discount"
                    )
                }
            }
            else -> { // Lead Generation / Signups
                when (theme.lowercase()) {
                    "modern minimalist" -> AdGenerationResult(
                        headline = "Stay Informed with $name.",
                        description = "Join our community and master $details. Weekly insights.",
                        ctaText = "Subscribe"
                    )
                    "premium luxury" -> AdGenerationResult(
                        headline = "Join the $name Inner Circle",
                        description = "Request an invite to access bespoke services for $details.",
                        ctaText = "Request Invite"
                    )
                    "playful & vibrant" -> AdGenerationResult(
                        headline = "Unlock Free $name Stuff!",
                        description = "Sign up today and get the ultimate guide to $details for free!",
                        ctaText = "Sign Up Free"
                    )
                    else -> AdGenerationResult(
                        headline = "Build Smarter with $name.",
                        description = "Get instant access to $details tools and advanced developer features.",
                        ctaText = "Register Now"
                    )
                }
            }
        }
    }
}
