package jalal.android.authenticationtest

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import jalal.android.authenticationtest.models.User
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


private const val TAG = "LoginActivity"


open class LoginActivity : AppCompatActivity() {
   // var INSTANCE: LoginActivity? = null
    private lateinit var auth : FirebaseAuth
    private  lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //INSTANCE=this

        auth = FirebaseAuth.getInstance()

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etNotRegister = findViewById<TextView>(R.id.etNotRegister)
        val gSignInBtn = findViewById<Button>(R.id.gSignInBtn)
        val etResetPass = findViewById<TextView>(R.id.etResetPass)
        val verification = auth.currentUser?.isEmailVerified

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this , gso)

        etNotRegister.setOnClickListener {

            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        etResetPass.setOnClickListener{
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }

        gSignInBtn.setOnClickListener {
                signInGoogle()
        }


       if(  (auth.currentUser != null && verification == true) ){
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

                    val verification = auth.currentUser?.isEmailVerified

                    if (verification == true){

                        goPostsActivity()
                    }else
                        Toast.makeText(this, "Please verify your Email!", Toast.LENGTH_SHORT).show()

                }else{
                    Log.i(TAG, "signInWithEmail failed", task.exception)
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                    btnLogin.isEnabled = true


                }
            }

        }
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        val account : GoogleSignInAccount? = task.result
        if (task.isSuccessful){

            if (account != null){
                auth.fetchSignInMethodsForEmail(account.email!!)

                    .addOnCompleteListener { task ->
                        val isNewUser = task.result.signInMethods!!.isEmpty()
                        if (isNewUser) {

                            Log.e(TAG, "Is New User!")
                            val intent: Intent = Intent(this, SignUpActivity::class.java)
                            intent.putExtra( "EXTRA_DATA",account.email!!)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent)
                        } else {
                            Log.e(TAG, "Is Old User!")
                            updateUI(account)
                        }
                    }


            }
        }else{
            Toast.makeText(this, task.exception.toString() , Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {

        val credential = GoogleAuthProvider.getCredential(account.idToken , null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful){

                    val intent: Intent = Intent(this, PostsActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent)

            }else{
                Toast.makeText(this, it.exception.toString() , Toast.LENGTH_SHORT).show()

            }
        }
    }


    private fun goPostsActivity() {
        Log.i(TAG, "goPostsActivity")
        val intent = Intent(this, PostsActivity::class.java)
        startActivity(intent)
        finish()
    }


}