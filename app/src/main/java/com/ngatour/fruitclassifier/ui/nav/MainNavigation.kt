package com.ngatour.fruitclassifier.ui.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.ngatour.fruitclassifier.ui.history.HistoryScreen
import com.ngatour.fruitclassifier.data.viewmodel.HistoryViewModel
import com.ngatour.fruitclassifier.ui.stats.StatsScreen
import com.ngatour.fruitclassifier.ui.classify.FruitClassifierScreen
import com.ngatour.fruitclassifier.ui.live.LiveCameraScreen
import com.ngatour.fruitclassifier.ui.profile.ProfileScreen

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainNavigation(isDarkMode: Boolean, onThemeToggle: () -> Unit) {
    val navController = rememberNavController()
    val viewModel: HistoryViewModel = viewModel()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                listOf(Screen.Classify, Screen.History, Screen.Live, Screen.Profile, Screen.Stats).forEach { screen ->
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = { navController.navigate(screen.route) },
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(navController, startDestination = Screen.Classify.route, Modifier.padding(padding)) {
            composable(Screen.Classify.route) {
                FruitClassifierScreen(viewModel = viewModel)
            }
            composable(Screen.History.route) {
                HistoryScreen(viewModel = viewModel)
            }
            composable(Screen.Live.route) {
                LiveCameraScreen(viewModel = viewModel)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onThemeToggled = onThemeToggle,
                    viewModel = viewModel
                )
            }
            composable(Screen.Stats.route) {
                StatsScreen(viewModel = viewModel)
            }
        }
    }
}