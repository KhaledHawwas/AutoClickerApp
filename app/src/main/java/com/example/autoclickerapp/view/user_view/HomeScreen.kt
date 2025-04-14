package com.example.autoclickerapp.view.user_view

import android.content.*
import android.net.*
import android.os.*
import android.provider.*
import android.util.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.hilt.navigation.compose.*
import androidx.navigation.*
import com.example.autoclickerapp.R
import com.example.autoclickerapp.notification.*
import com.example.autoclickerapp.viewmodel.*
import com.google.accompanist.systemuicontroller.*


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    role: Int,
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    val systemUiController = rememberSystemUiController()

    val context = LocalContext.current
    val backgroundColor = colorResource(R.color.light_green)
    SideEffect {
        systemUiController.setStatusBarColor(
            color = backgroundColor,
            darkIcons = true
        )
    }
    var isServiceEnabled by remember { mutableStateOf(isAccessibilityServiceEnabled(context)) }

    LaunchedEffect(Unit) {
        isServiceEnabled = isAccessibilityServiceEnabled(context)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                isServiceEnabled = isAccessibilityServiceEnabled(context)

                if (!isServiceEnabled) {
                    openAccessibilitySettings(context)
                    Handler(Looper.getMainLooper()).postDelayed({
                        isServiceEnabled = isAccessibilityServiceEnabled(context)
                    }, 2000)
                } else if (!isOverlayPermissionGranted(context)) {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${context.packageName}")
                    )
                    context.startActivity(intent)
                } else {
                    // All permissions granted – proceed
                    navigateToHomeScreen(context)
                    Handler(Looper.getMainLooper()).postDelayed({
                        startNotificationService(context)
                    }, 1000)
                    OverlayLayout(context).showOverlay()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.first_blue),
                contentColor = Color.White
            )
        ) {
            Text(
                if (!isServiceEnabled) "Enable Accessibility Service"
                else if (!isOverlayPermissionGranted(context)) "Enable Overlay Permission"
                else "Go to Home & Start Scrolling"
            )
        }

    }
}



fun isOverlayPermissionGranted(context: Context): Boolean {
    return Settings.canDrawOverlays(context)
}

// Function to check if Accessibility Service is enabled
fun isAccessibilityServiceEnabled(context: Context): Boolean {
    val enabledServices = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    ) ?: return false

    val colonSplitter = enabledServices.split(":")
    return colonSplitter.any { it.contains(context.packageName) }
}


// Open Accessibility settings for user to enable service
fun openAccessibilitySettings(context: Context) {
    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(intent)
}

// Start the NotificationService
fun startNotificationService(context: Context) {
    val intent = Intent(context, NotificationService::class.java)
    context.startForegroundService(intent)
}

// Stop the NotificationService when the app is destroyed
fun stopNotificationService(context: Context) {
    val intent = Intent(context, NotificationService::class.java)
    context.stopService(intent)
}

// Function to navigate to the home screen
fun navigateToHomeScreen(context: Context) {
    val intent = Intent(Intent.ACTION_MAIN)
    intent.addCategory(Intent.CATEGORY_HOME)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(intent)
}


