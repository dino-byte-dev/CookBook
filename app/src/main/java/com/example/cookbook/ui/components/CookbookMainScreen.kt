package com.example.cookbook.ui.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.cookbook.R
import com.example.cookbook.db.entities.Recipe
import com.example.cookbook.db.entities.RecipeCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookbookMainScreen(
    recipes: List<Recipe>,
    onNavigateToAdd: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onRecipeClick: (Recipe) -> Unit
) {
    // Aktuell ausgewählter Tab (Start mit Frühstück / Morgens)
    var selectedCategory by remember { mutableStateOf(RecipeCategory.BREAKFAST) }

    Scaffold(
        topBar = {
            CookbookTopBar(
                onNavigateToAdd = onNavigateToAdd,
                onNavigateToSearch = onNavigateToSearch
            )
        },
        bottomBar = {
            // Tabs unten für die Kategorien (Morgens, Mittags, Abends)
            NavigationBar {
                NavigationBarItem(
                    selected = selectedCategory == RecipeCategory.BREAKFAST,
                    onClick = { selectedCategory = RecipeCategory.BREAKFAST },
                    icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.morning_cafe_24dp),
                            contentDescription = "Rezept hinzufügen",
                            modifier = Modifier.size(32.dp))
                           },
                    label = { Text("Morgens", style = MaterialTheme.typography.bodyLarge) }
                )
                NavigationBarItem(
                    selected = selectedCategory == RecipeCategory.LUNCH,
                    onClick = { selectedCategory = RecipeCategory.LUNCH },
                    icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.dine_heart_24dp),
                            contentDescription = "Rezept hinzufügen",
                            modifier = Modifier.size(32.dp))
                    },
                    label = { Text("Mittags", style = MaterialTheme.typography.bodyLarge) }
                )
                NavigationBarItem(
                    selected = selectedCategory == RecipeCategory.DINNER,
                    onClick = { selectedCategory = RecipeCategory.DINNER },
                    icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.late_dinner_24dp),
                            contentDescription = "Rezept hinzufügen",
                            modifier = Modifier.size(32.dp))
                    },
                    label = { Text("Abends", style = MaterialTheme.typography.bodyLarge) }
                )
            }
        }
    ) { innerPadding ->
        val topRecipes: List<Recipe> = recipes
            .filter { it.categories.contains(selectedCategory) }
            .sortedByDescending { it.rating }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Das Swipe-Karussell für die gewählte Kategorie
            if (topRecipes.isNotEmpty()) {
                CategoryRecipeSwipeCarousel(
                    recipesOfCategory = topRecipes,
                    categoryTitle = "Rezeptideen fürs ${selectedCategory.label}",
                    onRecipeClick = onRecipeClick
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Kein Rezept für diese Kategorie gefunden.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 2. Aktions-Karten (Hinzufügen & Suchen)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Kachel: Neues Rezept hinzufügen
                OutlinedCard(
                    onClick = { onNavigateToAdd() },
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.note_add_24dp),
                            contentDescription = "Rezept hinzufügen",
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "Rezept hinzufügen",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Kachel: Rezept suchen
                OutlinedCard(
                    onClick = { onNavigateToSearch() },
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.document_search_24dp),
                            contentDescription = "Rezept suchen",
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "Rezept suchen",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}