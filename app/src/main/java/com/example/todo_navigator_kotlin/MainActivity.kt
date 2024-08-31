package com.example.todo_navigator_kotlin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Firebase data 가져오기 -> TodoCalendar로 넘겨주기
        //로딩 화면

        val intent = Intent(this, TodoCalendar::class.java)
        startActivity(intent)
        finish()
    }
}