package com.example.quizzapp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.quizzapp.repositories.UserRepository
import com.example.quizzapp.services.User

class UserViewModel (private val repository: UserRepository): ViewModel() {
    val currentUser get() = repository.currentUser
    val status: LiveData<Status> = repository.status
    val allUsers get() = repository.allUsers
    val enemyPoints get() = repository.enemyPoints

    fun registerUser(username: String, password: String, confirmPassword: String) {
        repository.registerUser(username, password, confirmPassword)
    }

    fun loginUser(username: String, password: String) {
        repository.loginUser(username, password)
    }

    fun setNormalStatus() {
        repository.setNormalStatus()
    }

    fun clearCurrentUser() {
        repository.clearCurrentUser()
    }

    fun getTitle(user: User): String {
        return repository.getTitle(user)
    }

    fun updatePoints(points: Int) {
        repository.updatePoints(points)
    }

    fun getAllUsers() {
        repository.getAllUsers()
    }

    fun updateLastPlayed() {
        repository.updateLastPlayed()
    }

    fun canEarnPoints(): Boolean {
        return currentUser?.gamesPlayedToday!! <= 3
    }

    fun getSocialMedia(username: String, password: String, type: String) {
        repository.getSocialMedia(username, password, type)
    }

    fun joinMultiplayer() {
        repository.joinMultiplayer()
    }

    fun removeMultiplayer() {
        repository.removeMultiplayer()
    }

    fun checkStatus() {
        repository.checkStatus()
    }

    fun finishMultiplayer(points: Int) {
        repository.finishMultiplayer(points)
    }

    fun getEnemyPoints() {
        repository.getEnemyPoints()
    }

}