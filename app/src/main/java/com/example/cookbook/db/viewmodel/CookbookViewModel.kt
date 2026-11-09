package com.example.cookbook.db.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookbook.db.CookbookDatabase
import com.example.cookbook.db.entities.Recipe
import com.example.cookbook.db.entities.RecipeEntity
import com.example.cookbook.db.entities.toDomain
import com.example.cookbook.db.entities.toEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.*

private fun mapEntitiesToDomain(entities: List<RecipeEntity>): List<Recipe> {
    return entities.map { it.toDomain() }
}

class CookbookViewModel(application: Application) : AndroidViewModel(application) {
    private val recipeDao = CookbookDatabase.getDatabase(application).recipeDao()

    // Die Rezept-Liste als StateFlow, die sich automatisch aktualisiert, wenn sich die DB ändert
    val recipes: StateFlow<List<Recipe>> = recipeDao.getAllRecipesFlow()
        .map { list -> mapEntitiesToDomain(list) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addRecipe(recipe: Recipe) {
        viewModelScope.launch {
            recipeDao.insertRecipe(recipe.toEntity())
        }
    }

    fun updateRecipe(recipe: Recipe) {
        viewModelScope.launch {
            recipeDao.updateRecipe(recipe.toEntity())
        }
    }

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            recipeDao.deleteRecipe(recipe.toEntity())
        }
    }
}