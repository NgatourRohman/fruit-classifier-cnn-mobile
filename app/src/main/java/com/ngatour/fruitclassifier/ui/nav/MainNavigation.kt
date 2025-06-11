package com.ngatour.fruitclassifier.ui.nav

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.ngatour.fruitclassifier.data.viewmodel.HistoryViewModel
import com.ngatour.fruitclassifier.ui.auth.LoginScreen
import com.ngatour.fruitclassifier.ui.auth.RegisterScreen
import com.ngatour.fruitclassifier.ui.classify.FruitClassifierScreen
import com.ngatour.fruitclassifier.ui.components.CustomBottomNavigationBar
import com.ngatour.fruitclassifier.ui.history.HistoryScreen
import com.ngatour.fruitclassifier.ui.live.LiveCameraScreen
import com.ngatour.fruitclassifier.ui.profile.ProfileScreen
import com.ngatour.fruitclassifier.ui.splash.SplashScreen
import com.ngatour.fruitclassifier.ui.results.ResultsScreen
import com.ngatour.fruitclassifier.ui.about.AboutAppScreen
import com.ngatour.fruitclassifier.ui.about.AboutDeveloperScreen

@OptIn(ExperimentalPermissionsApi::class, ExperimentalAnimationApi::class)
@Composable
fun MainNavigation(isDarkMode: Boolean, onThemeToggle: () -> Unit) {
    val navController = rememberNavController()
    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = currentBackStackEntry?.destination?.route
    val viewModel: HistoryViewModel = viewModel()

    val hideBottomBarRoutes = listOf(
        Screen.Splash.route,
        Screen.Login.route,
        Screen.Register.route
    )

    Scaffold(
        containerColor = Color(0xFFFFF3E0),
        bottomBar = {
            if (currentRoute !in hideBottomBarRoutes) {
                CustomBottomNavigationBar(
                    active = when (currentRoute) {
                        Screen.Classify.route -> "home"
                        Screen.History.route -> "history"
                        Screen.Live.route -> "scan"
                        Screen.Result.route -> "results"
                        Screen.Profile.route -> "profile"
                        else -> "home"
                    },
                    onItemClick = { id ->
                        when (id) {
                            "home" -> navController.navigate(Screen.Classify.route)
                            "history" -> navController.navigate(Screen.History.route)
                            "scan" -> navController.navigate(Screen.Live.route)
                            "results" -> navController.navigate(Screen.Result.route)
                            "profile" -> navController.navigate(Screen.Profile.route)
                        }
                    }
                )
            }
        }
    ) { padding ->
        AnimatedNavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(padding),
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) },
            popEnterTransition = { fadeIn(animationSpec = tween(300)) },
            popExitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(navController)
            }
            composable(route = Screen.Login.route) {
                LoginScreen(navController)
            }
            composable(route = Screen.Register.route) {
                RegisterScreen(navController)
            }
            composable(Screen.Classify.route) {
                FruitClassifierScreen(viewModel = viewModel)
            }
            composable(Screen.History.route) {
                HistoryScreen(viewModel = viewModel)
            }
            composable(Screen.Live.route) {
                LiveCameraScreen(viewModel = viewModel)
            }
            composable(Screen.Result.route) {
                ResultsScreen(viewModel = viewModel)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    onThemeToggled = onThemeToggle,
                    navController
                )
            }
            composable(Screen.AboutApp.route) {
                AboutAppScreen(navController)
            }
            composable(Screen.AboutDeveloper.route) {
                AboutDeveloperScreen(navController)
            }
        }
    }
}
