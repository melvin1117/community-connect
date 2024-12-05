package com.su.communityconnect.ui.screens.categories

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.su.communityconnect.R
import com.su.communityconnect.ui.components.CategoryCard
import com.su.communityconnect.ui.components.PrimaryButton
import com.su.communityconnect.ui.components.SearchBar

@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel = viewModel(),
    onDoneClick: (List<String>) -> Unit // Passing selected categories to HomeScreen
) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    val selectedCategories by viewModel.selectedCategories.collectAsState()

    // Filter categories based on the search text
    val filteredCategories = remember(searchText.text) {
        if (searchText.text.isEmpty()) {
            categories
        } else {
            categories.filter { category ->
                category.title.contains(searchText.text, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Search Bar
        SearchBar(
            searchText = searchText,
            onTextChange = { searchText = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
            text = "Select up to 4 categories:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.background,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Categories Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredCategories.size) { index ->
                val category = filteredCategories[index]
                CategoryCard(
                    title = category.title,
                    imageRes = category.imageRes,
                    isSelected = selectedCategories.contains(category.title),
                    onCategoryClick = {
                        viewModel.toggleCategorySelection(category.title)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Done Button
        PrimaryButton(
            text ="Done",
            onClick = {
                onDoneClick(selectedCategories.toList())
            }
        )

    }
}

// Sample Categories
data class Category(val title: String, val imageRes: Int)

val categories = listOf(
    Category("Concert", R.drawable.concert),
    Category("Comedy", R.drawable.comedy),
    Category("Theater", R.drawable.theater),
    Category("Music", R.drawable.music),
    Category("Art", R.drawable.art),
    Category("Reading", R.drawable.reading),
    Category("Food", R.drawable.food),
    Category("Travel", R.drawable.travel)
)
