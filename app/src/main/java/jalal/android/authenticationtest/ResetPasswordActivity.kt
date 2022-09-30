package jalal.android.authenticationtest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var etEmailResetPass : EditText
    private lateinit var buttonResetPass : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        etEmailResetPass = findViewById(R.id.etEmailResetPass)
        buttonResetPass = findViewById(R.id.buttonResetPass)

        auth = FirebaseAuth.getInstance()

        buttonResetPass.setOnClickListener {
            val emailReset = etEmailResetPass.text.toString()

            auth.sendPasswordResetEmail(emailReset)
                .addOnSuccessListener {
                    Toast.makeText(this, "Please check your email", Toast.LENGTH_LONG).show()

                    val intent = Intent(this, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent)

                }
                .addOnFailureListener {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()

                }
        }

    }
}