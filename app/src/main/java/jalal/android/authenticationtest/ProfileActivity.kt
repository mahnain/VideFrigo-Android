package jalal.android.authenticationtest

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import jalal.android.authenticationtest.models.User


private const val TAG ="ProfileActivity"
val auth = FirebaseAuth.getInstance()

class ProfileActivity : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val firestore = Firebase.firestore


        val tvName = findViewById<TextView>(R.id.tvName)
        val tvAge = findViewById<TextView>(R.id.tvAge)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val tvAddress = findViewById<TextView>(R.id.tvAddress)
        val buttonEdit = findViewById<ImageView>(R.id.buttonEdit)
        val buttonDelete = findViewById<ImageView>(R.id.buttonDelete)
        val btnResetPassProfile  = findViewById<Button>(R.id.btnResetPassProfile)
        val btnUpdateEmailProfile = findViewById<Button>(R.id.btnUpdateEmailProfile)
        val emailUpdate= findViewById<EditText>(R.id.updateEmailText)
        val btnDeleteAccount = findViewById<Button>(R.id.btnDeleteAccount)

        tvEmail.text = "${auth.currentUser?.email}"

        val postsReference = firestore
            .collection("users").document("${auth.currentUser?.uid}")

        postsReference.addSnapshotListener { snapshot, exception ->
            if (exception != null || snapshot == null) {
                Log.e(TAG, "Exception when querying posts", exception)
                return@addSnapshotListener
            }
            val user = snapshot.toObject(User::class.java)
            if (user == null) {
                tvName.text = "Not defined yet"
                tvAge.text = "Not defined yet"

                tvAddress.text = "Not defined yet"
            } else if (user.age == 0) {
                tvAge.text = "Age"
            }else{

                tvName.text = user.name
                tvAge.text = user.age.toString()
                tvAddress.text = user.address

            }
        }

        buttonEdit.setOnClickListener {
            val bottomSheet = BottomSheetDialog(this)
            bottomSheet.requestWindowFeature(Window.FEATURE_NO_TITLE)
            bottomSheet.setContentView(R.layout.bottom_sheet)
            bottomSheet.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            bottomSheet.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val btnCancel: Button? = bottomSheet.findViewById(R.id.buttonCancel)
            val btnEdit: Button? = bottomSheet.findViewById(R.id.buttonEditInfo)

            val editName: EditText? = bottomSheet.findViewById(R.id.editName)
            val editAge: EditText? = bottomSheet.findViewById(R.id.editAge)
            val editAddress: EditText? = bottomSheet.findViewById(R.id.editAddress)


            postsReference.addSnapshotListener { snapshot, exception ->
                if (exception != null || snapshot == null) {
                    Log.e(TAG, "Exception when querying posts", exception)
                    return@addSnapshotListener
                }
                val user = snapshot.toObject(User::class.java)

                editName?.setText(user?.name)
                if(user?.age == 0){
                    editAge?.setHint("Age")
                }else{
                    editAge?.setText(user?.age.toString())
                }

                editAddress?.setText(user?.address)
            }

                btnCancel?.setOnClickListener {
                    bottomSheet.dismiss()
                }

                btnEdit?.setOnClickListener {
                    val name = editName?.text.toString()
                    val age = editAge?.text.toString()
                    val address = editAddress?.text.toString()

                    if (name.isNotEmpty() && age.isNotEmpty() && name.isNotEmpty() && address.isNotEmpty() ){
                        val updateMap = mapOf(
                            "name" to name,
                            "age" to age.toInt() ,
                            "address" to  address

                        )

                        firestore.collection("users").document("${auth.currentUser?.uid}").update(updateMap)
                        Toast.makeText(this, "Info Updated Successfully", Toast.LENGTH_LONG).show()
                        bottomSheet.dismiss()
                    }else{
                        Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_LONG).show()
                    }

                }
                bottomSheet.show()
            }

        buttonDelete.setOnClickListener {

            val deleteInfoDialog = AlertDialog.Builder(this)

            deleteInfoDialog.setTitle("Delete Personal Info")
            deleteInfoDialog.setMessage("Are you sure to delete All your personal Info?")

            deleteInfoDialog.setPositiveButton("Yes"){dialogInterface, which ->

                val mapDelete = mapOf(
                    "name" to "",
                    "age" to 0,
                    "address" to "",
                )
                firestore.collection("users").document("${auth.currentUser?.uid}").update(mapDelete)
                    .addOnSuccessListener { Toast.makeText(this, "Your Personal Info were successfully Deleted", Toast.LENGTH_LONG).show()
                        restartActivity()
                        }
                    .addOnFailureListener { Toast.makeText(this, "Deletion Failed!", Toast.LENGTH_LONG).show() }

            }

            //performing negative action
            deleteInfoDialog.setNegativeButton("No"){dialogInterface, which ->

            }
            // Create the AlertDialog
            val alertDialog: AlertDialog = deleteInfoDialog.create()
            // Set other dialog properties

            alertDialog.show()

        }


        btnResetPassProfile.setOnClickListener {

            val resetPassword: EditText =  EditText(it.context)
            resetPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            val passwordResetDialog = AlertDialog.Builder(this)

            passwordResetDialog.setTitle("Reset Password")
            passwordResetDialog.setMessage("Enter new Password >= 6 characters long.")
            passwordResetDialog.setView(resetPassword)

            //performing positive action
            passwordResetDialog.setPositiveButton("Yes"){dialogInterface, which ->
                val newPassword = resetPassword.text.toString()
                auth.currentUser?.updatePassword(newPassword)
                    ?.addOnSuccessListener {Toast.makeText(this, "Password Reset Successfully", Toast.LENGTH_LONG).show()  }
                    ?.addOnFailureListener {
                        Toast.makeText(this, "Password Reset Failed", Toast.LENGTH_LONG).show() }

            }

            //performing negative action
            passwordResetDialog.setNegativeButton("No"){dialogInterface, which ->

            }
            // Create the AlertDialog
            val alertDialog: AlertDialog = passwordResetDialog.create()
            // Set other dialog properties

            alertDialog.show()

        }

        btnUpdateEmailProfile.setOnClickListener {

            val verifyPassword: EditText =  EditText(it.context)
            verifyPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            val passwordResetDialog = AlertDialog.Builder(this)

            passwordResetDialog.setTitle("Verify Password")
            passwordResetDialog.setMessage("Enter your Password")
            passwordResetDialog.setView(verifyPassword)
            passwordResetDialog.setPositiveButton("Ok",null)
            passwordResetDialog.setNegativeButton("Cancel",null)

            val alertDialog: AlertDialog = passwordResetDialog.create()

            alertDialog.show()

            val positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            positive.setOnClickListener {

                val password = verifyPassword.text.toString()
                val email = emailUpdate.text.toString().trim()
                if(password.isEmpty()){
                    Toast.makeText(applicationContext,"Password cannot be empty",Toast.LENGTH_LONG).show()
                }else{
                    auth.currentUser?.let {
                        val userCredentials = EmailAuthProvider.getCredential(it?.email!!,password)
                        it?.reauthenticate(userCredentials)
                            ?.addOnCompleteListener {
                                if(it.isSuccessful){
                                    auth.currentUser?.updateEmail(email)?.addOnCompleteListener {
                                        if(it.isSuccessful) {

                                            auth?.currentUser?.sendEmailVerification()
                                                ?.addOnSuccessListener {

                                                    Toast.makeText(applicationContext, "Email has been updated, Please Verify your Email", Toast.LENGTH_LONG).show()

                                                    val updateMap = mapOf(
                                                        "email" to email,
                                                    )
                                                    firestore.collection("users").document("${auth.currentUser?.uid}").update(updateMap)

                                                }
                                                ?.addOnFailureListener {
                                                    Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                                                }

                                        }else{
                                            Toast.makeText(applicationContext, "${it.exception?.message}",Toast.LENGTH_LONG).show()

                                        }
                                    }

                                }else{
                                    Toast.makeText(applicationContext, "${it.exception?.message}",Toast.LENGTH_LONG).show()

                                }
                            }
                    }
                    alertDialog.dismiss()
                }
            }

            negative.setOnClickListener {
                alertDialog.dismiss()
            }


        }

        btnDeleteAccount.setOnClickListener {


            val deleteAccountDialog = AlertDialog.Builder(this)

            deleteAccountDialog.setTitle("Delete Account")
            deleteAccountDialog.setMessage("Are you sure to delete your account?")


            //performing positive action
            deleteAccountDialog.setPositiveButton("Yes"){dialogInterface, which ->

                auth.currentUser?.delete()
                    ?.addOnSuccessListener {Toast.makeText(this, "Account Deleted Successfully", Toast.LENGTH_LONG).show()
                    auth.signOut()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent)
                        finish()
                    }
                    ?.addOnFailureListener {
                        Toast.makeText(this, "Password Reset Failed", Toast.LENGTH_LONG).show() }

            }

            //performing negative action
           deleteAccountDialog.setNegativeButton("No"){dialogInterface, which ->

            }
            // Create the AlertDialog
            val alertDialog: AlertDialog = deleteAccountDialog.create()
            // Set other dialog properties

            alertDialog.show()

        }


        }


        override fun onCreateOptionsMenu(menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.menu_profile, menu)
            return super.onCreateOptionsMenu(menu)
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            if (item.itemId == R.id.menu_logout) {
                Log.i(TAG, "User wants to logout")

                var googleSignInClient: GoogleSignInClient
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
                googleSignInClient = GoogleSignIn.getClient(this, gso)


                googleSignInClient.signOut()
                auth.signOut()

                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent)
                finish()


            }
            return super.onOptionsItemSelected(item)
        }

    private fun restartActivity() {
        val intent = intent
        finish()
        startActivity(intent)
    }
    }
