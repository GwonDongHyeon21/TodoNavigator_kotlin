package todo_navigator.example.todo_navigator_kotlin

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginFirebase : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val progressBar: ProgressBar by lazy {
        findViewById(R.id.progress_bar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        progressBar.visibility = ProgressBar.VISIBLE

        signInAnonymously()
    }

    private fun signInAnonymously() {
        if (auth.currentUser == null) {
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
                    } else {
                        progressBar.visibility = ProgressBar.INVISIBLE
                    }
                }
        }else{
            ValueSingleton.uid = auth.currentUser?.uid ?: ""
            getTodoListFromFirebase { todoData ->
                val intent = Intent(this, TodoCalendar::class.java)
                intent.putExtra("TODO_LIST", ArrayList(todoData))
                startActivity(intent)
                finish()
            }
        }
    }
}
