package com.example.todo_navigator_kotlin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.todo_navigator_kotlin.model.Todo

class MainActivity : AppCompatActivity() {

    private lateinit var todoList: MutableList<Todo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getTodoListFromFirebase { todoData ->
            todoList = todoData

            val intent = Intent(this, TodoCalendar::class.java)
            intent.putExtra("TODO_LIST", ArrayList(todoList))
            startActivity(intent)
            finish()
        }
    }
}