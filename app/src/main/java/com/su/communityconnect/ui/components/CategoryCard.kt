package com.su.communityconnect.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun CategoryCard(
    title: String,
    imageUrl: String,
    isSelected: Boolean,
    onCategoryClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp,
        modifier = Modifier
            .width(120.dp)
            .padding(8.dp)
            .clickable { onCategoryClick() }
    ) {
        Column(
            modifier = Modifier
                .background(color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(8.dp)

            )
        }
    }
}
