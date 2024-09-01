package com.example.todo_navigator_kotlin

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.todo_navigator_kotlin.model.Todo

class TodoAdd : AppCompatActivity() {

    private val calendarDate: TextView by lazy {
        findViewById(R.id.todoCalendarDate)
    }
    private val todoContent: EditText by lazy {
        findViewById(R.id.todoContent)
    }
    private val startLocation: TextView by lazy {
        findViewById(R.id.startLocation)
    }
    private val endLocation: TextView by lazy {
        findViewById(R.id.endLocation)
    }
    private val addTodoButton: Button by lazy {
        findViewById(R.id.addTodoButton)
    }
    private lateinit var locationLauncher: ActivityResultLauncher<Intent>
    private val maxContentLength: Int = 20
    private lateinit var location: String
    private var startLocationX: String = ""
    private var startLocationY: String = ""
    private var endLocationX: String = ""
    private var endLocationY: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_add)

        val selectedDate = intent.getStringExtra("SELECTED_DATE")

        calendarDate.text = selectedDate ?: "No date selected"

        addTodoButton.isEnabled = false

        todoContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addTodoButton.isEnabled = s?.isNotEmpty() == true
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        maxLengthCheck(todoContent, maxContentLength)

        locationLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val address = it.data?.getStringExtra("ADDRESS")
                    val addressX = it.data?.getStringExtra("COORDINATE_X")
                    val addressY = it.data?.getStringExtra("COORDINATE_Y")
                    when (location) {
                        "startLocation" -> {
                            startLocation.text = address
                            startLocationX = addressX.toString()
                            startLocationY = addressY.toString()
                        }

                        "endLocation" -> {
                            endLocation.text = address
                            endLocationX = addressX.toString()
                            endLocationY = addressY.toString()
                        }
                    }
                }
            }

        startLocation.setOnClickListener {
            location = "startLocation"
            val intent = Intent(this, TodoMap::class.java)
            locationLauncher.launch(intent)
        }
        endLocation.setOnClickListener {
            location = "endLocation"
            val intent = Intent(this, TodoMap::class.java)
            locationLauncher.launch(intent)
        }

        addTodoButton.setOnClickListener {
            val date = calendarDate.text.toString()
            val content = todoContent.text.toString()
            val startLocation = startLocation.text.toString().ifEmpty { "없음" }
            val endLocation = endLocation.text.toString().ifEmpty { "없음" }
            if (content.isNotEmpty()) {
                val todo = Todo(
                    System.currentTimeMillis().toString(),
                    date,
                    content,
                    startLocation,
                    startLocationX,
                    startLocationY,
                    endLocation,
                    endLocationX,
                    endLocationY,
                )
                addTodoToFirebase(todo)

                val resultIntent = Intent().apply {
                    putExtra("NEW_TODO", todo)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    private fun maxLengthCheck(editText: EditText, maxLength: Int) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.length > maxLength) {
                    Toast.makeText(
                        this@TodoAdd,
                        "To-do는 ${maxLength}자를 초과할 수 없습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                    todoContent.setText(s.substring(0, maxContentLength))
                    todoContent.setSelection(maxContentLength)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}