package com.example.todo_navigator_kotlin.model

import java.io.Serializable

data class Todo(
    val date: String,
    val content: String,
    val startLocation: String,
    val startLocationX: String,
    val startLocationY: String,
    val endLocation: String,
    val endLocationX: String,
    val endLocationY: String,
    var isDone: Boolean = false
) : Serializable
