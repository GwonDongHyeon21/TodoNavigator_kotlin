package todo_navigator.example.todo_navigator_kotlin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import todo_navigator.example.todo_navigator_kotlin.databinding.ActivityTodoAddBinding
import todo_navigator.example.todo_navigator_kotlin.map.GoogleMap
import todo_navigator.example.todo_navigator_kotlin.model.Todo

class TodoAdd : AppCompatActivity() {

    private lateinit var todoAddBinding: ActivityTodoAddBinding

    private lateinit var locationLauncher: ActivityResultLauncher<Intent>
    private val maxContentLength: Int = 20
    private lateinit var location: String
    private var startLocationX: String = ""
    private var startLocationY: String = ""
    private var endLocationX: String = ""
    private var endLocationY: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        todoAddBinding = ActivityTodoAddBinding.inflate(layoutInflater)
        setContentView(todoAddBinding.root)

        val calendarDate = todoAddBinding.todoCalendarDate
        val todoContent = todoAddBinding.todoContent
        val startLocation = todoAddBinding.startLocation
        val endLocation = todoAddBinding.endLocation
        val addTodoButton = todoAddBinding.addTodoButton

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
        todoContent.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_SEARCH || action == EditorInfo.IME_ACTION_DONE) {
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(todoContent.windowToken, 0)
                true
            } else {
                false
            }
        }

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
            val intent = Intent(this, GoogleMap::class.java)
            locationLauncher.launch(intent)
        }
        endLocation.setOnClickListener {
            location = "endLocation"
            val intent = Intent(this, GoogleMap::class.java)
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
                    todoAddBinding.todoContent.setText(s.substring(0, maxContentLength))
                    todoAddBinding.todoContent.setSelection(maxContentLength)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}