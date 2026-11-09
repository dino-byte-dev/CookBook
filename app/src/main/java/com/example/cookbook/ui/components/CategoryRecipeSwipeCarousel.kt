package com.example.cookbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.cookbook.R
import com.example.cookbook.db.entities.ImageSource
import com.example.cookbook.db.entities.Recipe
import kotlinx.coroutines.launch

@Composable
fun CategoryRecipeSwipeCarousel(
    recipesOfCategory: List<Recipe>,
    categoryTitle: String,
    onRecipeClick: (Recipe) -> Unit
) {
    if (recipesOfCategory.isEmpty()) return

    // 1. Pager State initialisieren (anzahl der Seiten = Anzahl der Rezepte)
    val pagerState = rememberPagerState(pageCount = { recipesOfCategory.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Optional: Überschrift der Kategorie (z.B. Rezeptideen fürs Frühstück)
        Text(
            text = categoryTitle,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // 2. Box für den Pager und die Overlay-Pfeilbuttons
        Box(
            modifier = Modifier
                .fillMaxWidth()
                // Todo Height is hardcoded 400.dp should change this later so it takes the
                // Todo remaining height. Right now, it's okay.
                .height(400.dp)
        ) {
            // Horizontale Pager (hier swiped der Nutzer)
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                pageSpacing = 12.dp
            ) { page ->
                val recipe = recipesOfCategory[page]

                // Recipe-Card für die jeweilige Seite
                RecipeInspirationCardSingle(
                    recipe = recipe,
                    onRecipeClick = onRecipeClick
                )
            }

            // PFEIL-BUTTONS LINKS & RECHTS (Floating über dem Pager)

            // Linke Pfeil-Schaltfläche (nur anzeigen, wenn wir nicht auf Seite 0 sind)
            if (pagerState.currentPage > 0) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 24.dp)
                        .size(48.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.arrow_back_ios_new_24px),
                        contentDescription = "Zurück"
                    )
                }
            }

            // Rechte Pfeil-Schaltfläche (nur anzeigen, wenn wir nicht auf der letzten Seite sind)
            if (pagerState.currentPage < recipesOfCategory.size - 1) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 24.dp)
                        .size(48.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.arrow_forward_ios_24px),
                        contentDescription = "Vorwärts"
                    )
                }
            }
        }
    }
}

@Composable
fun RecipeInspirationCardSingle(recipe: Recipe, onRecipeClick: (Recipe) -> Unit) {
    val imageModel = when (val source = recipe.imageUri) {
        is ImageSource.FromUri -> source.uri
        is ImageSource.FromString -> source.urlOrPath
        is ImageSource.FromRes -> source.resId
    }

    Card(
        modifier = Modifier.fillMaxSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        onClick = { onRecipeClick(recipe) }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = imageModel,
                contentDescription = recipe.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⭐".repeat(recipe.rating),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = recipe.categories.joinToString(prefix = "Kategorien:", separator = ",") { it.label },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}