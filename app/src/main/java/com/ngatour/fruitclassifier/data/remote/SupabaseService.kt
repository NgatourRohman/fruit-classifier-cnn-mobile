package com.ngatour.fruitclassifier.data.remote

import com.ngatour.fruitclassifier.data.model.SupabaseHistory
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SupabaseService {
    @POST("history")
    suspend fun uploadHistory(@Body data: List<SupabaseHistory>): Response<Unit>

    @GET("history")
    suspend fun getHistory(): List<SupabaseHistory>

    @DELETE("history")
    suspend fun deleteByTimestampAndUsername(
        @Query("timestamp") timestamp: String,
        @Query("username") username: String
    ): Response<Unit>

    @DELETE("history")
    suspend fun deleteAllByUsername(
        @Query("username") username: String
    ): Response<Unit>

    @PUT("object/{bucket}/{filename}")
    suspend fun uploadImage(
        @Path("bucket") bucket: String,
        @Path("filename") filename: String,
        @Body body: RequestBody,
        @Header("Content-Type") contentType: String = "image/jpeg"
    ): Response<Unit>
}