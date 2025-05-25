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
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Daftar Akun", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation())

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.register(
                email, password, context,
                onSuccess = { navController.navigate(Screen.Login.route) },
                onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
            )
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Daftar")
        }

        TextButton(onClick = {
            navController.popBackStack()
        }) {
            Text("Sudah punya akun? Login")
        }
    }
}
