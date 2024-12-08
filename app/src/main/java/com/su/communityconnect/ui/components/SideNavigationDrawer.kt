package com.su.communityconnect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.su.communityconnect.CATEGORY_SCREEN
import com.su.communityconnect.LOGOUT
import com.su.communityconnect.MY_EVENTS_SCREEN
import com.su.communityconnect.MY_TICKETS_SCREEN
import com.su.communityconnect.R
import com.su.communityconnect.USER_PROFILE_SCREEN
import com.su.communityconnect.model.state.UserState
import com.su.communityconnect.ui.components.ProfilePicture

data class NavItem(
    val key: String,
    val titleResId: Int,
    val icon: ImageVector,
)

@Composable
fun SideNavigationDrawer(
    itemClicked: (String) -> Unit
) {
    val userState = UserState.userState.collectAsState().value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
            .clickable(enabled = false, onClick = {})
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
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
                    text = userState?.displayName ?: stringResource(R.string.default_user_name),
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
                NavItem(USER_PROFILE_SCREEN, R.string.nav_profile, Icons.Outlined.Person),
                NavItem(CATEGORY_SCREEN, R.string.nav_category_preference, Icons.Outlined.Category),
                NavItem(MY_TICKETS_SCREEN, R.string.nav_my_tickets, Icons.Outlined.LocalActivity),
                NavItem(MY_EVENTS_SCREEN, R.string.nav_my_events, Icons.Outlined.EventAvailable)
            )

            navItems.forEach { (key, titleResId, icon) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .clickable {
                            itemClicked(key)
                        }
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Icon(
                        imageVector = icon,
                        contentDescription = stringResource(titleResId),
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = stringResource(titleResId),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Normal
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Logout Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { itemClicked(LOGOUT) }
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Icon(
                    imageVector = Icons.Outlined.Logout,
                    contentDescription = stringResource(R.string.nav_logout),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.nav_logout),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
