package com.ngatour.fruitclassifier.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ngatour.fruitclassifier.data.auth.AuthViewModel
import com.ngatour.fruitclassifier.ui.nav.Screen

@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel = AuthViewModel()) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Login", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation())

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = {
            if (email.isBlank()) {
                Toast.makeText(context, "Masukkan email terlebih dahulu", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.forgotPassword(email, context) {
                    Toast.makeText(context, "Link reset password dikirim ke email", Toast.LENGTH_LONG).show()
                }
            }
        }) {
            Text("Lupa password?")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            viewModel.login(
                email, password, context,
                onSuccess = { navController.navigate(Screen.Classify.route) },
                onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
            )
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Masuk")
        }

        TextButton(onClick = {
            navController.navigate(Screen.Register.route)
        }) {
            Text("Belum punya akun? Daftar")
        }
    }
}
