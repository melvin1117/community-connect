package com.su.communityconnect.ui.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.su.communityconnect.CATEGORY_SCREEN
import com.su.communityconnect.EVENT_SCREEN
import com.su.communityconnect.FORGOT_PASSWORD_SCREEN
import com.su.communityconnect.HOME_SCREEN
import com.su.communityconnect.SIGN_IN_SCREEN
import com.su.communityconnect.SIGN_UP_SCREEN
import com.su.communityconnect.SPLASH_SCREEN
import com.su.communityconnect.FAVOURITE_SCREEN
import com.su.communityconnect.SEARCH_EVENT_SCREEN
import com.su.communityconnect.CREATE_EVENT_SCREEN
import com.su.communityconnect.USER_PROFILE_SCREEN
import com.su.communityconnect.model.User
import com.su.communityconnect.model.service.AccountService
import com.su.communityconnect.model.service.UserService
import com.su.communityconnect.ui.screens.SplashScreen
import com.su.communityconnect.ui.screens.authentication.forgotpassword.ForgotPasswordScreen
import com.su.communityconnect.ui.screens.authentication.signin.SignInScreen
import com.su.communityconnect.ui.screens.authentication.signup.SignUpScreen
import com.su.communityconnect.ui.screens.category.CategoryScreen
import com.su.communityconnect.ui.screens.event.EventFormScreen
import com.su.communityconnect.ui.screens.home.HomeScreen
import com.su.communityconnect.ui.screens.userprofile.UserProfileScreen
import com.su.communityconnect.model.state.UserState

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    accountService: AccountService,
    userService: UserService,
) {
    // Global user state initialization
    val context = LocalContext.current
    val userState = UserState.userState.collectAsState().value

    LaunchedEffect(Unit) {
        if (accountService.hasUser()) {
            try {
                val user = userService.getUser(accountService.currentUserId)
                if (user != null) {
                    UserState.updateUser(user)
                } else {
                    // If no user exists in the database, initialize one
                    val authUser = accountService.getUserProfile()
                    val newUser = authUser.copy(
                        id = accountService.currentUserId,
                        email = authUser.email
                    )
                    userService.saveUser(newUser)
                    UserState.updateUser(newUser)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load user data: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Determine the start destination dynamically
    val startDestination = when {
        !accountService.hasUser() -> SPLASH_SCREEN
        userState == null -> SPLASH_SCREEN // Wait for UserState to load
        userState.displayName.isEmpty() -> USER_PROFILE_SCREEN
        userState.preferredCategories.isEmpty() -> CATEGORY_SCREEN
        else -> HOME_SCREEN
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(SPLASH_SCREEN) {
            SplashScreen(onNavigateToSignIn = { navController.navigate(SIGN_IN_SCREEN) })
        }

        composable(SIGN_IN_SCREEN) {
            SignInScreen(
                onNavigateToSignUp = { navController.navigate(SIGN_UP_SCREEN) },
                onSignInSuccess = {
                    redirectAfterLogin(navController)
                },
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
            SignUpScreen(
                onNavigateToSignIn = { navController.navigate(SIGN_IN_SCREEN) }
            )
        }

        composable(HOME_SCREEN) {
            HomeScreen(
                selectedCategories = userState?.preferredCategories ?: emptyList(),
                onLogout = {
                    navController.navigate(SIGN_IN_SCREEN) {
                        popUpTo(HOME_SCREEN) { inclusive = true }
                    }
                },
                onNavigateToOtherScreen = { screen: String ->
                    when (screen) {
                        "HOME_SCREEN" -> navController.navigate(HOME_SCREEN)
                        "FAVORITES_SCREEN" -> navController.navigate(FAVOURITE_SCREEN)
                        "SEARCH_SCREEN" -> navController.navigate(SEARCH_EVENT_SCREEN)
                        "ADD_SCREEN" -> navController.navigate(CREATE_EVENT_SCREEN)
                    }
                }
            )
        }

        // Event Form Screen
        composable(EVENT_SCREEN) {
            EventFormScreen(
                onBackClick = { navController.popBackStack() },
                onEventSaved = { navController.navigate(HOME_SCREEN) }
            )
        }

        // User Profile Screen
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
            // FavoritesScreen placeholder
        }

        composable(SEARCH_EVENT_SCREEN) {
            // SearchScreen placeholder
        }

        composable(CREATE_EVENT_SCREEN) {
            // AddScreen placeholder
        }
    }
}

// Function to redirect after successful login
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
