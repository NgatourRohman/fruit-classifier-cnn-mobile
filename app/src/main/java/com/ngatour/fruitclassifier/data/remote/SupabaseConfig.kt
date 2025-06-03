package com.ngatour.fruitclassifier.data.remote

import com.ngatour.fruitclassifier.BuildConfig

object SupabaseConfig {
    const val API_KEY = BuildConfig.SUPABASE_API_KEY
    const val BASE_URL = BuildConfig.SUPABASE_BASE_URL
    val AUTH_BASE_URL: String
        get() = BASE_URL.replace("/rest/v1/", "/auth/v1/")
    val STORAGE_BASE_URL: String
        get() = BASE_URL.replace("/rest/v1/", "/storage/v1/")
    val STORAGE_PUBLIC_URL: String
        get() = BASE_URL.replace("/rest/v1/", "")

}
