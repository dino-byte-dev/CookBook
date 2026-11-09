package com.example.cookbook.db.entities

import android.net.Uri
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.example.cookbook.R

data class Recipe(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val markdownInstructions: String,
    val duration: RecipeDuration,
    val categories: List<RecipeCategory>,
    val imageUri: ImageSource,
    val rating: Int // 1 bis 5 Sterne
) {
    // Hilfsfunktion: Holt exakt die ersten 13 Wörter aus dem Markdown-Text als Teaser
    val instructionTeaser: String
        get() {
            // Markdown-Zeilen/Symbole etwas bereinigen
            val words = markdownInstructions
                .replace(Regex("[#*_`\\[\\]()]"), "")
                .split(Regex("\\s+"))
                .filter { it.isNotBlank() }

            return if (words.size <= 13) {
                words.joinToString(" ")
            } else {
                words.take(13).joinToString(" ") + "..."
            }
        }
}

enum class RecipeDuration(val label: String, val durationText: String) {
    FAST("schnell", "Bis zu 20 min"),
    MEDIUM("mittel", "20 bis 45 min"),
    LONG("lang", "45 bis 90 min"),
    VERY_LONG("sehr lang", "Über 90 min")
}

enum class RecipeCategory(val label: String) {
    BREAKFAST("Frühstück"),
    LUNCH("Mittagessen"),
    DINNER("Abendessen")
}