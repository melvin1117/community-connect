package com.su.communityconnect.ui.screens.category

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.su.communityconnect.R
import com.su.communityconnect.ui.components.CategoryCard
import com.su.communityconnect.ui.components.PrimaryButton
import com.su.communityconnect.ui.components.SearchBar

@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel = hiltViewModel(),
    onDoneClick: (List<String>) -> Unit
) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    val selectedCategories by viewModel.selectedCategories.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val uiEvent by viewModel.uiEvent.collectAsState()
    val context = LocalContext.current

    // Observe UI events
    LaunchedEffect(uiEvent) {
        uiEvent?.let {
            when (it) {
                CategoryViewModel.CategoryUiEvent.MaxLimitReached -> {
                    Toast.makeText(
                        context,
                        "You can select up to 4 categories only.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            viewModel.onUiEventConsumed()
        }
    }

    // Filter categories based on the search text
    val filteredCategories = remember(searchText.text, categories) {
        if (searchText.text.isEmpty()) {
            categories
        } else {
            categories.filter { category ->
                category.name.contains(searchText.text, ignoreCase = true)
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
            onTextChange = { searchText = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Select up to 4 categories:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.weight(1f)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredCategories.size) { index ->
                    val category = filteredCategories[index]
                    CategoryCard(
                        title = category.name,
                        imageUrl = category.image,
                        isSelected = selectedCategories.contains(category.name),
                        onCategoryClick = {
                            viewModel.toggleCategorySelection(category.name)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Done Button
        PrimaryButton(
            text = "Done",
            horizontalPadding = 50.dp,
            onClick = {
                onDoneClick(selectedCategories.toList())
            }
        )
    }
}
