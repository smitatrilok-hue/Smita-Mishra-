package com.example.ui.theme

import androidx.compose.ui.graphics.Color

data class AdPalette(
    val id: String,
    val name: String,
    val backgroundHex: String,
    val textHex: String,
    val primaryHex: String,
    val secondaryHex: String,
    val description: String
) {
    val backgroundColor: Color get() = Color(android.graphics.Color.parseColor(backgroundHex))
    val textColor: Color get() = Color(android.graphics.Color.parseColor(textHex))
    val primaryColor: Color get() = Color(android.graphics.Color.parseColor(primaryHex))
    val secondaryColor: Color get() = Color(android.graphics.Color.parseColor(secondaryHex))
}

object AdDesignTheme {
    val palettes = listOf(
        AdPalette(
            id = "bold_typography",
            name = "Bold Typography",
            backgroundHex = "#FEF7FF", // Soft warm purple/white
            textHex = "#21005D",       // Deep dark violet
            primaryHex = "#6750A4",    // Rich violet
            secondaryHex = "#EADDFF",  // Lavender
            description = "High-impact layouts with dominant dark violet headlines, extra-bold weights, and rounded elements."
        ),
        AdPalette(
            id = "modern_minimalist",
            name = "Modern Minimalist",
            backgroundHex = "#F8F9FA",
            textHex = "#1F2937",
            primaryHex = "#10B981", // Emerald
            secondaryHex = "#E5E7EB", // Light Gray
            description = "Clean layouts, generous padding, and crisp emerald accents."
        ),
        AdPalette(
            id = "premium_luxury",
            name = "Premium Luxury",
            backgroundHex = "#121212",
            textHex = "#F9FAFB",
            primaryHex = "#D4AF37", // Metallic Gold
            secondaryHex = "#2D2D2D", // Dark Slate
            description = "Obsidian canvases combined with exquisite metallic gold features."
        ),
        AdPalette(
            id = "playful_vibrant",
            name = "Playful & Vibrant",
            backgroundHex = "#FFFBEB", // Soft Warm Cream
            textHex = "#1E1B4B", // Ink Blue
            primaryHex = "#FF5A5F", // Electric Coral
            secondaryHex = "#FBBF24", // Yellow
            description = "Upbeat, energetic color pairing filled with dynamic contrast."
        ),
        AdPalette(
            id = "cyber_tech",
            name = "High-Contrast Tech",
            backgroundHex = "#0A0F1D", // Space Navy
            textHex = "#E0F2FE", // Sky Tint
            primaryHex = "#06B6D4", // Cyan
            secondaryHex = "#8B5CF6", // Electric Violet
            description = "Futuristic cyber feel styled with vibrant neon gradients."
        )
    )

    fun getPaletteById(id: String): AdPalette {
        return palettes.find { it.id == id } ?: palettes.first()
    }
}
