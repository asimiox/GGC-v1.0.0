package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.UserProfileManager
import com.example.ui.MainScreen
import com.example.ui.navigation.NavRoutes
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.screens.splash.SplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.util.SystemNotificationHelper

class MainActivity : ComponentActivity() {

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        android.util.Log.d("MainActivity", "POST_NOTIFICATIONS granted: $isGranted")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserProfileManager.init(this)
        com.example.data.datasource.RegisteredFacultyStore.init(this)
        com.example.data.datasource.RegisteredStudentStore.init(this)

        // Initialize High-Priority System Notification Channel for WhatsApp-like alerts
        SystemNotificationHelper.initNotificationChannel(this)

        // Request runtime permission on Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

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
            MainScreen(
                onLogout = {
                    navController.navigate(NavRoutes.ONBOARDING) {
                        popUpTo(NavRoutes.MAIN) { inclusive = true }
                    }
                }
            )
        }
    }
}
