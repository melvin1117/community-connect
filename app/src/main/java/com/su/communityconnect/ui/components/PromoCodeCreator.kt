package com.su.communityconnect.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.su.communityconnect.model.PromoCode
import com.su.communityconnect.R

@Composable
fun PromoCodeCreator(
    label: String,
    promoCodes: List<PromoCode>,
    onAddPromoCode: () -> Unit,
    onRemovePromoCode: (Int) -> Unit,
    onUpdatePromoCode: (Int, PromoCode) -> Unit,
    onValidationError: (String) -> Unit,
    onSurface: Boolean = true,
) {
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Label with Add Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (onSurface) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )
            IconButton(onClick = {
                if (promoCodes.any { it.code.isBlank() }) {
                    onValidationError(context.getString(R.string.error_complete_existing_promo_code))
                } else {
                    onAddPromoCode()
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_promo_code)
                )
            }
        }

        // Promo Code Items
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            promoCodes.forEachIndexed { index, promoCode ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Promo Code Field
                    TextField(
                        value = promoCode.code,
                        onValueChange = { newCode ->
                            val upperCaseCode = newCode.uppercase()
                            if (promoCodes.any { it.code == upperCaseCode && promoCodes.indexOf(it) != index }) {
                                onValidationError(context.getString(R.string.error_promo_code_unique))
                            } else {
                                onUpdatePromoCode(
                                    index,
                                    promoCode.copy(code = upperCaseCode)
                                )
                            }
                        },
                        label = stringResource(R.string.label_code),
                        placeholder = stringResource(R.string.placeholder_code),
                        modifier = Modifier.weight(1f)
                    )

                    var rawInputDiscount by remember { mutableStateOf(promoCode.discount.toString()) }

                    TextField(
                        value = rawInputDiscount,
                        onValueChange = { newInput ->
                            if (newInput.isEmpty()) {
                                rawInputDiscount = ""
                                onUpdatePromoCode(index, promoCode.copy(discount = 0.0))
                            } else {
                                val parsedDiscount = newInput.toDoubleOrNull()
                                if (parsedDiscount == null || parsedDiscount < 0.0 || parsedDiscount > 100.0) {
                                    onValidationError(context.getString(R.string.error_invalid_discount))
                                } else {
                                    rawInputDiscount = newInput
                                    onUpdatePromoCode(index, promoCode.copy(discount = parsedDiscount))
                                }
                            }
                        },
                        label = stringResource(R.string.label_discount),
                        placeholder = stringResource(R.string.placeholder_discount),
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )

                    // Remove Button
                    IconButton(
                        onClick = { onRemovePromoCode(index) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.remove_promo_code)
                        )
                    }
                }
            }
        }
    }
}
