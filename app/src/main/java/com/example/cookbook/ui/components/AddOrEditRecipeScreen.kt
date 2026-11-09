package com.example.cookbook.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.cookbook.R
import com.example.cookbook.db.entities.ImageSource
import com.example.cookbook.db.entities.Recipe
import com.example.cookbook.db.entities.RecipeCategory
import com.example.cookbook.db.entities.RecipeDuration
import com.example.cookbook.db.utils.saveImageToAppFolder
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrEditRecipeScreen(
    existingRecipe: Recipe? = null,
    onNavigateBack: () -> Unit,
    onSaveRecipe: (Recipe) -> Unit
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf(existingRecipe?.title ?: "") }
    var instructions by remember { mutableStateOf(existingRecipe?.markdownInstructions ?: "") }
    var selectedDuration by remember { mutableStateOf(existingRecipe?.duration ?: RecipeDuration.FAST) }
    var selectedCategories by remember {
        mutableStateOf<Set<RecipeCategory>>(existingRecipe?.categories?.toSet() ?: emptySet())
    }
    var rating by remember { mutableIntStateOf(existingRecipe?.rating ?: 5) }
    var imageSource by remember {
        mutableStateOf<ImageSource>(existingRecipe?.imageUri ?: ImageSource.FromRes(R.drawable.placeholder_recipe))
    }

    var isInstructionsFocused by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val savedPath = saveImageToAppFolder(context, uri)
            if (savedPath != null) {
                imageSource = ImageSource.FromString(savedPath)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existingRecipe == null) "Neues Rezept" else "Rezept bearbeiten") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Bild-Vorschau (wird bei Fokus auf Anleitung ausgeblendet)
            if (!isInstructionsFocused) {
                RecipeImageCard(
                    imageSource = imageSource,
                    onClick = {
                        imagePickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )
            }

            // 2. Metadaten (Titel, Dauer, Kategorien, Bewertung) – ebenfalls im Fokusmodus ausgeblendet
            if (!isInstructionsFocused) {
                RecipeMetadataSection(
                    title = title,
                    onTitleChange = { title = it },
                    selectedDuration = selectedDuration,
                    onDurationSelected = { selectedDuration = it },
                    selectedCategories = selectedCategories,
                    onCategoryToggled = { category ->
                        selectedCategories = if (selectedCategories.contains(category)) {
                            selectedCategories - category
                        } else {
                            selectedCategories + category
                        }
                    },
                    rating = rating,
                    onRatingChanged = { rating = it }
                )
            }

            // 3. Anleitung-Textfeld (wächst im Fokus)
            RecipeInstructionsSection(
                instructions = instructions,
                onInstructionsChange = { instructions = it },
                isFocused = isInstructionsFocused,
                onFocusChanged = { isInstructionsFocused = it }
            )

            // 4. Speichern-Button
                Button(
                    onClick = {
                        if (title.isNotBlank() && selectedCategories.isNotEmpty()) {
                            val recipeToSave = Recipe(
                                id = existingRecipe?.id ?: UUID.randomUUID().toString(),
                                title = title.trim(),
                                markdownInstructions = instructions.trim(),
                                duration = selectedDuration,
                                categories = selectedCategories.toList(),
                                imageUri = imageSource,
                                rating = rating
                            )
                            onSaveRecipe(recipeToSave)
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = title.isNotBlank() && selectedCategories.isNotEmpty()
                ) {
                    Text(if (existingRecipe == null) "Rezept speichernn" else "Änderungen speichernn")
                }
        }
    }
}

@Composable
fun RecipeImageCard(
    imageSource: ImageSource,
    onClick: () -> Unit
) {
    val imageModel = when (imageSource) {
        is ImageSource.FromUri -> imageSource.uri
        is ImageSource.FromString -> imageSource.urlOrPath
        is ImageSource.FromRes -> imageSource.resId
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable(onClick = onClick)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AsyncImage(
                model = imageModel,
                contentDescription = "Rezept Bild",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "📸 Bild ändern",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(6.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun RecipeMetadataSection(
    title: String,
    onTitleChange: (String) -> Unit,
    selectedDuration: RecipeDuration,
    onDurationSelected: (RecipeDuration) -> Unit,
    selectedCategories: Set<RecipeCategory>,
    onCategoryToggled: (RecipeCategory) -> Unit,
    rating: Int,
    onRatingChanged: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Titel
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Rezept-Titel") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Zubereitungsdauer
        Text("Dauer", style = MaterialTheme.typography.titleMedium)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            RecipeDuration.entries.forEach { duration ->
                FilterChip(
                    selected = selectedDuration == duration,
                    onClick = { onDurationSelected(duration) },
                    label = { Text(duration.name) }
                )
            }
        }

        // Kategorien
        Text("Kategorien", style = MaterialTheme.typography.titleMedium)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            RecipeCategory.entries.forEach { category ->
                val isSelected = selectedCategories.contains(category)
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategoryToggled(category) },
                    label = { Text(category.name) }
                )
            }
        }

        // Sterne-Bewertung
        Text("Bewertung", style = MaterialTheme.typography.titleMedium)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (i in 1..5) {
                val star = ImageVector.vectorResource(R.drawable.star_24dp)
                val fullStar = ImageVector.vectorResource(R.drawable.star_full_24dp)

                Icon(
                    imageVector = if (i <= rating) star else fullStar,
                    contentDescription = "$i Sterne",
                    tint = if (i <= rating) Color(0xFFFFC107) else Color.Gray,
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onRatingChanged(i) }
                )
            }
        }
    }
}

@Composable
fun RecipeInstructionsSection(
    instructions: String,
    onInstructionsChange: (String) -> Unit,
    isFocused: Boolean,
    onFocusChanged: (Boolean) -> Unit
) {
    OutlinedTextField(
        value = instructions,
        onValueChange = onInstructionsChange,
        label = { Text("Anleitung (Markdown unterstützt)") },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = if (isFocused) 450.dp else 150.dp)
            .onFocusChanged { focusState ->
                onFocusChanged(focusState.isFocused)
            },
        maxLines = if (isFocused) 30 else 6
    )
}