package todo_navigator.example.todo_navigator_kotlin

import todo_navigator.example.todo_navigator_kotlin.model.Todo
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object ValueSingleton {
    var uid: String = ""
}

val db = Firebase.database
val todoDB = db.reference
val uid = ValueSingleton.uid

fun getTodoListFromFirebase(callback: (MutableList<Todo>) -> Unit) {
    val todos = mutableListOf<Todo>()
    todoDB.child(uid).get().addOnSuccessListener { dataSnapshot ->
        for (dataDate in dataSnapshot.children) {
            for (data in dataDate.children) {
                val todo = Todo(
                    unique = data.child("unique").getValue(String::class.java) ?: "",
                    date = data.child("date").getValue(String::class.java) ?: "",
                    content = data.child("content").getValue(String::class.java) ?: "",
                    startLocation = data.child("startLocation").getValue(String::class.java)
                        ?: "",
                    startLocationX = data.child("startLocationX").getValue(String::class.java)
                        ?: "",
                    startLocationY = data.child("startLocationY").getValue(String::class.java)
                        ?: "",
                    endLocation = data.child("endLocation").getValue(String::class.java) ?: "",
                    endLocationX = data.child("endLocationX").getValue(String::class.java)
                        ?: "",
                    endLocationY = data.child("endLocationY").getValue(String::class.java)
                        ?: "",
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
    todoDB.child(uid).child(todo.date).push().setValue(todo)
}

fun deleteTodoToFirebase(date: String, todoUnique: List<String>) {
    todoDB.child(uid).child(date).get().addOnSuccessListener { dataSnapshot ->
        for (data in dataSnapshot.children) {
            val todoUniqueValue = data.child("unique").getValue(String::class.java)
            if (todoUnique.contains(todoUniqueValue)) {
                data.ref.removeValue()
            }
        }
    }
}