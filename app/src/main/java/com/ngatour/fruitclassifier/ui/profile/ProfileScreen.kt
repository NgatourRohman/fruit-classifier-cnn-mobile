package com.ngatour.fruitclassifier.ui.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ngatour.fruitclassifier.data.auth.SessionManager
import com.ngatour.fruitclassifier.data.pref.UserPreferences
import com.ngatour.fruitclassifier.data.viewmodel.HistoryViewModel
import com.ngatour.fruitclassifier.ui.components.ClickableRow
import com.ngatour.fruitclassifier.ui.components.ProfileItem
import com.ngatour.fruitclassifier.ui.components.SectionTitle
import com.ngatour.fruitclassifier.ui.nav.Screen
import com.ngatour.fruitclassifier.ui.theme.Poppins
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun ProfileScreen(
    viewModel: HistoryViewModel,
    onThemeToggled: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val name = userPrefs.name.ifBlank { "Unknown" }
    val email = userPrefs.email.ifBlank { "Unknown" }
    var showDialog by remember { mutableStateOf(false) }
    val session = SessionManager(context)

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text("Confirmation", fontFamily = Poppins, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Are you sure you want to delete all classification history?",
                    fontFamily = Poppins
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        try {
                            val history = viewModel.history.value
                            if (history.isEmpty()) {
                                Toast.makeText(context, "No data can be deleted", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.deleteAll(context)
                                Toast.makeText(context, "All history deleted", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Failed to clear history: ${e.message}", Toast.LENGTH_LONG).show()
                        } finally {
                            showDialog = false
                        }
                    }
                ) {
                    Text("Yes", fontFamily = Poppins)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel", fontFamily = Poppins)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF3E0))
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "Profile",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            color = Color.Black,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // User Info
        SectionTitle("User Info")
        Spacer(modifier = Modifier.height(12.dp))

        ProfileItem(icon = Icons.Default.Person, label = name)
        Spacer(modifier = Modifier.height(8.dp))
        ProfileItem(icon = Icons.Default.Email, label = email, labelColor = Color(0xFFFF6F00))

        Spacer(modifier = Modifier.height(24.dp))

        // Data
        SectionTitle("Data")
        Spacer(modifier = Modifier.height(12.dp))

        ClickableRow("Reset Classification History") {
            showDialog = true
        }

        Spacer(modifier = Modifier.height(24.dp))

        // About
        SectionTitle("About")
        Spacer(modifier = Modifier.height(12.dp))

        ClickableRow("App Version", subtitle = "v1.0.0") {
            navController.navigate(Screen.AboutApp.route)
        }

        ClickableRow("Developer Info", subtitle = "Developed by Arthur") {
            navController.navigate(Screen.AboutDeveloper.route)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                session.clear()
                Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .shadow(6.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Logout", color = MaterialTheme.colorScheme.onError, fontFamily = Poppins)
        }
    }
}
