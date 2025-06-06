package com.ngatour.fruitclassifier.data.auth

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngatour.fruitclassifier.data.pref.UserPreferences
import com.ngatour.fruitclassifier.data.remote.SupabaseConfig
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class AuthViewModel : ViewModel() {
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    fun login(
        email: String,
        password: String,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (email.isBlank() || password.isBlank()) {
            onError("Email and password must not be empty")
            return
        }

        viewModelScope.launch {
            try {
                val response: HttpResponse = client.post("${SupabaseConfig.AUTH_BASE_URL}token?grant_type=password") {
                    headers {
                        append("apikey", SupabaseConfig.API_KEY)
                        append(HttpHeaders.ContentType, "application/json")
                    }
                    setBody("""{"email":"$email","password":"$password"}""")
                }

                Log.d("LOGIN", "Status: ${response.status}")
                Log.d("LOGIN", "Body: ${response.bodyAsText()}")

                if (response.status == HttpStatusCode.OK) {
                    val auth = response.body<AuthResponse>()
                    SessionManager(context).saveToken(auth.access_token)

                    // Save user data
                    val nameFromMetadata = auth.user?.user_metadata?.name
                    val email = auth.user?.email ?: "-"
                    val name = nameFromMetadata ?: "User"

                    UserPreferences(context).saveUser(name = name, email = email)

                    onSuccess()

                } else {
                    onError("Login failed: ${response.bodyAsText()}")
                }
            } catch (e: Exception) {
                onError("An error occurred: ${e.localizedMessage}")
            }
        }
    }

    fun register(
        email: String,
        password: String,
        name: String,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response: HttpResponse = client.post("${SupabaseConfig.AUTH_BASE_URL}signup") {
                    headers {
                        append("apikey", SupabaseConfig.API_KEY)
                        append("Content-Type", "application/json")
                    }
                    setBody(
                        """
                        {
                            "email": "$email",
                            "password": "$password",
                            "data": {
                                "name": "$name"
                            }
                        }
                        """.trimIndent()
                    )
                }

                if (response.status == HttpStatusCode.OK) {
                    onSuccess()
                } else {
                    onError("Registration failed: ${response.status}")
                }
            } catch (e: Exception) {
                onError("An error occurred: ${e.localizedMessage}")
            }
        }
    }

    fun forgotPassword(email: String, context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response: HttpResponse = client.post("${SupabaseConfig.AUTH_BASE_URL}recover") {
                    headers {
                        append("apikey", SupabaseConfig.API_KEY)
                        append(HttpHeaders.ContentType, "application/json")
                    }
                    setBody("""{"email":"$email"}""")
                }

                if (response.status == HttpStatusCode.OK) {
                    onSuccess()
                } else {
                    Log.e("RESET", "Failed to send reset email: ${response.bodyAsText()}")
                    Toast.makeText(context, "Failed to send reset email", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("RESET", "Error: ${e.localizedMessage}")
                Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun resetPassword(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response: HttpResponse = client.post("${SupabaseConfig.AUTH_BASE_URL}recover") {
                    headers {
                        append("apikey", SupabaseConfig.API_KEY)
                        append(HttpHeaders.ContentType, "application/json")
                    }
                    setBody("""{"email":"$email"}""")
                }

                if (response.status == HttpStatusCode.OK) {
                    onSuccess()
                } else {
                    onError("Failed to send reset email: ${response.bodyAsText()}")
                }
            } catch (e: Exception) {
                onError("An error occurred: ${e.localizedMessage}")
            }
        }
    }
}
