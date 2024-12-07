package com.su.communityconnect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.LocalActivity
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.su.communityconnect.R
import com.su.communityconnect.model.state.UserState
import com.su.communityconnect.ui.components.ProfilePicture

data class NavItem(
    val key: String, // Unique key for the navigation item
    val title: String, // Display title
    val icon: ImageVector // Associated icon
)

@Composable
fun SideNavigationDrawer(
    itemClicked: (String) -> Unit
) {
    val userState = UserState.userState.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp)
    ) {
        // User Profile Section
        Row(
            modifier = Modifier.padding(bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfilePicture(
                imageUrl = userState?.profilePictureUrl,
                displayName = userState?.displayName,
                onImageSelected = {},
                size = 64,
                profileClicked = {}
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = userState?.displayName ?: "User",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.background.copy(alpha = 0.3f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        val navItems = listOf(
            NavItem("USER_PROFILE_SCREEN", "Profile", Icons.Outlined.Person),
            NavItem("CATEGORY_SCREEN", "Category Preference", Icons.Outlined.Category),
            NavItem("MY_TICKETS_SCREEN", "My Tickets", Icons.Outlined.LocalActivity),
            NavItem("MY_EVENT_SCREEN", "My Events", Icons.Outlined.EventAvailable)
            // Add more items as needed
        )


        navItems.forEach { (key, label, icon) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .clickable {
                        itemClicked(key)
                    }
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Logout Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { itemClicked("LOGOUT") }
        ) {
            Icon(
                imageVector = Icons.Outlined.Logout,
                contentDescription = "Logout",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Logout",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}
