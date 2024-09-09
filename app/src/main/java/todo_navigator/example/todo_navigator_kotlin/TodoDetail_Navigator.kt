package todo_navigator.example.todo_navigator_kotlin

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import todo_navigator.example.todo_navigator_kotlin.model.Todo

class TodoDetail_Navigator : AppCompatActivity() {

    private val todoContent: TextView by lazy {
        findViewById(R.id.todoContent)
    }
    private val startLocation: TextView by lazy {
        findViewById(R.id.startLocation)
    }
    private val endLocation: TextView by lazy {
        findViewById(R.id.endLocation)
    }
    private val navigatorButton: Button by lazy {
        findViewById(R.id.navigatorButton)
    }
    private lateinit var startLocationX: String
    private lateinit var startLocationY: String
    private lateinit var endLocationX: String
    private lateinit var endLocationY: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_detail)

        val selectedTodo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("SELECTED_TODO", Todo::class.java)
        } else {
            intent.getSerializableExtra("SELECTED_TODO") as Todo?
        }

        selectedTodo?.let { todo ->
            todoContent.text = todo.content
            startLocation.text = "출발지: ${todo.startLocation}"
            startLocationX = todo.startLocationX
            startLocationY = todo.startLocationY
            endLocation.text = "목적지: ${todo.endLocation}"
            endLocationX = todo.endLocationX
            endLocationY = todo.endLocationY
        }

        if (endLocation.text.isEmpty())
            navigatorButton.isEnabled = false

        navigatorButton.setOnClickListener {
            val encodedStartLocation = Uri.encode(selectedTodo?.startLocation ?: "")
            val encodedEndLocation = Uri.encode(selectedTodo?.endLocation ?: "")
            val packageName = applicationContext.packageName
            val mapUri = "nmap://route/public?" +
                    "slat=${startLocationY}&slng=${startLocationX}&sname=${encodedStartLocation}&" +
                    "dlat=${endLocationY}&dlng=${endLocationX}&dname=${encodedEndLocation}&" +
                    "appname=${packageName}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapUri))
            intent.setPackage("com.nhn.android.nmap")

            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                downloadConfirmDialog()
            }
        }
    }

    private fun downloadConfirmDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("다운로드")
        builder.setMessage("네이버 지도를 다운로드하러 이동하시겠습니까?")

        builder.setPositiveButton("이동") { _, _ ->
            val playStoreIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=com.nhn.android.nmap")
            )
            startActivity(playStoreIntent)
        }

        builder.setNegativeButton("취소", null)

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
