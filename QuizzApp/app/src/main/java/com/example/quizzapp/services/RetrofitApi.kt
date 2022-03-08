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

    @GET("easyBiology")
    fun getEasyBiology(): Call<List<Question>>

    @GET("hardBiology")
    fun getHardBiology(): Call<List<Question>>

    @POST("update")
    fun updatePoints(@Body map: Map<String, String>): Call<Unit>

    @GET("users")
    fun getUsers(): Call<List<User>>

    @POST("updateLastPlayed")
    fun updateLastPlayed(@Body map: Map<String, String>): Call<Unit>

    @POST("socialMedia")
    fun getSocialMediaAccount(@Body map: Map<String, String>): Call<User>

    @POST("addMultiplayer")
    fun joinMultiplayer(@Body map: Map<String, String>): Call<Unit>

    @POST("removeMultiplayer")
    fun removeMultiplayer(@Body map: Map<String, String>): Call<Unit>

    @POST("checkStatus")
    fun checkStatus(@Body map: Map<String, String>): Call<Unit>

    @POST("finishMultiplayer")
    fun finishMultiplayer(@Body map: Map<String, String>): Call<Unit>
}