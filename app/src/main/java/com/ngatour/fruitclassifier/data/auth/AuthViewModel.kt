package com.ngatour.fruitclassifier.data.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngatour.fruitclassifier.data.remote.SupabaseConfig
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

@Serializable
data class AuthResponse(val access_token: String)

class AuthViewModel : ViewModel() {
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    fun login(email: String, password: String, context: Context, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response: HttpResponse = client.post("${SupabaseConfig.AUTH_BASE_URL}token?grant_type=password")

                {
                    headers {
                        append("apikey", SupabaseConfig.API_KEY)
                        append("Content-Type", "application/json")
                    }
                    setBody("""{"email":"$email","password":"$password"}""")
                }

                if (response.status == HttpStatusCode.OK) {
                    val auth = response.body<AuthResponse>()
                    SessionManager(context).saveToken(auth.access_token)
                    onSuccess()
                } else {
                    onError("Login gagal: ${response.status}")
                }
            } catch (e: Exception) {
                onError("Terjadi kesalahan: ${e.localizedMessage}")
            }
        }
    }

    fun register(email: String, password: String, context: Context, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response: HttpResponse = client.post("${SupabaseConfig.AUTH_BASE_URL}signup")
                {
                    headers {
                        append("apikey", SupabaseConfig.API_KEY)
                        append("Content-Type", "application/json")
                    }
                    setBody("""{"email":"$email","password":"$password"}""")
                }

                if (response.status == HttpStatusCode.OK) {
                    onSuccess()
                } else {
                    onError("Registrasi gagal: ${response.status}")
                }
            } catch (e: Exception) {
                onError("Terjadi kesalahan: ${e.localizedMessage}")
            }
        }
    }
}
