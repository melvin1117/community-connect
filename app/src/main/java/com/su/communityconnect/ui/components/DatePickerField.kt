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
import network.chaintech.kmp_date_time_picker.utils.MIN
import network.chaintech.kmp_date_time_picker.utils.now
import java.time.format.DateTimeFormatter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import network.chaintech.kmp_date_time_picker.ui.datepicker.WheelDatePickerView
import network.chaintech.kmp_date_time_picker.utils.DateTimePickerView

@Composable
fun DatePickerField(
    modifier: Modifier = Modifier,
    label: String,
    placeholder: String = "Click to select",
    startDate: LocalDate = LocalDate.now(),
    prevSelectedDate: LocalDate? = null,
    minDate: LocalDate = LocalDate.MIN(),
    maxDate: LocalDate = LocalDate.now(),
    onDateSelected: (LocalDate) -> Unit,
    onSurface: Boolean = true,
) {
    var showPicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(prevSelectedDate) }

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
            value = selectedDate?.let {
                val javaDateTime = it.toJavaLocalDate()
                javaDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            } ?: "",
            onValueChange = {},
            placeholder = { Text(placeholder) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Open Date Picker",
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
            WheelDatePickerView(
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
                startDate = startDate,
                minDate = minDate,
                maxDate = maxDate,
                dateTimePickerView = DateTimePickerView.BOTTOM_SHEET_VIEW,
                dateTextStyle = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                dateTextColor = MaterialTheme.colorScheme.primary,
                onDoneClick = { snappedDate ->
                    selectedDate = snappedDate
                    onDateSelected(snappedDate)
                    showPicker = false
                },
                onDismiss = { showPicker = false }
            )
        }
    }
}
