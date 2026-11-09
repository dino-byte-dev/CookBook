package com.example.cookbook.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.cookbook.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookbookTopBar(
    onNavigateToAdd: () -> Unit,
    onNavigateToSearch: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.chef_hat_24dp),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("CookBook")
            }
        },
        actions = {
            TextButton(
                onClick = { expanded = true },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Menü", style = MaterialTheme.typography.titleLarge)
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.more_vert_24dp),
                        contentDescription = "Menü öffnen"
                    )
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Neues Rezept hinzufügen", style = MaterialTheme.typography.titleLarge) },
                    onClick = {
                        expanded = false
                        onNavigateToAdd()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Rezept suchen", style = MaterialTheme.typography.titleLarge) },
                    onClick = {
                        expanded = false
                        onNavigateToSearch()
                    }
                )
            }
        }
    )
}