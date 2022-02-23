package com.example.quizzapp.services

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RetrofitApi {

    @POST("register")
    fun registerUser(@Body map: Map<String, String>): Call<Unit>

    @POST("login")
    fun loginUser(@Body map: Map<String, String>): Call<User>

    @GET("question")
    fun getQuestions(): Call<List<Question>>

    @POST("update")
    fun updatePoints(@Body map: Map<String, String>): Call<Unit>

    @GET("users")
    fun getUsers(): Call<List<User>>

    @POST("updateLastPlayed")
    fun updateLastPlayed(@Body map: Map<String, String>): Call<Unit>

    @POST("/facebook")
    fun getFacebook(@Body map: Map<String, String>): Call<User>
}