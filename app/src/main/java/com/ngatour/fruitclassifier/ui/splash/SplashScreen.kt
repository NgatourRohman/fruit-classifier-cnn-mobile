package com.ngatour.fruitclassifier.ui.splash

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.ngatour.fruitclassifier.data.auth.SessionManager
import com.ngatour.fruitclassifier.ui.nav.Screen

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Handler(Looper.getMainLooper()).postDelayed({
            val session = SessionManager(context)
            if (session.isLoggedIn()) {
                navController.navigate(Screen.Classify.route) {
                    popUpTo(0)
                }
            } else {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0)
                }
            }
        }, 2000)
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("🌿 Fruit Classifier", style = MaterialTheme.typography.headlineMedium)
    }
}
