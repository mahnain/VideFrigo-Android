package com.example.videfrigo

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.videfrigo.model.Meal
import com.example.videfrigo.model.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import org.json.JSONObject


class DetailActivity : AppCompatActivity() {

    private var firebaseAuth= FirebaseAuth.getInstance()
    private val db = Firebase.firestore
    private var idMeal: String = "0"
    private var imageView : ImageView?= null
    private var youtubeImgView:ImageView?=null
    private var textViewStrMeal: TextView?=null
    private var textViewCategory: TextView?=null
    private var textViewIngredients: TextView?=null
    private var textViewInstructions: TextView?=null
    private var cbFavorite: CheckBox?=null
    private var isSelected:Boolean =false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        val bundle: Bundle? = intent.extras
        idMeal = bundle?.get("detailRecipe").toString()
        initLayout()
        fetchJson()


    }

    private fun initLayout()
    {

        youtubeImgView =findViewById(R.id.imgYoutube)
        imageView=findViewById(R.id.imgRecipe)
        textViewStrMeal=findViewById(R.id.tvMealname)
        textViewCategory=findViewById(R.id.tvMealCategory)
        textViewIngredients=findViewById(R.id.tvIngredients)
        textViewInstructions=findViewById(R.id.tvInstructions)
        cbFavorite=findViewById(R.id.cbHeart)

        checkFavoritesMeal(idMeal)

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

                            val meal= Meal(strMeal,strMealThumb,idMeal,null)


                            textViewStrMeal?.text=strMeal
                            textViewCategory?.text="$strCategory | $strArea"
                            textViewInstructions?.text=strInstructions

                            youtubeImgView?.setOnClickListener {
                                startYoutubeActivity(strYoutube)
                            }
                            cbFavorite?.setOnCheckedChangeListener { checkBox, isChecked ->


                                if (isChecked) {

                                    if(!isSelected)
                                    {
                                        addRecipeDB(meal)
                                        isSelected=true
                                    }


                                } else {
                                    deleteRecipeDB(meal)
                                    isSelected=false
                                }
                            }


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

    private fun startYoutubeActivity(source:String)
    {
        val intentYoutube = Intent(Intent.ACTION_VIEW)
        intentYoutube.data = Uri.parse(source)
        startActivity(intentYoutube)

    }



    private fun addRecipeDB(meal: Meal)
    {

        meal.idUser=firebaseAuth.currentUser?.uid
        db.collection("Favorites")
            .document("${meal.idMeal}").set(meal)
            .addOnSuccessListener {
                displayToast("meal ${meal.strMeal} added to favorite")
            }.addOnFailureListener {
                displayToast("meal ${meal.strMeal} add failed!!!")
            }
    }
    private fun deleteRecipeDB(meal: Meal)
    {
        db.collection("Favorites").document("${meal.idMeal}")
        .delete()
        .addOnSuccessListener {  displayToast("meal ${meal.strMeal} deleted from favorite") }
        .addOnFailureListener { displayToast("meal ${meal.strMeal} delete failed!!!") }

    }

    private fun checkFavoritesMeal(idMeal:String)
    {
        val msg="FIREBASE"

        db.collection("Favorites")
            .whereEqualTo("idUser", firebaseAuth.currentUser?.uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if(idMeal == document.data.getValue("idMeal").toString())
                    {


                        isSelected=true
                        cbFavorite?.isChecked=true

                    }

                }

            }
            .addOnFailureListener { exception ->
                Log.w(msg, "Error getting documents: ", exception)
            }


    }

}