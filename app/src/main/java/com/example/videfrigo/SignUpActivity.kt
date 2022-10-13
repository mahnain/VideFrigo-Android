package com.example.videfrigo


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.videfrigo.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase




class SignUpActivity : AppCompatActivity() {

    private  lateinit var firebaseAuth: FirebaseAuth
    private  lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val firestore = Firebase.firestore

        val buttonSignUp = findViewById<Button>(R.id.buttonSignUp)
        val nameET = findViewById<EditText>(R.id.nameET)
        val ageET = findViewById<EditText>(R.id.ageET)
        val addressET = findViewById<EditText>(R.id.addressET)
        val emailEt  = findViewById<EditText>(R.id.emailEt)
        val passET  = findViewById<TextView>(R.id.passET)
        val confirmPassEt  = findViewById<TextView>(R.id.confirmPassEt)
        val textAlreadyRegistered = findViewById<TextView>(R.id.textAlreadyRegistered)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val bundle: Bundle? = intent.extras
        val emailGoogle = bundle?.get("EXTRA_DATA")
        if(emailGoogle != null) {
            emailEt.setText(emailGoogle.toString())
            emailEt.isEnabled = false;
        }
        googleSignInClient = GoogleSignIn.getClient(this , gso)

        firebaseAuth = FirebaseAuth.getInstance()

        buttonSignUp.setOnClickListener {
            val name = nameET.text.toString()
            val age = ageET.text.toString()
            val address = addressET.text.toString()
            val email = emailEt.text.toString()
            val password = passET.text.toString()
            val confirmPass = confirmPassEt.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && age.isNotEmpty() && name.isNotEmpty() && address.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (password == confirmPass) {

                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            // if(emailGoogle == null) {
                            firebaseAuth.currentUser?.sendEmailVerification()

                                ?.addOnSuccessListener {

                                    Toast.makeText(this, "Please verify your Email", Toast.LENGTH_LONG).show()
                                    ///////////////////////////
                                    val userInfo = User(name, age.toInt(), address, email,)

                                    firestore.collection("users")
                                        .document("${firebaseAuth.currentUser?.uid}").set(userInfo)
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "User info Added", Toast.LENGTH_LONG).show()
                                        }.addOnFailureListener {
                                            Toast.makeText(this, "Failed !!", Toast.LENGTH_LONG).show()
                                        }

                                    ///////////////////////
                                    val intent = Intent(this, LoginActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent)
                                }
                                ?.addOnFailureListener {

                                    Toast.makeText(this, "erreur"+it.toString(), Toast.LENGTH_LONG).show()
                                }


                        } else {
                            Toast.makeText(this, "erreur", Toast.LENGTH_LONG).show()
                            print("erreur: "+it.toString());
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

                        }
                    }
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }
        }
        textAlreadyRegistered.setOnClickListener {
            googleSignInClient.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent)
        }



    }
    override fun onBackPressed() {
        super.onBackPressed()
        googleSignInClient.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent)
    }

}