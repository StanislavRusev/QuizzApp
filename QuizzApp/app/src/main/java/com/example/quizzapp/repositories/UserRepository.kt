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
const val PLAYED = "gamesPlayedToday"
const val TYPE = "type"

class UserRepository (private val retrofit: RetrofitApi) {
    private var _currentUser: User? = null
    val currentUser get() = _currentUser
    private var _status = MutableLiveData<Status>()
    val status: LiveData<Status> = _status
    private var _allUsers: List<User>? = null
    val allUsers get() = _allUsers
    private var _enemyPoints: Int = 0
    val enemyPoints get() = _enemyPoints

    fun registerUser(username: String, password: String, confirmPassword: String) {
        if(password != confirmPassword) {
            _status.value = Status.ERROR_PASSWORD_MATCH
            return
        } else if(username == "" || password == "") {
            _status.value = Status.ERROR_NULL_FIELDS
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
                    _status.value = Status.ERROR_USER_EXISTS
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                println(t.toString())
                setConnectionErrorStatus()
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
                    _status.value = Status.ERROR_INVALID_LOGIN
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                println(t.toString())
                setConnectionErrorStatus()
            }

        })
    }

    fun updateLastPlayed() {
        val body = mutableMapOf<String, String>()
        val currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        if(_currentUser?.date == currentDate) {
            _currentUser?.gamesPlayedToday = _currentUser?.gamesPlayedToday?.inc()!!
        } else {
            _currentUser?.date  = currentDate
            _currentUser?.gamesPlayedToday = 1
        }

        body.put(NAME, currentUser?.name.toString())
        body.put(DATE, currentDate)
        body.put(PLAYED, _currentUser?.gamesPlayedToday.toString())

        val requestCall = retrofit.updateLastPlayed(body)

        requestCall.enqueue(object: Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {

            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                println(t.toString())
                setConnectionErrorStatus()
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
                setConnectionErrorStatus()
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
                setConnectionErrorStatus()
            }

        })
    }

    fun getSocialMedia(username: String, password: String, type: String) {
        val body = mutableMapOf<String, String>()
        val currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        body.put(NAME, username)
        body.put(PASSWORD, password)
        body.put(DATE, currentDate)
        body.put(TYPE, type)

        val requestCall = retrofit.getSocialMediaAccount(body)

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
                setConnectionErrorStatus()
            }

        })
    }

    fun joinMultiplayer() {
        val body = mutableMapOf<String, String>()

        body.put(NAME, _currentUser?.name.toString())

        val requestCall = retrofit.joinMultiplayer(body)

        requestCall.enqueue(object: Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                when(response.code()) {
                    200 -> _status.value = Status.PLAYING
                    201 -> _status.value = Status.WAITING
                    else -> _status.value = Status.ERROR
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                setConnectionErrorStatus()
            }

        })


    }

    fun removeMultiplayer() {
        val body = mutableMapOf<String, String>()

        body.put(NAME, _currentUser?.name.toString())

        val requestCall = retrofit.removeMultiplayer(body)

        requestCall.enqueue(object: Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                setConnectionErrorStatus()
            }

        })
    }

    fun checkStatus() {
        val body = mutableMapOf<String, String>()

        body.put(NAME, _currentUser?.name.toString())

        val requestCall = retrofit.checkStatus(body)

        requestCall.enqueue(object: Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                when(response.code()) {
                    200 -> _status.value = Status.PLAYING
                    201 -> _status.value = Status.WAITING
                    202 -> _status.value = Status.ONE_FINISHED
                    204 -> _status.value = Status.ALL_FINISHED
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                    setConnectionErrorStatus()
            }

        })
    }

    fun finishMultiplayer(points: Int) {
        val body = mutableMapOf<String, String>()

        body.put(NAME, _currentUser?.name.toString())
        body.put(POINTS, points.toString())

        val requestCall = retrofit.finishMultiplayer(body)

        requestCall.enqueue(object: Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                _status.value = Status.ONE_FINISHED
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                setConnectionErrorStatus()
            }

        })
    }

    fun getEnemyPoints() {
        val body = mutableMapOf<String, String>()

        body.put(NAME, _currentUser?.name.toString())

        val requestCall = retrofit.getEnemyPoints(body)

        requestCall.enqueue(object: Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if(response.isSuccessful) {
                    _enemyPoints = response.body() ?: 0
                    _status.value = Status.SHOW_RESULTS
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                setConnectionErrorStatus()
            }

        })
    }

    fun getTitle(user: User): String {
        return when {
            user.points <= 200 -> "Noob"
            user.points <= 400 -> "Smart"
            user.points <= 600 -> "Rising star"
            user.points <= 800 -> "Einstein"
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

    private fun setConnectionErrorStatus() {
        _status.value = Status.ERROR_CANNOT_CONNECT
    }
}