package com.su.communityconnect.ui.navigation

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.su.communityconnect.*
import com.su.communityconnect.model.service.AccountService
import com.su.communityconnect.model.service.UserService
import com.su.communityconnect.ui.screens.SplashScreen
import com.su.communityconnect.ui.screens.authentication.forgotpassword.ForgotPasswordScreen
import com.su.communityconnect.ui.screens.authentication.signin.SignInScreen
import com.su.communityconnect.ui.screens.authentication.signup.SignUpScreen
import com.su.communityconnect.ui.screens.category.CategoryScreen
import com.su.communityconnect.ui.screens.eventform.EventFormScreen
import com.su.communityconnect.ui.screens.home.HomeScreen
import com.su.communityconnect.ui.screens.userprofile.UserProfileScreen
import com.su.communityconnect.model.state.UserState
import com.su.communityconnect.ui.components.BottomNavBar
import com.su.communityconnect.ui.screens.LocationSelectionScreen
import com.su.communityconnect.ui.screens.MapScreen
import com.su.communityconnect.ui.screens.eventdetail.EventDetailScreen
import com.su.communityconnect.ui.screens.mybookings.MyTicketsScreen
import com.su.communityconnect.ui.screens.myevents.MyEventsScreen
import com.su.communityconnect.ui.screens.ticket.TicketScreen
import com.su.communityconnect.ui.screens.ticketbooking.TicketBookingScreen
import com.su.communityconnect.ui.screens.wishlist.WishlistScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    accountService: AccountService,
    userService: UserService,
) {
    val context = LocalContext.current
    val userState = UserState.userState.collectAsState().value
    val locationPermissionGranted = remember { mutableStateOf(false) } // Shared state for permission

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            locationPermissionGranted.value = granted
            if (!granted) {
                Toast.makeText(
                    context,
                    context.getString(R.string.location_permission_required),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        if (accountService.hasUser()) {
            try {
                val user = userService.getUser(accountService.currentUserId)
                if (user != null) {
                    UserState.updateUser(user)
                } else {
                    val authUser = accountService.getUserProfile()
                    val newUser = authUser.copy(
                        id = accountService.currentUserId,
                        email = authUser.email
                    )
                    userService.saveUser(newUser)
                    UserState.updateUser(newUser)
                }
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    context.getString(R.string.failed_to_load_user_data, e.localizedMessage),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    val startDestination = when {
        !accountService.hasUser() -> SPLASH_SCREEN
        userState == null -> SPLASH_SCREEN
        userState.displayName.isEmpty() -> USER_PROFILE_SCREEN
        userState.preferredCategories.isEmpty() -> CATEGORY_SCREEN
        else -> HOME_SCREEN
    }

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val showBottomNav = listOf(
        HOME_SCREEN,
        LOCATION_SELECTION_SCREEN,
        EVENT_DETAIL_SCREEN,
        FAVOURITE_SCREEN,
        EVENT_FORM_SCREEN,
        EVENT_TICKET_BOOKING_SCREEN,
        EVENT_TICKET_SCREEN,
        MY_TICKETS_SCREEN,
        MY_EVENTS_SCREEN,
    ).any { currentRoute?.contains(it) == true }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            if (showBottomNav) {
                BottomNavBar(
                    selectedItem = when (currentRoute) {
                        HOME_SCREEN -> 0
                        EVENT_FORM_SCREEN -> 1
                        FAVOURITE_SCREEN -> 2
                        else -> 0
                    },
                    onItemSelected = { index ->
                        when (index) {
                            0 -> navController.navigate(HOME_SCREEN) { launchSingleTop = true }
                            1 -> navController.navigate("$EVENT_FORM_SCREEN/$NO_EVENT_ID") { launchSingleTop = true }
                            2 -> navController.navigate(FAVOURITE_SCREEN) { launchSingleTop = true }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(SPLASH_SCREEN) {
                SplashScreen(onNavigateToSignIn = { navController.navigate(SIGN_IN_SCREEN) })
            }

            composable(SIGN_IN_SCREEN) {
                SignInScreen(
                    onNavigateToSignUp = { navController.navigate(SIGN_UP_SCREEN) },
                    onSignInSuccess = { redirectAfterLogin(navController) },
                    onForgotPassword = { navController.navigate(FORGOT_PASSWORD_SCREEN) }
                )
            }

            composable(FORGOT_PASSWORD_SCREEN) {
                ForgotPasswordScreen(
                    onPasswordResetSuccess = {
                        navController.navigate(SIGN_IN_SCREEN) {
                            popUpTo(FORGOT_PASSWORD_SCREEN) { inclusive = true }
                        }
                    },
                    onBackSignIn = { navController.navigate(SIGN_IN_SCREEN) }
                )
            }

            composable(SIGN_UP_SCREEN) {
                SignUpScreen(onNavigateToSignIn = { navController.navigate(SIGN_IN_SCREEN) })
            }

            composable(HOME_SCREEN) {
                HomeScreen(
                    onRequestLocationPermission = {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    },
                    onLocationBoxClick = {
                        navController.navigate(LOCATION_SELECTION_SCREEN)
                    },
                    drawerItemClicked = { selectedPage ->
                        when (selectedPage) {
                            USER_PROFILE_SCREEN -> navController.navigate(USER_PROFILE_SCREEN)
                            CATEGORY_SCREEN -> navController.navigate(CATEGORY_SCREEN)
                            MY_TICKETS_SCREEN -> navController.navigate(MY_TICKETS_SCREEN)
                            MY_EVENTS_SCREEN -> navController.navigate(MY_EVENTS_SCREEN)
                            LOGOUT -> {
                                CoroutineScope(Dispatchers.Main).launch {
                                    try {
                                        accountService.signOut()
                                        UserState.clear()
                                        navController.navigate(SIGN_IN_SCREEN) {
                                            popUpTo(HOME_SCREEN) { inclusive = true }
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.error_during_logout, e.localizedMessage),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    },
                    onEventClick = { eventId -> navController.navigate("$EVENT_DETAIL_SCREEN/$eventId") },
                    isPermissionGranted = locationPermissionGranted.value
                )
            }

            composable(LOCATION_SELECTION_SCREEN) {
                LocationSelectionScreen(
                    onLocationSelected = { city ->
                        UserState.updateUser(UserState.userState.value!!.copy(preferredCity = city))
                        navController.navigate(HOME_SCREEN)
                    },
                    onRequestCurrentLocation = {
                        UserState.updateUser(UserState.userState.value!!.copy(preferredCity = ""))
                        navController.navigate(HOME_SCREEN)
                    }
                )
            }

            composable("$EVENT_FORM_SCREEN/{eventId}") { backStackEntry ->
                var eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
                if (eventId == NO_EVENT_ID) {
                    eventId = ""
                }
                EventFormScreen(
                    eventId = eventId,
                    onBackClick = { navController.popBackStack() },
                    onEventSaved = { navController.navigate(HOME_SCREEN) }
                )
            }

            composable(USER_PROFILE_SCREEN) {
                UserProfileScreen(
                    onBackClick = {
                        if (userState?.preferredCategories.isNullOrEmpty()) {
                            navController.navigate(CATEGORY_SCREEN) {
                                popUpTo(USER_PROFILE_SCREEN) { inclusive = true }
                            }
                        } else {
                            navController.navigate(HOME_SCREEN) {
                                popUpTo(USER_PROFILE_SCREEN) { inclusive = true }
                            }
                        }
                    },
                )
            }

            composable(CATEGORY_SCREEN) {
                CategoryScreen(
                    onBackClick = {
                        navController.navigate(HOME_SCREEN) {
                            popUpTo(CATEGORY_SCREEN) { inclusive = true }
                        }
                    },
                )
            }

            composable(FAVOURITE_SCREEN) {
                WishlistScreen(
                    onBackClick = { navController.navigate(HOME_SCREEN) },
                    onEventClick = { eventId -> navController.navigate("$EVENT_DETAIL_SCREEN/$eventId") }
                )
            }

            composable(MY_TICKETS_SCREEN) {
                MyTicketsScreen(
                    onBackClick = { navController.navigate(HOME_SCREEN) },
                    onTicketClick = { ticketId -> navController.navigate("$EVENT_TICKET_SCREEN/$ticketId") }
                )
            }

            composable(MY_EVENTS_SCREEN) {
                MyEventsScreen(
                    onBackClick = { navController.navigate(HOME_SCREEN) },
                    onEventClick = { eventId -> navController.navigate("$EVENT_DETAIL_SCREEN/$eventId") }
                )
            }

            composable("$EVENT_DETAIL_SCREEN/{eventId}") { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
                EventDetailScreen(
                    eventId = eventId,
                    onBackClick = { navController.navigate(HOME_SCREEN) },
                    onMapClick = { navController.navigate("$MAP_SCREEN/$eventId") },
                    onAttendClick = { eventId -> navController.navigate("$EVENT_TICKET_BOOKING_SCREEN/$eventId") },
                    onEditClick = { eventId -> navController.navigate("$EVENT_FORM_SCREEN/$eventId") },
                )
            }

            composable("$EVENT_TICKET_BOOKING_SCREEN/{eventId}") { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
                TicketBookingScreen(
                    eventId = eventId,
                    userId = accountService.currentUserId,
                    onBackClick = { eventId -> navController.navigate("$EVENT_DETAIL_SCREEN/$eventId") },
                    onPaymentSuccess = { ticketId -> navController.navigate("$EVENT_TICKET_SCREEN/$ticketId") },
                )

            }

            composable("$EVENT_TICKET_SCREEN/{ticketId}") { backStackEntry ->
                val ticketId = backStackEntry.arguments?.getString("ticketId") ?: return@composable
                TicketScreen(
                    ticketId = ticketId,
                    onBackClick = { navController.navigate(HOME_SCREEN) },
                    onMapClick = { eventId -> navController.navigate("$MAP_SCREEN/$eventId") },
                )
            }

            composable("$MAP_SCREEN/{eventId}") { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
                MapScreen(onBackClick = { navController.navigate("$EVENT_DETAIL_SCREEN/$eventId") })
            }
        }
    }
}

private fun redirectAfterLogin(navController: NavHostController) {
    val userState = UserState.userState.value
    when {
        userState?.displayName.isNullOrEmpty() -> {
            navController.navigate(USER_PROFILE_SCREEN) {
                popUpTo(SIGN_IN_SCREEN) { inclusive = true }
            }
        }
        userState?.preferredCategories.isNullOrEmpty() -> {
            navController.navigate(CATEGORY_SCREEN) {
                popUpTo(SIGN_IN_SCREEN) { inclusive = true }
            }
        }
        else -> {
            navController.navigate(HOME_SCREEN) {
                popUpTo(SIGN_IN_SCREEN) { inclusive = true }
            }
        }
    }
}
