package com.su.communityconnect.ui.screens.eventdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.su.communityconnect.ui.components.BackButton
import com.su.communityconnect.ui.components.ImageCarousel
import com.su.communityconnect.ui.components.OrganizerDetails
import com.su.communityconnect.ui.components.PrimaryButton
import com.su.communityconnect.utils.openDialer
import com.su.communityconnect.utils.openEmailClient
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalTime
import java.time.format.DateTimeFormatter
import com.su.communityconnect.R

@Composable
fun EventDetailScreen(
    eventId: String,
    onBackClick: () -> Unit,
    onMapClick: () -> Unit,
    onAttendClick: (String) -> Unit,
    viewModel: EventDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val eventState by viewModel.eventState.collectAsState()
    val organizerState by viewModel.organizerState.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    var isDescriptionExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    when (val event = eventState) {
        is EventDetailState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is EventDetailState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(R.string.error_event_details), color = MaterialTheme.colorScheme.error)
            }
        }
        is EventDetailState.Success -> {
            val eventData = event.event

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Image Carousel
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) {
                        if (eventData.images.isNotEmpty()) {
                            ImageCarousel(images = eventData.images)
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.2f))
                            )
                        }

                        // Back Button
                        BackButton(
                            onBackClick = { onBackClick() },
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(16.dp)
                        )

                        // Share and Wishlist Buttons
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.75f))
                                    .clickable {
                                        viewModel.shareEvent(
                                            eventData.title,
                                            eventData.description,
                                            context
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = stringResource(R.string.icon_share),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.75f))
                                    .clickable { viewModel.toggleFavorite(eventData.id) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = stringResource(R.string.icon_favorite),
                                    tint = if (isFavorite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Event Details Card Overlapping the Carousel
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .offset(y = (-50).dp)
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onBackground,
                        ),
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = eventData.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = eventData.location.fullAddress,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${eventData.eventTimestamp.date.toJavaLocalDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}, ${
                                        eventData.eventTimestamp.time.toJavaLocalTime().format(
                                            DateTimeFormatter.ofPattern("hh:mm a")
                                        )
                                    }",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterVertically),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                IconButton(
                                    onClick = onMapClick
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Map,
                                        contentDescription = stringResource(R.string.icon_map),
                                        modifier = Modifier.size(64.dp),
                                        tint = MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Organizer Details Label
                    Text(
                        text = stringResource(R.string.label_event_by),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp)) // Space between label and organizer details

                    // Organizer Details
                    when (val organizer = organizerState) {
                        is OrganizerState.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        }
                        is OrganizerState.Success -> {
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                OrganizerDetails(
                                    organizer.user,
                                    eventData.eventCreationTimestamp,
                                    onPhoneClick = { phone -> openDialer(phone, context) },
                                    onEmailClick = { email -> openEmailClient(email, context) },
                                )
                            }
                        }
                        is OrganizerState.Error -> {
                            Text(
                                text = stringResource(id = R.string.failed_org_detail),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description Label
                    Text(
                        text = stringResource(R.string.label_description),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Description
                    Text(
                        text = eventData.description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 4,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    if (!isDescriptionExpanded && eventData.description.length > 100) {
                        TextButton(onClick = { isDescriptionExpanded = true }, modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text(text = stringResource(id = R.string.read_more))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Paid or Free
                    Text(
                        text = if (eventData.price > 0) stringResource(R.string.paid_event) else stringResource(R.string.free_event),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Attend Button Aligned to the Very End
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        PrimaryButton(
                            horizontalPadding = 20.dp,
                            text = stringResource(id = R.string.attend_button),
                            onClick = { onAttendClick(eventData.id) }
                        )
                    }
                }
            }
        }
    }
}
