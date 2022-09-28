import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.videfrigo.R
import com.example.videfrigo.activity.MealFragment
import com.example.videfrigo.model.Meal
import com.squareup.picasso.Picasso

class MealAdapter(private val items: List<Meal>, private val selectedItem: onSelectItem):
    RecyclerView.Adapter<MealAdapter.ViewHolder>() {


    interface onSelectItem{
        fun onSelected(meal: Meal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_recipe, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: Meal = items[position]

        val picasso = Picasso.get()
        picasso.load(data.strMealThumb).into( holder.imgCategory)
        holder.tvKategory.setText(data.strMeal)
        holder.imgCategory.setOnClickListener {
            println("category : "+data.strMeal)
            selectedItem.onSelected(data)

        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    //Class Holder
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvKategory: TextView

        var imgCategory: ImageView

        init {

            tvKategory = itemView.findViewById(R.id.tvMeal)
            imgCategory = itemView.findViewById(R.id.imgMeal)

        }
    }


}