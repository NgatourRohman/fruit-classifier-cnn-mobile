package com.ngatour.fruitclassifier.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ngatour.fruitclassifier.data.auth.AuthViewModel
import com.ngatour.fruitclassifier.ui.nav.Screen

@Composable
fun RegisterScreen(navController: NavController, viewModel: AuthViewModel = AuthViewModel()) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Daftar Akun", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nama") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (name.isBlank() || email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isLoading = true
                viewModel.register(
                    email = email,
                    password = password,
                    name = name,
                    context = context,
                    onSuccess = {
                        isLoading = false
                        Toast.makeText(context, "Berhasil daftar", Toast.LENGTH_SHORT).show()
                        navController.navigate(Screen.Login.route)
                    },
                    onError = {
                        isLoading = false
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoading) "Mendaftarkan..." else "Daftar")
        }

        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = { navController.popBackStack() }) {
            Text("Sudah punya akun? Login")
        }
    }
}
