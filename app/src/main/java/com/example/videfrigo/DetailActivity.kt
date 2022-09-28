package com.example.videfrigo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.squareup.picasso.Picasso
import org.json.JSONObject


class DetailActivity : AppCompatActivity() {

    private var idMeal: String = "0"

    private var imageView : ImageView?= null
    private var textViewStrMeal: TextView?=null
    private var textViewCategory: TextView?=null
    private var textViewIngredients: TextView?=null
    private var textViewInstructions: TextView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val bundle: Bundle? = intent.extras
        idMeal = bundle?.get("detailRecipe").toString()
        initLayout()
        fetchJson()


    }

    private fun initLayout()
    {
        imageView=findViewById(R.id.imgRecipe)
        textViewStrMeal=findViewById(R.id.tvMealname)
        textViewCategory=findViewById(R.id.tvMealCategory)
        textViewIngredients=findViewById(R.id.tvIngredients)
        textViewInstructions=findViewById(R.id.tvInstructions)


    }
    private fun fetchJson()
    {
        AndroidNetworking.get("https://www.themealdb.com/api/json/v1/1/lookup.php?i=$idMeal")
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {

                    val playerArray = response?.getJSONArray("meals")
                    if (playerArray != null) {
                        for (i in 0 until playerArray.length()) {

                            val temp = playerArray.getJSONObject(i)
                            val strMeal=temp.getString("strMeal")
                            val strCategory = temp.getString("strCategory")
                            val strArea = temp.getString("strArea")
                            val strYoutube = temp.getString("strYoutube")
                            val strMealThumb = temp.getString("strMealThumb")
                            val strInstructions = temp.getString("strInstructions")
                            Picasso.get().load(strMealThumb).into(imageView)
                            textViewStrMeal?.text=strMeal
                            textViewCategory?.text="$strCategory | $strArea"
                            textViewInstructions?.text=strInstructions

                           // val meal = Meal(strMeal,strMealThumb,idMeal)

                            for (n in 1 .. 20){
                                val ingredient = temp.getString("strIngredient$n")
                                val measure = temp.getString("strMeasure$n")
                                if (ingredient.trim() != "" && ingredient.trim() != "null") textViewIngredients?.append("\n \u2022 $ingredient")
                                if (measure.trim() != "" && measure.trim() != "null") textViewIngredients?.append(" : $measure")
                            }




                        }
                    }


                }

                override fun onError(anError: ANError?) {
                    displayToast("Meals :Response Failure")
                }
            })

    }
    private fun displayToast(message: String) {
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }

}