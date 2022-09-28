package com.example.videfrigo.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.videfrigo.R
import com.example.videfrigo.model.Category
import com.squareup.picasso.Picasso


class CategoryAdapter(private val items: List<Category>, private  val selectedItem: onSelectData):RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {


    interface onSelectData {
        fun onSelected(category: Category)
   }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_category, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: Category = items[position]

        val picasso = Picasso.get()
        picasso.load(data.strCategoryThumb).into( holder.imgCategory)
        holder.tvKategory.setText(data.strCategory)
        holder.imgCategory.setOnClickListener {
            println("category : "+data.strCategory)
            selectedItem.onSelected(data)

        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    //Class Holder
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvKategory: TextView
        var cardKategory: CardView
        var imgCategory: ImageView

        init {
            cardKategory = itemView.findViewById(R.id.cardCategory)
            tvKategory = itemView.findViewById(R.id.tvCategory)
            imgCategory = itemView.findViewById(R.id.imgCategory)

        }
    }


}