package com.example.todo_navigator_kotlin

import com.example.todo_navigator_kotlin.model.Todo
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

val db = Firebase.database
val todoDB = db.getReference("todos")

fun getTodoListFromFirebase(callback: (MutableList<Todo>) -> Unit) {
    val todos = mutableListOf<Todo>()
    todoDB.get().addOnSuccessListener { dataSnapshot ->
        for (dataDate in dataSnapshot.children) {
            for (data in dataDate.children) {
                val todo = Todo(
                    unique = data.child("unique").getValue(String::class.java) ?: "",
                    date = data.child("date").getValue(String::class.java) ?: "",
                    content = data.child("content").getValue(String::class.java) ?: "",
                    startLocation = data.child("startLocation").getValue(String::class.java) ?: "",
                    startLocationX = data.child("startLocationX").getValue(String::class.java)
                        ?: "",
                    startLocationY = data.child("startLocationY").getValue(String::class.java)
                        ?: "",
                    endLocation = data.child("endLocation").getValue(String::class.java) ?: "",
                    endLocationX = data.child("endLocationX").getValue(String::class.java) ?: "",
                    endLocationY = data.child("endLocationY").getValue(String::class.java) ?: "",
                )
                todos.add(todo)
            }
        }
        callback(todos)
    }.addOnFailureListener {
        callback(todos)
    }
}

fun addTodoToFirebase(todo: Todo) {
    //todo 날짜별로 저장한다면 더 효율적이지 않을까? -> 삭제할 때도 효율적일듯
    todoDB.child(todo.date).push().setValue(todo)
}

fun deleteTodoToFirebase(date: String, todoUnique: List<String>) {
    //todo 다 읽고 삭제 -> 다시 다 읽고 삭제 => todos 다 읽어야 하는 비효율
//    todoUnique.forEach { unique ->
//        todoDB.orderByChild("unique").equalTo(unique).get().addOnSuccessListener { data ->
//            data.ref.removeValue()
//        }.addOnFailureListener { }
//    }
    //todo 다 읽고 date 일치하는 것들만 가져오고 -> todo 하나씩 돌아가며 확인 -> todoUnique에 todo의 unique이 포함시 삭제
//    todoDB.orderByChild("date").equalTo(date).get().addOnSuccessListener { dataSnapshot ->
//        for (data in dataSnapshot.children) {
//            val todoUniqueValue = data.child("unique").getValue(String::class.java)
//            if (todoUnique.contains(todoUniqueValue)) {
//                data.ref.removeValue()
//            }
//        }
//    }
    //todo *한번에 하나의 date에서만 삭제 가능하다는 점 이용 -> 효율성 증가
    //todo date 일치 부분만 가져오고 -> todo 하나씩 돌아가며 확인 -> todoUnique에 todo의 unique이 포함시 삭제
    todoDB.child(date).get().addOnSuccessListener { dataSnapshot ->
        for (data in dataSnapshot.children) {
            val todoUniqueValue = data.child("unique").getValue(String::class.java)
            if (todoUnique.contains(todoUniqueValue)) {
                data.ref.removeValue()
            }
        }
    }
}