package com.su.communityconnect.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.su.communityconnect.R

@Composable
fun SearchBar(
    searchText: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
    onSurface: Boolean = true,
) {
    OutlinedTextField(
        value = searchText,
        onValueChange = onTextChange,
        singleLine = true,
        placeholder = { Text("Search Category") },
        isError = false,
        shape = RoundedCornerShape(50.dp),
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search Icon",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = if (onSurface) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = if (onSurface) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onBackground,
            errorTextColor = MaterialTheme.colorScheme.error
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        modifier = Modifier.fillMaxWidth()
    )
}
