package com.ngatour.fruitclassifier.ui.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ngatour.fruitclassifier.R
import com.ngatour.fruitclassifier.data.auth.AuthViewModel
import com.ngatour.fruitclassifier.ui.nav.Screen
import com.ngatour.fruitclassifier.ui.theme.Poppins

@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel = AuthViewModel()) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        // Background image
        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Blur Rectangle container behind form
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.73f)
                .align(Alignment.BottomCenter)
                .graphicsLayer {
                    alpha = 0.64f
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    clip = true
                }
                .background(Color.White)
                .blur(9.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.80f)
                .align(Alignment.BottomCenter)
        ) {

            Image(
                painter = painterResource(id = R.drawable.fruit_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = (-120).dp)
            )

            // Login Form
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    "Hello Again",
                    fontSize = 28.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    "Welcome back, You’ve\nbeen missed!",
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Email", fontFamily = Poppins) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(16.dp),
                            ambientColor = Color.Gray.copy(alpha = 0.3f),
                            spotColor = Color.Black.copy(alpha = 0.3f)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(27.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Password", fontFamily = Poppins) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = { Icon(Icons.Default.VisibilityOff, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(16.dp),
                            ambientColor = Color.Gray.copy(alpha = 0.3f),
                            spotColor = Color.Black.copy(alpha = 0.3f)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(27.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(50), // Elips shape
                                ambientColor = Color.Black.copy(alpha = 0.2f),
                                spotColor = Color.Black.copy(alpha = 0.2f)
                            )
                    ) {
                        Switch(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFFFF9800)
                            )
                        )
                    }
                    Text(
                        "Remember me",
                        fontFamily = Poppins,
                        color = Color(0xFF664A00),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = {
                        if (email.isNotBlank()) {
                            viewModel.forgotPassword(email, context) {
                                Toast.makeText(
                                    context,
                                    "Link reset dikirim ke email",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }) {
                        Text("Forgot Password?", color = Color(0xFFF57C00), fontFamily = Poppins)
                    }
                }

                Spacer(modifier = Modifier.height(27.dp))
                Button(
                    onClick = {
                        viewModel.login(
                            email, password, context,
                            onSuccess = { navController.navigate(Screen.Classify.route) },
                            onError = { msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6F00))
                ) {
                    Text("SIGN IN", color = Color.White, fontFamily = Poppins, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Don't have an account?", color = Color.Black, fontFamily = Poppins)
                    TextButton(
                        onClick = { navController.navigate(Screen.Register.route) },
                        contentPadding = PaddingValues(start = 4.dp)
                    ) {
                        Text("Sign up", color = Color(0xFFFF6F00),fontFamily = Poppins, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(navController = rememberNavController())
}
