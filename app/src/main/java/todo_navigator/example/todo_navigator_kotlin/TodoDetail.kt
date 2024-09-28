package todo_navigator.example.todo_navigator_kotlin

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import todo_navigator.example.todo_navigator_kotlin.model.Todo

class TodoDetail : AppCompatActivity() {

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

        if (startLocation.text == "출발지: 없음" && endLocation.text == "목적지: 없음") {
            navigatorButton.isEnabled = false
        }

        var googleMapUri = "https://www.google.com/maps/dir/?api=1&" +
                "origin=${startLocationY},${startLocationX}" +
                "&destination=${endLocationY},${endLocationX}&"
        if (startLocation.text != "출발지: 없음" && endLocation.text == "목적지: 없음") {
            googleMapUri = "https://www.google.com/maps/dir/?api=1&" +
                    "origin=${startLocationY},${startLocationX}"
        } else if (startLocation.text == "출발지: 없음" && endLocation.text != "목적지: 없음") {
            googleMapUri = "https://www.google.com/maps/dir/?api=1&" +
                    "destination=${endLocationY},${endLocationX}&"
        }

        navigatorButton.setOnClickListener {
            val encodedStartLocation = Uri.encode(selectedTodo?.startLocation ?: "")
            val encodedEndLocation = Uri.encode(selectedTodo?.endLocation ?: "")

            val naverMapUri = "nmap://route/public?" +
                    "slat=${startLocationY}&slng=${startLocationX}&sname=${encodedStartLocation}&" +
                    "dlat=${endLocationY}&dlng=${endLocationX}&dname=${encodedEndLocation}&"
            val kakaoMapUrl = "kakaomap://route?" +
                    "sp=${startLocationY},${startLocationX}" +
                    "&ep=${endLocationY},${endLocationX}"

            val options = arrayOf("네이버 지도", "구글 지도", "카카오 지도")
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("길찾기 앱 선택")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(naverMapUri))
                            intent.setPackage("com.nhn.android.nmap")
                            try {
                                startActivity(intent)
                            } catch (e: ActivityNotFoundException) {
                                downloadNaverMapDialog()
                            }
                        }

                        1 -> {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(googleMapUri))
                            intent.setPackage("com.google.android.apps.maps")
                            try {
                                startActivity(intent)
                            } catch (e: ActivityNotFoundException) {
                                downloadGoogleMapDialog()
                            }
                        }

                        2 -> {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(kakaoMapUrl))
                            intent.setPackage("net.daum.android.map")
                            try {
                                startActivity(intent)
                            } catch (e: ActivityNotFoundException) {
                                downloadKakaoMapDialog()
                            }
                        }
                    }
                }
                .show()
        }
    }

    private fun downloadNaverMapDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("네이버 지도 다운로드")
            .setMessage("네이버 지도가 설치되어 있지 않습니다.\n다운로드하시겠습니까?")
            .setPositiveButton("다운로드") { _, _ ->
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=com.nhn.android.nmap")
                )
                startActivity(intent)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun downloadGoogleMapDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("구글 지도 다운로드")
            .setMessage("구글 지도가 설치되어 있지 않습니다.\n다운로드하시겠습니까?")
            .setPositiveButton("다운로드") { _, _ ->
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=com.google.android.apps.maps")
                )
                startActivity(intent)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun downloadKakaoMapDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("카카오 지도 다운로드")
            .setMessage("카카오 지도가 설치되어 있지 않습니다.\n다운로드하시겠습니까?")
            .setPositiveButton("다운로드") { _, _ ->
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=net.daum.android.map")
                )
                startActivity(intent)
            }
            .setNegativeButton("취소", null)
            .show()
    }
}
