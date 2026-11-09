package com.example.cookbook.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cookbook.R

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val markdownInstructions: String,
    val durationName: String, // Speichert z. B. "SHORT", "LONG" als String
    val categoriesCsv: String, // Speichert Kategorien als kommaseparierten String (z. B. "BREAKFAST,LUNCH")
    val imageType: String, // "RES", "URI", oder "STRING"
    val imageValue: String, // Entweder die Res-ID, der URI-String oder die URL
    val rating: Int
)

// Hilfs-Erweiterungen zum einfachen Umwandeln zwischen UI-Modell (Recipe) und DB-Modell (RecipeEntity)
fun Recipe.toEntity(): RecipeEntity {
    val (type, value) = when (val src = this.imageUri) {
        is ImageSource.FromRes -> "RES" to src.resId.toString()
        is ImageSource.FromUri -> "URI" to src.uri.toString()
        is ImageSource.FromString -> "STRING" to src.urlOrPath
    }
    return RecipeEntity(
        id = id,
        title = title,
        markdownInstructions = markdownInstructions,
        durationName = duration.name,
        categoriesCsv = categories.joinToString(",") { it.name },
        imageType = type,
        imageValue = value,
        rating = rating
    )
}

fun RecipeEntity.toDomain(): Recipe {
    val imageSource = when (imageType) {
        "RES" -> ImageSource.FromRes(imageValue.toIntOrNull() ?: R.drawable.placeholder_recipe)
        "URI" -> ImageSource.FromUri(android.net.Uri.parse(imageValue))
        else -> ImageSource.FromString(imageValue)
    }

    val parsedCategories = categoriesCsv
        .split(",")
        .filter { it.isNotBlank() }
        .mapNotNull { name ->
            RecipeCategory.entries.find { it.name == name }
        }

    val parsedDuration = RecipeDuration.entries.find { it.name == durationName } ?: RecipeDuration.FAST

    return Recipe(
        id = id,
        title = title,
        markdownInstructions = markdownInstructions,
        duration = parsedDuration,
        categories = parsedCategories,
        imageUri = imageSource,
        rating = rating
    )
}