package com.ngatour.fruitclassifier.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ngatour.fruitclassifier.data.pref.UserPreferences

@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }

    val name = userPrefs.name.ifBlank { "Tidak diketahui" }
    val email = userPrefs.email.ifBlank { "Tidak diketahui" }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
    ) {
        Text("👤 Profil Pengguna", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Nama:", style = MaterialTheme.typography.labelMedium)
                Text(name, style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(12.dp))

                Text("Email:", style = MaterialTheme.typography.labelMedium)
                Text(email, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
