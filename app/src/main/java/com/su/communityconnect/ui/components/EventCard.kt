package com.su.communityconnect.ui.components
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import com.su.communityconnect.R

@Composable
fun EventCard(
    imageRes: Int,
    badgeText: String,
    title: String,
    date: String,
    time: String,
    location: String,
    status: String,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 4.dp,
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(8.dp)
    ) {
        Box {
            // Event Image
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // White rectangle overlay at the bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(
                            bottomStart = 12.dp,
                            bottomEnd = 12.dp
                        )
                    )
            )
            // Badge
            Text(
                text = badgeText,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.background,
                    fontWeight = FontWeight.Light
                ),
                modifier = Modifier
                    .padding(8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .align(Alignment.TopStart)
            )

            // Heart Icon (Favorite)
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isFavorite) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
                    ),
                    contentDescription = "Favorite Icon",
                    tint = Color.White
                )
            }

            // Event Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .background(MaterialTheme.colorScheme.background)

            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.surface
                    )
                )
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "$time, $location",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    )


                    Text(
                        text = status,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontWeight = FontWeight.Light
                        )
                    )
                }
            }
        }
    }
}

