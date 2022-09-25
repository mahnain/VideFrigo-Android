package jalal.android.authenticationtest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

private const val TAG = "LoginActivity"
private const val REQUEST_CODE = 5555;

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etNotRegister = findViewById<TextView>(R.id.etNotRegister)

        etNotRegister.setOnClickListener {

            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        val auth = FirebaseAuth.getInstance()
        if(auth.currentUser != null){
            goPostsActivity()
        }

        btnLogin.setOnClickListener {
            btnLogin.isEnabled = false
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isBlank() || password.isBlank()) {
                btnLogin.isEnabled = true
                Toast.makeText(this, "Email/password cant be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            //Firebdase authentication check

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                btnLogin.isEnabled = true
                if(task.isSuccessful) {

                    Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
                   // goSearchActivity()
                    goPostsActivity()
                }else{
                    Log.i(TAG, "signInWithEmail failed", task.exception)
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                    btnLogin.isEnabled = true


                }
            }

        }
    }

    private fun goPostsActivity() {
        Log.i(TAG, "goPostsActivity")
        val intent = Intent(this, PostsActivity::class.java)
        startActivity(intent)
        finish()
    }

    /* private fun goSearchActivity() {
         Log.i(TAG, "goSearchActivity")
     }
     */

}