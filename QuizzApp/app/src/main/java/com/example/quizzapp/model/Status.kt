package com.example.quizzapp.model

enum class Status {
    // For Singleplayer and receiving data
    NORMAL,
    SUCCESS,
    ERROR,
    RECEIVED_USERS,

    //For Multiplayer
    WAITING,
    PLAYING,
    ONE_FINISHED,
    ALL_FINISHED,
    SHOW_RESULTS
}