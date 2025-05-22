package com.ngatour.fruitclassifier.data.remote

import com.ngatour.fruitclassifier.data.model.SupabaseHistory
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface SupabaseService {
    @POST("history")
    suspend fun uploadHistory(@Body data: List<SupabaseHistory>): Response<Unit>

    @GET("history")
    suspend fun getHistory(): List<SupabaseHistory>
}