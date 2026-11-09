package com.example.cookbook.ui.components

import android.content.Context
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cookbook.db.entities.Recipe
import com.example.cookbook.db.viewmodel.CookbookViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

// Definition der Navigations-Routen (Screens)
sealed class Screen(val route: String) {
    object Main : Screen("main")
    object AddRecipe : Screen("add_recipe")
    object Search : Screen("search")

    object Detail : Screen("detail/{recipeId}") {
        fun createRoute(recipeId: String) = "detail/$recipeId"
    }

    object EditRecipe : Screen("edit_recipe/{recipeId}") {
        fun createRoute(recipeId: String) = "edit_recipe/$recipeId"
    }
}

@Composable
fun CookbookNavGraph(
    viewModel: CookbookViewModel = viewModel(),
    context: Context
) {
    val navController = rememberNavController()

    // Beobachtet die Rezept-Liste aus der Datenbank in Echtzeit
    val recipeList by viewModel.recipes.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        // 1. Der Haupt-Feed
        composable(Screen.Main.route) {
            CookbookMainScreen(
                recipes = recipeList,
                onNavigateToAdd = { navController.navigate(Screen.AddRecipe.route) },
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onRecipeClick = { clickedRecipe ->
                    navController.navigate(Screen.Detail.createRoute(clickedRecipe.id))
                }
            )
        }

        // 2. Rezept hinzufügen Screen
        composable(Screen.AddRecipe.route) {
            AddOrEditRecipeScreen(
                onNavigateBack = { navController.popBackStack() },
                onSaveRecipe = { newRecipe ->
                    // Speichert das Rezept direkt in der Room-Datenbank!
                    viewModel.addRecipe(newRecipe)
                }
            )
        }

        // 3. Rezept suchen Screen
        composable(Screen.Search.route) {
            RecipeSearchScreen(
                recipes = recipeList,
                onNavigateBack = { navController.popBackStack() },
                onRecipeClick = { clickedRecipe ->
                    navController.navigate(Screen.Detail.createRoute(clickedRecipe.id))
                }
            )
        }

        // 4. Rezept-Detailansicht
        composable(Screen.Detail.route) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId")
            // Sucht das passende Rezept anhand der ID aus unserer Liste
            val recipe = recipeList.find { it.id == recipeId }

            var id = ""
            if (recipe != null) {
                id = recipe.id
            }

            if (recipe != null) {
                RecipeDetailScreen(
                    recipe = recipe,
                    onNavigateBack = { navController.popBackStack() },
                    onEditClick = { navController.navigate(Screen.EditRecipe.createRoute(id)) },
                    onDeleteClick = { recipeToDelete: Recipe ->
                        // Ruft die Methode im ViewModel auf -> löscht es in Room
                        viewModel.deleteRecipe(recipeToDelete)
                    }
                )
            }
        }

        // 5. Rezept ändern oder anlegen-Screen
        composable(Screen.EditRecipe.route) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId")
            val recipeToEdit = recipeList.find { it.id == recipeId }

            AddOrEditRecipeScreen(
                existingRecipe = recipeToEdit,
                onNavigateBack = { navController.popBackStack() },
                onSaveRecipe = { updatedRecipe ->
                    viewModel.updateRecipe(updatedRecipe)
                }
            )
        }
    }
}