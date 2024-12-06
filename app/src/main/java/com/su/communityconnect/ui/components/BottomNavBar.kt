package com.su.communityconnect.ui.components
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavBar(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit
) {
    val items = listOf(
        BottomNavItem("Home", Icons.Filled.Home),
        BottomNavItem("Search", Icons.Filled.Search),
        BottomNavItem("Favorites", Icons.Outlined.Favorite),
        BottomNavItem("Add", Icons.Outlined.Add)
    )
    Box(

        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onBackground) // Background of the nav bar

    ) {
        HorizontalDivider(
            color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.7f)
        )
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSecondary
        )
        {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = selectedItem == index,
                    onClick = { onItemSelected(index) },
                    icon = {
                        Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
                            Icon(
                                imageVector = item.icon,
                                modifier = Modifier.size(30.dp),
                                contentDescription = item.label,
                                tint = if (selectedItem == index) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.background
                            )
                            if (selectedItem == index) {
                                // Add the line below the selected icon
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.2f) // Adjust line width
                                        .height(2.dp)
                                        .background(MaterialTheme.colorScheme.secondary)
                                        .align(androidx.compose.ui.Alignment.BottomCenter)
                                )
                            }
                        }
                    },
                    label = null,
                    alwaysShowLabel = false
                )
            }
        }
    }
}

data class BottomNavItem(
    val label: String,
    val icon: ImageVector
)
