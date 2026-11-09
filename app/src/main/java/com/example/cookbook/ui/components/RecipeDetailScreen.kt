package com.example.cookbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.cookbook.R
import com.example.cookbook.db.entities.ImageSource
import com.example.cookbook.db.entities.Recipe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipe: Recipe,
    onNavigateBack: () -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (Recipe) -> Unit
) {
    val imageModel = when (val source = recipe.imageUri) {
        is ImageSource.FromUri -> source.uri
        is ImageSource.FromString -> source.urlOrPath
        is ImageSource.FromRes -> source.resId
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(recipe.title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.arrow_back_ios_new_24px),
                            contentDescription = "Zurück"
                        )
                    }
                },
                actions = {
                    // Bearbeiten
                    IconButton(onClick = { onEditClick(recipe.id) }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.edit_24dp),
                            contentDescription = "Bearbeiten"
                        )
                    }

                    // Löschen
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.delete_24dp),
                            contentDescription = "Löschen"
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
                .verticalScroll(rememberScrollState())
        ) {
            // Großes Bild oben
            AsyncImage(
                model = imageModel,
                contentDescription = recipe.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Titel & Sterne
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.headlineLarge
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text(
                        text = "⭐".repeat(recipe.rating),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "•  Dauer: ${recipe.duration.label}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Kategorien als Chips oder Text
                Text(
                    text = "Kategorien: ${recipe.categories.joinToString { it.label }}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Vollständige Anleitung
                Text(
                    text = "Zubereitung",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = recipe.markdownInstructions.ifBlank { "Keine Anleitung hinterlegt." },
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Rezept löschen?") },
            text = { Text("Möchtest du \"${recipe.title}\" wirklich unwiderruflich löschen?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteClick(recipe)
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Löschen")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Abbrechen")
                }
            }
        )
    }
}