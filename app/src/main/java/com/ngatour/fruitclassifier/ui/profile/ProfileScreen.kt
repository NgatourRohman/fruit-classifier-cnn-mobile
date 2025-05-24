package com.ngatour.fruitclassifier.ui.profile

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ngatour.fruitclassifier.data.viewmodel.HistoryViewModel
import com.ngatour.fruitclassifier.LocalThemePreference
import com.ngatour.fruitclassifier.data.pref.ThemePreferences
import com.ngatour.fruitclassifier.data.pref.UserPreferences


@Composable
fun ProfileScreen(viewModel: HistoryViewModel = viewModel()) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    var name by remember { mutableStateOf(userPrefs.name) }
    var email by remember { mutableStateOf(userPrefs.email) }

    val prefs = LocalThemePreference.current
    var isDarkMode by remember { mutableStateOf(prefs.isDarkMode) }



    Column(
        modifier = Modifier
        .padding(16.dp)
        .verticalScroll(rememberScrollState())
        .navigationBarsPadding() // to provide distance from the bottom bar) {
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Profil Pengguna", style = MaterialTheme.typography.titleLarge)
        }
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

        Button(
            onClick = {
                viewModel.deleteAll()
                UserPreferences(context).clearAll()
                ThemePreferences(context).isDarkMode = false
                Toast.makeText(context, "Semua data telah direset", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Reset Semua Data", color = MaterialTheme.colorScheme.onError)
        }

    }
}
