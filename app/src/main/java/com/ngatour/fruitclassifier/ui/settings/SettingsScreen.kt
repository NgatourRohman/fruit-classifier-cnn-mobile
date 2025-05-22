package com.ngatour.fruitclassifier.ui.settings

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ngatour.fruitclassifier.data.pref.ThemePreferences
import com.ngatour.fruitclassifier.data.pref.UserPreferences
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ngatour.fruitclassifier.LocalThemePreference
import com.ngatour.fruitclassifier.data.viewmodel.HistoryViewModel
import com.ngatour.fruitclassifier.ui.nav.Screen

@Composable
fun SettingsScreen(
    viewModel: HistoryViewModel = viewModel(),
    navController: NavController,
    onThemeToggled: () -> Unit
) {
    val context = LocalContext.current
    val themePrefs = LocalThemePreference.current
    var isDarkMode by remember { mutableStateOf(themePrefs.isDarkMode) }

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Profil Pengguna", style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Buka Profil"
                )
            }
        }

        Text("Pengaturan", style = MaterialTheme.typography.titleLarge)

        // Dark Mode Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Mode Gelap")
            Switch(
                checked = isDarkMode,
                onCheckedChange = {
                    Log.d("ThemeToggle", "User toggled to: $it")
                    isDarkMode = it
                    themePrefs.isDarkMode = it
                    onThemeToggled()
                }
            )
        }

        // Upload ke Supabase
        Button(onClick = {
            viewModel.uploadAllHistoryToSupabase(context)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Upload Riwayat ke Cloud")
        }

        // Export CSV & Share
        Button(onClick = {
            val file = viewModel.exportToCsv(context)
            if (file != null) {
                val uri = androidx.core.content.FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
                val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(android.content.Intent.EXTRA_STREAM, uri)
                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(android.content.Intent.createChooser(intent, "Bagikan Riwayat CSV"))
            } else {
                Toast.makeText(context, "Tidak ada data untuk diekspor", Toast.LENGTH_SHORT).show()
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Export Riwayat ke CSV")
        }

        // Reset Data
        Button(
            onClick = {
                viewModel.deleteAll()
                UserPreferences(context).clearAll()
                themePrefs.isDarkMode = false
                Toast.makeText(context, "Semua data berhasil direset.", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Reset Semua Data", color = MaterialTheme.colorScheme.onError)
        }
    }
}
