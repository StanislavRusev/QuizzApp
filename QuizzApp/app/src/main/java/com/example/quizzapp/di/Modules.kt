package com.example.quizzapp.di

import com.example.quizzapp.model.AuthenticationViewModel
import com.example.quizzapp.model.GameViewModel
import com.example.quizzapp.repositories.AuthenticationRepository
import com.example.quizzapp.repositories.GameRepository
import com.example.quizzapp.services.RetrofitApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val URL = "http://10.0.2.2:8080/"

val apiModels = module {
    single { provideRetrofit() }
    single { provideRetrofitApi(get()) }
}

val repositoryModules = module {
    single { AuthenticationRepository(get()) }
    single { GameRepository(get()) }
}

val viewModels = module {
    viewModel { AuthenticationViewModel(get()) }
    viewModel {GameViewModel(get())}
}

fun provideRetrofit(): Retrofit {
    return Retrofit.Builder().baseUrl(URL).addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun provideRetrofitApi(retrofit: Retrofit): RetrofitApi = retrofit.create(RetrofitApi::class.java)