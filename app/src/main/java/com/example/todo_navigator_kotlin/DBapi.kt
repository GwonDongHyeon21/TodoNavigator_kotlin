package com.example.todo_navigator_kotlin

import android.util.Log
import com.example.todo_navigator_kotlin.model.Todo
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

val db = Firebase.database
val todoDB = db.getReference("todos")

//fun getTodoListFromFirebase(): MutableList<Todo> {
//    val todos = mutableListOf<Todo>()
//
//    todoDB.get().addOnSuccessListener { dataSnapshot ->
//        for (data in dataSnapshot.children) {
//            val todo = data.getValue(Todo::class.java)
//            todo?.let { todos.add(it) }
//        }
//
//        Log.d("log","Todos: $todos")
//    }.addOnFailureListener { exception ->
//        Log.d("log","오류가 발생했습니다. 데이터 가져오기를 실패했습니다: ${exception.message}")
//    }
//
//    return todos
//}

fun addTodoToFirebase(todo: Todo) {

    todoDB.push().setValue(todo)
}

fun deleteTodoToFirebase() {

}