package com.ngatour.fruitclassifier

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(onThemeToggled: () -> Unit) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    var name by remember { mutableStateOf(userPrefs.name) }
    var email by remember { mutableStateOf(userPrefs.email) }

    val prefs = LocalThemePreference.current
    var isDarkMode by remember { mutableStateOf(prefs.isDarkMode) }

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(end = 16.dp, top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically)
    {
        Switch(
            checked = isDarkMode,
            onCheckedChange = {
                isDarkMode = it
                prefs.isDarkMode = it
                onThemeToggled() // ACTIVATES THE GLOBAL THEME
            }
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Profil Pengguna", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            userPrefs.name = name
            userPrefs.email = email
            Toast.makeText(context, "Profil disimpan", Toast.LENGTH_SHORT).show()
        }) {
            Text("Simpan Profil")
        }
        Divider(modifier = Modifier.padding(vertical = 16.dp))

        Text("Tentang Aplikasi", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Text("Nama Aplikasi: Klasifikasi Buah Tropis")
        Text("Versi: 1.0.0")
        Text("Dibuat oleh: Arthur")
        Text("Tahun: 2025")

        Spacer(modifier = Modifier.height(8.dp))

        Text("Teknologi yang digunakan:")
        Text("• Kotlin + Jetpack Compose")
        Text("• PyTorch Mobile (TorchScript)")
        Text("• Android Studio")
        Text("• CameraX & Room Database")

        Spacer(modifier = Modifier.height(8.dp))

        Text("Aplikasi ini digunakan untuk mengidentifikasi jenis buah tropis dari gambar yang diunggah atau diambil pengguna menggunakan teknologi deep learning berbasis CNN.")

    }
}
