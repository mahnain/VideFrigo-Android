package jalal.android.authenticationtest.adapter

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import jalal.android.authenticationtest.R
import jalal.android.authenticationtest.models.Post

class PostsAdapter(val context: Context, val posts: List<Post>) :
    RecyclerView.Adapter<PostsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_post,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(posts[position])
    }

    override fun getItemCount() = posts.size

    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(post: Post) {

            itemView.findViewById<TextView>(R.id.tvUsername).text = post.user?.name
            itemView.findViewById<TextView>(R.id.tvDescription).text = post.description
            Glide.with(context).load(post.image).into(itemView.findViewById(R.id.ivPost))
            itemView.findViewById<TextView>(R.id.tvRelativeTime).text = DateUtils.getRelativeTimeSpanString(post.creationTimeMs)
        }

    }
}