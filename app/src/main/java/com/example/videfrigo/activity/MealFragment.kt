package com.example.videfrigo.activity

import MealAdapter
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.videfrigo.DetailActivity
import com.example.videfrigo.R
import com.example.videfrigo.model.Meal
import org.json.JSONObject

private const val ARG_PARAM1 = "param1"

class MealFragment : Fragment(),MealAdapter.onSelectItem{

    private var param1: String? = null
    // getting the recyclerview by its id
    private var recyclerview : RecyclerView?= null
    private var listMeals= ArrayList<Meal>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout(view)
        fetchJson()
    }

    private fun initLayout(view:View)
    {
        recyclerview=view.findViewById(R.id.rvMeal)

        val mLayoutManager =  GridLayoutManager(activity, 2, RecyclerView.VERTICAL, false)
        recyclerview?.layoutManager = mLayoutManager
        recyclerview?.setHasFixedSize(true)


    }

    private fun loadRecycleView()
    {

        val mealAdapter = MealAdapter(listMeals,this)
        recyclerview!!.adapter =mealAdapter

    }
    private fun fetchJson()
    {
        AndroidNetworking.get("https://www.themealdb.com/api/json/v1/1/filter.php?c=$param1")
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {


                    val playerArray = response?.getJSONArray("meals")
                    if (playerArray != null) {
                        for (i in 0 until playerArray.length()) {

                            val temp = playerArray.getJSONObject(i)
                            val strMeal=temp.getString("strMeal")
                            val strMealThumb = temp.getString("strMealThumb")
                            val idMeal = temp.getString("idMeal")
                            val meal = Meal(strMeal,strMealThumb,idMeal)

                            listMeals.add(meal)
                            loadRecycleView()

                        }
                    }


                }

                override fun onError(anError: ANError?) {
                    displayToast("Meals :Response Failure")
                }
            })

    }


    private  fun loadFragment(fragment: Fragment)
    {
        val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.frame_layout,fragment)
        fragmentTransaction?.commit()
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String?) =
            MealFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)

                }
            }
    }


    private fun displayToast(message: String) {
    Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
}

    override fun onSelected(meal: Meal) {

        val intent = Intent(activity,DetailActivity::class.java )
        intent.putExtra("detailRecipe", meal.idMeal)
        startActivity(intent)
    }


}