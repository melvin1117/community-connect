package com.su.communityconnect.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.su.communityconnect.ui.screens.SignInScreen
import com.su.communityconnect.ui.screens.SignUpScreen
import com.su.communityconnect.ui.screens.SplashScreen

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "splash") {
        // Splash Screen
        composable("splash") {
            SplashScreen(onNavigateToSignIn = { navController.navigate("signin") })
        }
        // Sign-In Screen
        composable("signin") {
            SignInScreen(
                onNavigateToSignUp = { navController.navigate("signup") },
                onSignInSuccess = { /* Handle post-sign-in navigation */ }
            )
        }
        // Sign-Up Screen
        composable("signup") {
            SignUpScreen(
                onNavigateToSignIn = { navController.navigate("signin") }
            )
        }
    }
}
