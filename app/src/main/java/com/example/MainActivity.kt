package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.UserProfileManager
import com.example.ui.MainScreen
import com.example.ui.navigation.NavRoutes
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.screens.splash.SplashScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserProfileManager.init(this)
        com.example.data.datasource.RegisteredFacultyStore.init(this)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                GgcAppNavigation()
            }
        }
    }
}

@Composable
fun GgcAppNavigation() {
    val context = LocalContext.current
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.SPLASH
    ) {
        composable(NavRoutes.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    val destination = if (UserProfileManager.isOnboarded(context)) {
                        NavRoutes.MAIN
                    } else {
                        NavRoutes.ONBOARDING
                    }
                    navController.navigate(destination) {
                        popUpTo(NavRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.ONBOARDING) {
            OnboardingScreen(
                onOnboardingFinished = {
                    navController.navigate(NavRoutes.MAIN) {
                        popUpTo(NavRoutes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.MAIN) {
            MainScreen()
        }
    }
}
