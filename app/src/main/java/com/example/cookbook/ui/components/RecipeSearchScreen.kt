package com.example.cookbook.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.cookbook.db.entities.Recipe
import com.example.cookbook.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeSearchScreen(
    recipes: List<Recipe>,
    onNavigateBack: () -> Unit,
    onRecipeClick: (Recipe) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    // Sucht im Titel oder in der Anleitung (Groß-/Kleinschreibung egal)
    val filteredRecipes = remember(searchQuery, recipes) {
        if (searchQuery.isBlank()) {
            recipes
        } else {
            recipes.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.markdownInstructions.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rezepte suchen") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.arrow_back_ios_new_24px),
                            contentDescription = "Zurück"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Suchfeld (SearchBar)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Nach Zutat oder Titel suchen...") },
                leadingIcon = { Icon(ImageVector.vectorResource(R.drawable.search_24dp), contentDescription = "suchen") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(ImageVector.vectorResource(R.drawable.search_off_24dp), contentDescription = "Suche leeren")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Ergebnis-Liste
            if (filteredRecipes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Keine passenden Rezepte gefunden.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredRecipes) { recipe: Recipe ->
                        RecipeSearchItem(recipe = recipe, onClick = { onRecipeClick(recipe) })
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeSearchItem(
    recipe: Recipe,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "⭐".repeat(recipe.rating) + " • " + recipe.categories.joinToString { it.name },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}