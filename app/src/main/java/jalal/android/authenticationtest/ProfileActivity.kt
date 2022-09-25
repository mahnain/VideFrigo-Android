package jalal.android.authenticationtest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import jalal.android.authenticationtest.models.Post
import jalal.android.authenticationtest.models.User
import org.checkerframework.checker.index.qual.NonNegative
import org.checkerframework.checker.units.qual.A

private const val TAG ="ProfileActivity"
class ProfileActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val firestore = Firebase.firestore
        val auth = FirebaseAuth.getInstance()

        val buttonEdit = findViewById<Button>(R.id.buttonEdit)

        val etUsernameEdit = findViewById<EditText>(R.id.etUsernameEdit)
        val etAgeEdit = findViewById<EditText>(R.id.etAgeEdit)
        val etEmailEdit = findViewById<TextView>(R.id.etEmailEdit)

        val UsernameInfoText = findViewById<TextView>(R.id.UsernameInfoText)
        val AgeInfoText = findViewById<TextView>(R.id.AgeInfoText)


        etEmailEdit.text = "${auth.currentUser?.email}"

        val postsReference = firestore
            .collection("users").document("${auth.currentUser?.uid}")

        postsReference.addSnapshotListener { snapshot, exception ->
            if(exception != null || snapshot == null){
                Log.e(TAG, "Exception when querying posts", exception)
                return@addSnapshotListener
            }
            val user = snapshot.toObject(User::class.java)
                if (user == null){
                    UsernameInfoText.text = "Not defined yet"
                    AgeInfoText.text = "Not defined yet"
                }else {

                    UsernameInfoText.text = user.username
                    AgeInfoText.text = user.age.toString()
                }
        }





        buttonEdit.setOnClickListener {
            val userInfo = User(
                etUsernameEdit.text.toString(),
                etAgeEdit.text.toString().toInt(),

            )


            firestore.collection("users").document("${auth.currentUser?.uid}")
                .set(userInfo)
                .addOnSuccessListener {
                    Toast.makeText(this, "User info Added", Toast.LENGTH_LONG).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed !!", Toast.LENGTH_LONG).show()
                }

        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_profile,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_logout){
            Log.i(TAG, "User wants to logout")
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent)
            finish()


        }
        return super.onOptionsItemSelected(item)
    }


}