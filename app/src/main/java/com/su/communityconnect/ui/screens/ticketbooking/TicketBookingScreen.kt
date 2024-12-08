package com.su.communityconnect.ui.screens.ticketbooking

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.su.communityconnect.model.Event
import com.su.communityconnect.ui.components.BackButton
import com.su.communityconnect.ui.components.PrimaryButton
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toJavaLocalDate
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.*
import com.su.communityconnect.ui.components.TextField

@Composable
fun TicketBookingScreen(
    eventId: String,
    userId: String,
    onBackClick: (String) -> Unit,
    onPaymentSuccess: (String) -> Unit,
    viewModel: TicketBookingViewModel = hiltViewModel()
) {
    val eventState by viewModel.eventState.collectAsState()
    var ticketCount by remember { mutableStateOf(1) }
    var promoCode by remember { mutableStateOf("") }
    var discountApplied by remember { mutableStateOf(0.0) }
    var promoCodeApplied by remember { mutableStateOf(false) }
    val promoCodeError by viewModel.promoCodeError.collectAsState()
    val createdTicketId by viewModel.createdTicketId.collectAsState()
    val isPayButtonEnabled by viewModel.payButtonEnabled.collectAsState()
    val payButtonError by viewModel.payButtonError.collectAsState()
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())

    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId, userId)
    }

    if (createdTicketId != null) {
        LaunchedEffect(Unit) {
            onPaymentSuccess(createdTicketId!!)
        }
    }

    when (val state = eventState) {
        is TicketBookingState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is TicketBookingState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.message, color = MaterialTheme.colorScheme.error)
            }
        }
        is TicketBookingState.Success -> {
            val event = state.event
            val maxTickets = minOf(event.perUserTicketLimit, event.maxTickets - (event.ticketsBooked ?: 0))
            val convenienceFee = (event.price * ticketCount * 0.5).coerceAtMost(5.0)
            val totalPrice = (event.price * ticketCount) - discountApplied + convenienceFee

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(250.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box {
                        val painter = rememberAsyncImagePainter(event.images.firstOrNull())
                        Image(
                            painter = painter,
                            contentDescription = event.title,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${event.location.displayName} | ${
                                    event.eventTimestamp.date.toJavaLocalDate().format(
                                        DateTimeFormatter.ofPattern("MMM dd, yyyy")
                                    )
                                }",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        BackButton(
                            onBackClick = { onBackClick(eventId) },
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Ticket Selector
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tickets", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = {
                            if (ticketCount > 1) {
                                ticketCount -= 1
                                discountApplied = viewModel.recalculateDiscount(event, ticketCount)
                            }
                        }) {
                            Icon(Icons.Filled.Remove, contentDescription = null)
                        }
                        Text(
                            ticketCount.toString(),
                            modifier = Modifier.padding(horizontal = 8.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        IconButton(onClick = {
                            if (ticketCount < maxTickets) {
                                ticketCount += 1
                                discountApplied = viewModel.recalculateDiscount(event, ticketCount)
                            }
                        }) {
                            Icon(Icons.Filled.Add, contentDescription = null)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Promo Code Applier
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Promo Code", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = promoCode,
                                onValueChange = {
                                    promoCode = it.uppercase()
                                    promoCodeApplied = false
                                },
                                placeholder = { Text("Enter your code") },
                                shape = RoundedCornerShape(50.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    errorTextColor = MaterialTheme.colorScheme.error
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                modifier = Modifier.weight(1f),
                            )
                            Spacer(Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = {
                                    discountApplied = viewModel.applyPromoCode(promoCode, event, ticketCount)
                                    promoCodeApplied = discountApplied > 0
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.background
                                ),
                                modifier = Modifier.align(Alignment.CenterVertically)
                            ) {
                                Text("Apply")
                            }
                        }
                        Spacer(Modifier.height(8.dp))

                        // Show error or success message
                        when {
                            promoCodeError != null -> {
                                Text(
                                    text = promoCodeError!!,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            promoCodeApplied -> {
                                Text(
                                    text = "Promo code applied successfully!",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Price Summary
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Price Summary", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tickets (${ticketCount}):")
                            Text(currencyFormat.format(event.price * ticketCount))
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Discount:")
                            Text("-${currencyFormat.format(discountApplied)}")
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Convenience Fee:")
                            Text(currencyFormat.format(convenienceFee))
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total:", fontWeight = FontWeight.Bold)
                            Text(
                                currencyFormat.format(totalPrice),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pay Button
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    payButtonError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    PrimaryButton(
                        text = "Pay",
                        onClick = {
                            if (ticketCount <= maxTickets) {
                                viewModel.bookTickets(
                                    eventId = event.id,
                                    userId = userId,
                                    ticketCount = ticketCount,
                                    promoCode = promoCode,
                                    discount = discountApplied,
                                    totalPrice = totalPrice
                                )
                            }
                        },
                        enabled = isPayButtonEnabled,
                    )
                }
            }
        }
    }
}
