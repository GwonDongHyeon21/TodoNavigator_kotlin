package com.example.todo_navigator_kotlin

import com.example.todo_navigator_kotlin.model.Todo
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

val db = Firebase.database
val todoDB = db.getReference("todos")

fun getTodoListFromFirebase(callback: (MutableList<Todo>) -> Unit) {
    val todos = mutableListOf<Todo>()
    todoDB.get().addOnSuccessListener { dataSnapshot ->
        for (data in dataSnapshot.children) {
            val todo = Todo(
                unique = data.child("unique").getValue(String::class.java) ?: "",
                date = data.child("date").getValue(String::class.java) ?: "",
                content = data.child("content").getValue(String::class.java) ?: "",
                startLocation = data.child("startLocation").getValue(String::class.java) ?: "",
                startLocationX = data.child("startLocationX").getValue(String::class.java) ?: "",
                startLocationY = data.child("startLocationY").getValue(String::class.java) ?: "",
                endLocation = data.child("endLocation").getValue(String::class.java) ?: "",
                endLocationX = data.child("endLocationX").getValue(String::class.java) ?: "",
                endLocationY = data.child("endLocationY").getValue(String::class.java) ?: "",
            )
            todos.add(todo)
        }
        callback(todos)
    }.addOnFailureListener {
        callback(todos)
    }
}

fun addTodoToFirebase(todo: Todo) {
    todoDB.push().setValue(todo)
}

fun deleteTodoToFirebase(unique: String) {
    todoDB.orderByChild("unique").equalTo(unique).get().addOnSuccessListener { dataSnapshot ->
        for (data in dataSnapshot.children) {
            data.ref.removeValue()
        }
    }
}