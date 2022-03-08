package com.example.quizzapp.model

enum class Status {
    NORMAL,
    SUCCESS,
    ERROR,
    RECEIVED_USERS,

    //For Multiplayer
    WAITING,
    PLAYING,
    FINISHED
}