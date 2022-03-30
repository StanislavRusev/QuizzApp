package com.example.quizzapp.model

enum class Status {
    // For Singleplayer and receiving data
    NORMAL,
    SUCCESS,
    ERROR,
    RECEIVED_USERS,

    // Errors
    ERROR_INVALID_LOGIN,
    ERROR_PASSWORD_MATCH,
    ERROR_NULL_FIELDS,
    ERROR_USER_EXISTS,
    ERROR_CANNOT_CONNECT,

    //For Multiplayer
    WAITING,
    PLAYING,
    ONE_FINISHED,
    ALL_FINISHED,
    SHOW_RESULTS
}