package com.example.quizzapp.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.quizzapp.model.Status
import com.example.quizzapp.services.Question
import com.example.quizzapp.services.RetrofitApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val POINTS_EASY_BIOLOGY = 3
const val POINTS_HARD_BIOLOGY = 5

class GameRepository (private val retrofit: RetrofitApi) {
    private var _status = MutableLiveData<Status>()
    private var _numberOfCurrentQuestion: Int = 0
    private var _currentQuestions: List<Question> = listOf()
    private var _earnedPoints: Int = 0

    val status: LiveData<Status> = _status
    val numberOfCurrentQuestion get() = _numberOfCurrentQuestion
    val currentQuestions get() = _currentQuestions
    val earnedPoints get() = _earnedPoints

    private var gameMode: String = "easyBiology"
    private var pointPerQuestion: Int = 0

    fun setupQuestions() {
        var requestCall: Call<List<Question>>? = null

        when(gameMode) {
            "easyBiology" -> {
                requestCall = retrofit.getEasyBiology()
                pointPerQuestion = POINTS_EASY_BIOLOGY
            }
            "hardBiology" -> {
                requestCall = retrofit.getHardBiology()
                pointPerQuestion = POINTS_HARD_BIOLOGY
            }
        }

        requestCall?.enqueue(object: Callback<List<Question>> {
            override fun onResponse(
                call: Call<List<Question>>,
                response: Response<List<Question>>
            ) {
                if(response.isSuccessful) {
                    _earnedPoints = 0
                    _numberOfCurrentQuestion = 0
                    _currentQuestions = response.body()!!
                    setSuccessStatus()
                } else {
                    setErrorStatus()
                }
            }

            override fun onFailure(call: Call<List<Question>>, t: Throwable) {
                _status.value = Status.ERROR_CANNOT_CONNECT
            }

        })
    }

    fun toNextQuestion() {
        _numberOfCurrentQuestion++
    }

    fun addPoints() {
        _earnedPoints += pointPerQuestion
    }

    fun setNormalStatus() {
        _status.value = Status.NORMAL
    }

    private fun setErrorStatus() {
        _status.value = Status.ERROR
    }

    private fun setSuccessStatus() {
        _status.value = Status.SUCCESS
    }

    fun setGameMode(mode: String) {
        gameMode = mode
    }
}