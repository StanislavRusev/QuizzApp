package com.example.quizzapp.services

data class Question(
    val question: String,
    val distractors: Array<String>,
    val answer: String

)
