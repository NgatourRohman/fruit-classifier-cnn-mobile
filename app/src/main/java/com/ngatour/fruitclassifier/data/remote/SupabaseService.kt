package com.ngatour.fruitclassifier.data.remote

import com.ngatour.fruitclassifier.data.model.SupabaseHistory
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SupabaseService {
    @POST("history")
    suspend fun uploadHistory(
        @Body data: List<SupabaseHistory>,
        @Header("apikey") apiKey: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InR4eHB1ZmlvcmNqZGRyYXZvc3hwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDc3NTU1NDQsImV4cCI6MjA2MzMzMTU0NH0.L__y0lxToEonPfboB6rAyfplVNoNTgu92iDjOAKeq3c",
        @Header("Authorization") auth: String = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InR4eHB1ZmlvcmNqZGRyYXZvc3hwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDc3NTU1NDQsImV4cCI6MjA2MzMzMTU0NH0.L__y0lxToEonPfboB6rAyfplVNoNTgu92iDjOAKeq3c"
    ): Response<Unit>
}