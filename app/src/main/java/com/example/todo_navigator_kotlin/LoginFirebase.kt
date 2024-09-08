package com.example.todo_navigator_kotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginFirebase : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        signInAnonymously()
    }

    private fun signInAnonymously() {
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    ValueSingleton.uid = auth.currentUser?.uid ?: ""
                    getTodoListFromFirebase { todoData ->
                        val intent = Intent(this, TodoCalendar::class.java)
                        intent.putExtra("TODO_LIST", ArrayList(todoData))
                        startActivity(intent)
                        finish()
                    }
                } else { //
                }
            }
    }
}
