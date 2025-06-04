package com.ngatour.fruitclassifier.ui.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ngatour.fruitclassifier.data.remote.SupabaseConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

class SupabaseHeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder()
            .addHeader("apikey", SupabaseConfig.API_KEY)
            .build()
        return chain.proceed(newRequest)
    }
}

fun createSupabaseImageLoader(context: Context): ImageLoader {
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(SupabaseHeaderInterceptor())
        .build()

    return ImageLoader.Builder(context)
        .okHttpClient(okHttpClient)
        .build()
}

@Composable
fun SupabaseImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val imageLoader = createSupabaseImageLoader(context)

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        imageLoader = imageLoader,
        modifier = modifier
    )
}
