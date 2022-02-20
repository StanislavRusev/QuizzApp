package com.example.quizzapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.quizzapp.services.User
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}