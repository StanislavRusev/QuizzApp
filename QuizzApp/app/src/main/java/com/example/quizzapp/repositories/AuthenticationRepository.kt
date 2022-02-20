package com.example.quizzapp.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.quizzapp.model.Status
import com.example.quizzapp.services.RetrofitApi
import com.example.quizzapp.services.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

const val NAME = "name"
const val PASSWORD = "password"
const val POINTS = "points"
const val DATE = "date"

class AuthenticationRepository (private val retrofit: RetrofitApi) {
    private var _currentUser: User? = null
    val currentUser get() = _currentUser
    private var _status = MutableLiveData<Status>()
    val status: LiveData<Status> = _status
    private var _allUsers: List<User>? = null
    val allUsers get() = _allUsers

    fun registerUser(username: String, password: String, confirmPassword: String) {
        if(password != confirmPassword) {
            setErrorStatus()
            return
        }

        val body = mutableMapOf<String, String>()
        val currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        body.put(NAME, username)
        body.put(PASSWORD, password)
        body.put(DATE, currentDate)
        val requestCall = retrofit.registerUser(body)

        requestCall.enqueue(object: Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if(response.isSuccessful) {
                    setSuccessStatus()
                } else {
                    setErrorStatus()
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                println(t.toString())
                setErrorStatus()
            }

        })
    }

    fun loginUser(username: String, password: String) {
        val body = mutableMapOf<String, String>()
        body.put(NAME, username)
        body.put(PASSWORD, password)

        val requestCall = retrofit.loginUser(body)

        requestCall.enqueue(object: Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful) {
                    _currentUser = response.body()
                    setSuccessStatus()
                } else {
                    setErrorStatus()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                println(t.toString())
                setErrorStatus()
            }

        })
    }

    fun getAllUsers() {
        val requestCall = retrofit.getUsers()

        requestCall.enqueue(object: Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if(response.isSuccessful) {
                    _allUsers = response.body()
                    _status.value = Status.RECEIVED_USERS
                } else {
                    setErrorStatus()
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                println(t.toString())
                setErrorStatus()
            }

        })
    }

    fun updatePoints(points: Int) {
        val body = mutableMapOf<String, String>()
        val totalPoints = _currentUser?.points?.plus(points)
        body.put(NAME, _currentUser?.name.toString())
        body.put(POINTS, totalPoints.toString())

        val requestCall = retrofit.updatePoints(body)

        requestCall.enqueue(object: Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                _currentUser?.points = _currentUser?.points?.plus(points)!!
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    fun getTitle(user: User): String {
        return when {
            user.points <= 20 -> "Noob"
            user.points <= 40 -> "Smart"
            user.points <= 60 -> "Rising star"
            user.points <= 80 -> "Einstein"
            else -> "Quiz GOD"
        }
    }

    fun clearCurrentUser() {
        _currentUser = null
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
}