package com.example.quizzapp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.quizzapp.repositories.GameRepository

class GameViewModel (private val repository: GameRepository): ViewModel() {
    val status: LiveData<Status> = repository.status
    private val currentQuestions get() = repository.currentQuestions
    private val numberOfCurrentQuestion get() = repository.numberOfCurrentQuestion
    val earnedPoints get() = repository.earnedPoints

    fun setupEasyBiologyQuestions() {
        repository.setupEasyBiologyQuestions()
    }

    fun setNormalStatus() {
        repository.setNormalStatus()
    }

    fun toNextQuestion(): Boolean {
        repository.toNextQuestion()
        if(numberOfCurrentQuestion >= currentQuestions.size)
            return false
        return true
    }

    fun getQuestion(): String {
        return currentQuestions[numberOfCurrentQuestion].question
    }

    fun getDistractors(): Array<String> {
        return currentQuestions[numberOfCurrentQuestion].distractors
    }

    fun isRightAnswer(answer: String): Boolean {
        return currentQuestions[numberOfCurrentQuestion].answer == answer
    }

    fun addPoints() {
        repository.addPoints()
    }

}