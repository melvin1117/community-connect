package com.su.communityconnect.ui.components
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.su.communityconnect.ui.theme.DarkOnSurfaceSecondaryText
import com.su.communityconnect.ui.theme.NavBorderColor

@Composable
fun BottomNavBar(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit
) {
    val items = listOf(
        BottomNavItem("Home", Icons.Outlined.Home),
        BottomNavItem("Search", Icons.Outlined.Search),
        BottomNavItem("Favorites", Icons.Outlined.FavoriteBorder),
        BottomNavItem("Add", Icons.Outlined.AddCircleOutline)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Top border (divider)
        HorizontalDivider(
            color = NavBorderColor,
            thickness = 0.7.dp
        )

        // Bottom navigation bar
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ) {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = selectedItem == index,
                    onClick = { onItemSelected(index) },
                    label = { Text(item.label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp) },
                    icon = { Icon(imageVector = item.icon, contentDescription = null) },
                    colors = NavigationBarItemColors (
                        selectedIconColor = MaterialTheme.colorScheme.secondary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                        selectedTextColor = MaterialTheme.colorScheme.secondary,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledIconColor = DarkOnSurfaceSecondaryText,
                        disabledTextColor = DarkOnSurfaceSecondaryText,
                        selectedIndicatorColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                    )
                )
            }
        }
    }
}

data class BottomNavItem(
    val label: String,
    val icon: ImageVector
)
