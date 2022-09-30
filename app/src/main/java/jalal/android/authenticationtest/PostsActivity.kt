package jalal.android.authenticationtest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import jalal.android.authenticationtest.adapter.PostsAdapter
import jalal.android.authenticationtest.models.Post

private const val TAG = "PostsActivity"
class PostsActivity : AppCompatActivity() {

    private lateinit var firestoreDb: FirebaseFirestore
    private lateinit var posts: MutableList<Post>
    private lateinit var adapter: PostsAdapter
    private lateinit var rvPosts: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)

        rvPosts = findViewById(R.id.rvPosts)

        posts = mutableListOf()

        adapter = PostsAdapter(this, posts)

        rvPosts.adapter = adapter
        rvPosts.layoutManager = LinearLayoutManager(this)





        val auth = FirebaseAuth.getInstance()
        Log.i(TAG, "${auth.currentUser?.uid}")
        firestoreDb = FirebaseFirestore.getInstance()
        val postsReference = firestoreDb
            .collection("posts")
            .limit(20)
            .orderBy("creation_time_ms", Query.Direction.DESCENDING)

        postsReference.addSnapshotListener { snapshot, exception ->
            if(exception != null || snapshot == null){
                Log.e(TAG, "Exception when querying posts", exception)
                return@addSnapshotListener
            }
            val postList = snapshot.toObjects(Post::class.java)
            posts.clear()
            posts.addAll(postList)
            adapter.notifyDataSetChanged()

            for(post in postList ){
                Log.i(TAG, "Post $post")


            }
           /* for(document in snapshot.documents ){
                Log.i(TAG, "Document $document.id: $document.data")
            }
            */
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_posts,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_profile){
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)


        }
        return super.onOptionsItemSelected(item)
    }
}