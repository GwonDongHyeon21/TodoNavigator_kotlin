package todo_navigator.example.todo_navigator_kotlin

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import todo_navigator.example.todo_navigator_kotlin.databinding.ActivityLoginBinding

class LoginFirebase : AppCompatActivity() {

    private lateinit var loginFirebaseBinding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginFirebaseBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginFirebaseBinding.root)

        auth = FirebaseAuth.getInstance()
        loginFirebaseBinding.progressBar.visibility = ProgressBar.VISIBLE

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
                        loginFirebaseBinding.progressBar.visibility = ProgressBar.INVISIBLE
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
