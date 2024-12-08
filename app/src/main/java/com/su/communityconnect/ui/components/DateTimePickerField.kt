package com.su.communityconnect.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import network.chaintech.kmp_date_time_picker.ui.datetimepicker.WheelDateTimePickerView
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import network.chaintech.kmp_date_time_picker.utils.MAX
import network.chaintech.kmp_date_time_picker.utils.MIN
import network.chaintech.kmp_date_time_picker.utils.TimeFormat
import network.chaintech.kmp_date_time_picker.utils.now
import java.time.format.DateTimeFormatter
import com.su.communityconnect.R

@Composable
fun DateTimePickerField(
    modifier: Modifier = Modifier,
    label: String,
    initialSelectedDateTime: LocalDateTime? = null,
    placeholder: String = stringResource(id = R.string.click_select),
    startDateTime: LocalDateTime = LocalDateTime.now(),
    minDateTime: LocalDateTime = LocalDateTime.MIN(),
    maxDateTime: LocalDateTime = LocalDateTime.MAX(),
    onDateTimeSelected: (LocalDateTime) -> Unit,
    onSurface: Boolean = true,
) {
    var showPicker by remember { mutableStateOf(false) }
    var selectedDateTime by remember(initialSelectedDateTime) { mutableStateOf<LocalDateTime?>(initialSelectedDateTime) }

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = if (onSurface) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        OutlinedTextField(
            value = selectedDateTime?.let {
                val javaDateTime = it.toJavaLocalDateTime()
                javaDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            } ?: "",
            onValueChange = {},
            placeholder = { Text(placeholder) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Open DateTime Picker",
                    tint = Color.Gray,
                    modifier = Modifier.clickable { showPicker = true }
                )
            },
            shape = RoundedCornerShape(50.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = if (onSurface) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = if (onSurface) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onBackground,
                errorTextColor = MaterialTheme.colorScheme.error
            ),
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showPicker = true }
        )

        if (showPicker) {
            WheelDateTimePickerView(
                modifier = modifier,
                height = 128.dp,
                showDatePicker = true,
                title = label,
                doneLabel = "Done",
                titleStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold
                ),
                doneLabelStyle = TextStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Normal
                ),
                startDate = startDateTime,
                minDate = minDateTime,
                maxDate = maxDateTime,
                timeFormat = TimeFormat.AM_PM,
                dateTextStyle = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                dateTextColor = MaterialTheme.colorScheme.primary,
                onDoneClick = { snappedDateTime ->
                    selectedDateTime = snappedDateTime
                    onDateTimeSelected(snappedDateTime)
                    showPicker = false
                },
                onDismiss = { showPicker = false }
            )
        }
    }
}
