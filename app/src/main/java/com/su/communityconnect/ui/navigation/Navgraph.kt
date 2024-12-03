package com.su.communityconnect.navigation
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.su.communityconnect.CATEGORY_SCREEN
import com.su.communityconnect.FORGOT_PASSWORD_SCREEN
import com.su.communityconnect.HOME_SCREEN
import com.su.communityconnect.SIGN_IN_SCREEN
import com.su.communityconnect.SIGN_UP_SCREEN
import com.su.communityconnect.SPLASH_SCREEN
import com.su.communityconnect.model.service.AccountService
import com.su.communityconnect.ui.screens.authentication.signin.SignInScreen
import com.su.communityconnect.ui.screens.authentication.signup.SignUpScreen
import com.su.communityconnect.ui.screens.SplashScreen
import com.su.communityconnect.ui.screens.authentication.forgotpassword.ForgotPasswordScreen
import com.su.communityconnect.ui.screens.categories.CategoryScreen
import com.su.communityconnect.ui.screens.categories.CategoryViewModel
import com.su.communityconnect.ui.screens.home.HomeScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    accountService: AccountService,
) {
    // Define the start destination based on user authentication
    val startDestination = if (accountService.hasUser()) HOME_SCREEN else SPLASH_SCREEN
    val categoryViewModel: CategoryViewModel = hiltViewModel() // Inject the ViewModel

    NavHost(navController = navController, startDestination = startDestination) {
        // Splash Screen
        composable(SPLASH_SCREEN) {
            SplashScreen(onNavigateToSignIn = { navController.navigate(SIGN_IN_SCREEN) })
        }

        // Sign-In Screen
        composable(SIGN_IN_SCREEN) {
            SignInScreen(
                onNavigateToSignUp = { navController.navigate(SIGN_UP_SCREEN) },
                onSignInSuccess = { navController.navigate(CATEGORY_SCREEN) },
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
                }
            )
        }

        // Category Screen
        composable(CATEGORY_SCREEN) {
            CategoryScreen(
                viewModel = categoryViewModel,
                onDoneClick = { selectedCategories ->
                    // Pass selected categories to the HomeScreen
                    navController.navigate("$HOME_SCREEN/${selectedCategories.joinToString(",")}") {
                        popUpTo(CATEGORY_SCREEN) { inclusive = true } // Remove CategoryScreen from the backstack
                    }
                }
            )
        }
    }
}
