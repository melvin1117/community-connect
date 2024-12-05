package com.su.communityconnect.ui.navigation
import androidx.compose.runtime.Composable
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
import com.su.communityconnect.model.service.AccountService
import com.su.communityconnect.ui.screens.authentication.signin.SignInScreen
import com.su.communityconnect.ui.screens.authentication.signup.SignUpScreen
import com.su.communityconnect.ui.screens.SplashScreen
import com.su.communityconnect.ui.screens.authentication.forgotpassword.ForgotPasswordScreen
import com.su.communityconnect.ui.screens.category.CategoryScreen
import com.su.communityconnect.ui.screens.event.EventFormScreen
import com.su.communityconnect.ui.screens.home.HomeScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    accountService: AccountService,
) {
    val startDestination = if (accountService.hasUser()) HOME_SCREEN else SPLASH_SCREEN

    NavHost(navController = navController, startDestination = startDestination) {
        // Splash Screen
        composable(SPLASH_SCREEN) {
            SplashScreen(onNavigateToSignIn = { navController.navigate(SIGN_IN_SCREEN) })
        }

        // Sign-In Screen
        composable(SIGN_IN_SCREEN) {
            SignInScreen(
                onNavigateToSignUp = { navController.navigate(SIGN_UP_SCREEN) },
                onSignInSuccess = { navController.navigate(EVENT_SCREEN) },
                onForgotPassword = { navController.navigate(FORGOT_PASSWORD_SCREEN) }
            )
        }

        // Forgot Password Screen
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

        // Sign-Up Screen
        composable(SIGN_UP_SCREEN) {
            SignUpScreen(
                onNavigateToSignIn = { navController.navigate(SIGN_IN_SCREEN) }
            )
        }

        // Home Screen
        composable(HOME_SCREEN) { backStackEntry ->
            // Retrieve selected categories from the arguments
            val selectedCategories = backStackEntry.arguments?.getString("selectedCategories")?.split(",") ?: emptyList()
            HomeScreen(
                selectedCategories = selectedCategories,
                onLogout = {
                    navController.navigate(SIGN_IN_SCREEN) {
                        popUpTo(HOME_SCREEN) { inclusive = true }
                    }
                },
                onNavigateToOtherScreen = { screen: String ->
                    when (screen) {
                        "HOME_SCREEN" -> navController.navigate(HOME_SCREEN) // Navigate to Category Screen
                        "FAVORITES_SCREEN" -> navController.navigate(FAVOURITE_SCREEN) // Navigate to Favorites Screen
                        "SEARCH_SCREEN" -> navController.navigate(SEARCH_EVENT_SCREEN) // Navigate to Search Screen
                        "ADD_SCREEN" -> navController.navigate(CREATE_EVENT_SCREEN) // Navigate to Add Screen
                    }
                }
            )
        }


        // Home Screen
        composable(EVENT_SCREEN) {
            EventFormScreen(
                onBackClick = { navController.popBackStack() },
                onEventSaved = {
                    navController.navigate(HOME_SCREEN)
                }
            )
        }

        // Category Screen
        composable(CATEGORY_SCREEN) {
            CategoryScreen(
                onDoneClick = { selectedCategories ->
                    // Pass selected categories to the HomeScreen
                    navController.navigate("$HOME_SCREEN/${selectedCategories.joinToString(",")}") {
                        popUpTo(CATEGORY_SCREEN) { inclusive = true } // Remove CategoryScreen from the backstack
                    }
                }
            )
        }
        composable(FAVOURITE_SCREEN) {
                   /* FavoritesScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )*/
                }
        composable(SEARCH_EVENT_SCREEN) {
                   /* SearchScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )*/
                }
        composable(CREATE_EVENT_SCREEN) {
                    /*AddScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )*/
                }

        }
}
