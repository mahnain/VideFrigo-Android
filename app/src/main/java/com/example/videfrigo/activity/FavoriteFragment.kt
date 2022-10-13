package com.example.videfrigo.activity

import MealAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.videfrigo.DetailActivity
import com.example.videfrigo.R
import com.example.videfrigo.model.Meal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class FavoriteFragment : Fragment(), MealAdapter.onSelectItem{


    private var recyclerview : RecyclerView?= null
    private var listMeals= ArrayList<Meal>()
    override fun onCreate(savedInstanceState: Bundle?) {
        loadFragment(this)
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
      // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFavoritesDB()
        initLayout(view)

    }

    private fun initLayout(view:View)
    {
        recyclerview=view.findViewById(R.id.rvFavorite)
        val mLayoutManager =  GridLayoutManager(activity, 2, RecyclerView.VERTICAL, false)
        recyclerview?.layoutManager = mLayoutManager
        recyclerview?.setHasFixedSize(true)

    }

    private fun loadRecycleView()
    {
        val mealAdapter = MealAdapter(listMeals,this)
        recyclerview!!.adapter =mealAdapter

    }

    private fun loadFavoritesDB()
    {
        val msg="FIREBASE"
        val db = Firebase.firestore
        val firebaseAuth = FirebaseAuth.getInstance()
        db.collection("Favorites")
            .whereEqualTo("idUser", firebaseAuth.currentUser?.uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val idMeal=document.data.getValue("idMeal").toString()
                    val strMeal=document.data.getValue("strMeal").toString()
                    val strMealThumb=document.data.getValue("strMealThumb").toString()
                    val idUser=document.data.getValue("idUser").toString()
                    val meal =Meal(strMeal,strMealThumb,idMeal,idUser)
                    listMeals.add(meal)



                }
                loadRecycleView()
            }
            .addOnFailureListener { exception ->
                Log.w(msg, "Error getting documents: ", exception)
            }
    }


    override fun onSelected(meal: Meal) {
        val intent = Intent(activity, DetailActivity::class.java )
        intent.putExtra("detailRecipe", meal.idMeal)
        startActivity(intent)
    }


    private  fun loadFragment(fragment: Fragment)
    {
        val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.frame_layout,fragment)
        fragmentTransaction?.commit()
    }
}