package com.example.todo_navigator_kotlin

import com.google.firebase.firestore.FirebaseFirestore

class DBapi {
    private val db = FirebaseFirestore.getInstance()

    fun addTodoToFirebase(todoTitle: String, todoDescription: String) {
        // 컬렉션 참조
        val todoCollection = db.collection("todos")

        // 문서 데이터 만들기
        val todo = hashMapOf(
            "title" to todoTitle,
            "description" to todoDescription,
            "timestamp" to System.currentTimeMillis()
        )

        // Firestore에 문서 추가하기
        todoCollection.add(todo)
    }
}