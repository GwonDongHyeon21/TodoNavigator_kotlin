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

        val intent = Intent(this, LoginFirebase::class.java)
        startActivity(intent)
        finish()
    }
}