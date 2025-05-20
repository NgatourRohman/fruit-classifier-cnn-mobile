package com.ngatour.fruitclassifier

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Classify : Screen("classify", "Klasifikasi", Icons.Filled.Image)
    object History : Screen("history", "Riwayat", Icons.Filled.History)
    object Live : Screen("live", "Kamera Langsung", Icons.Filled.Camera)
    object Profile : Screen("profile", "Profil", Icons.Filled.Person)
}
