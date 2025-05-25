package com.ngatour.fruitclassifier.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Start
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Classify : Screen("classify", "Klasifikasi", Icons.Filled.Image)
    object History : Screen("history", "Riwayat", Icons.Filled.History)
    object Live : Screen("live", "Kamera Langsung", Icons.Filled.Camera)
    object Stats : Screen("stats", "Statistik", Icons.Filled.QueryStats)
    object Settings : Screen("settings", "Pengaturan", Icons.Filled.Settings)
    object Profile : Screen("profile", "Profil", Icons.Filled.Person)
    object About : Screen("about", "Tentang Aplikasi", Icons.Filled.Info)
    object Login : Screen("login", "Login", Icons.Filled.Lock)
    object Register : Screen("register", "Register", Icons.Filled.PersonAdd)
    object Splash : Screen("splash", "Splash", Icons.Filled.Start)
}