package com.example.videfrigo.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.videfrigo.R
import com.example.videfrigo.adapter.CategoryAdapter
import com.example.videfrigo.model.Category
import org.json.JSONObject





class CategoryFragment : Fragment(),CategoryAdapter.onSelectData {

    // getting the recyclerview by its id
    private var recyclerview :RecyclerView ?= null
    private val listCategories= ArrayList<Category>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        initLayout(view)
        fetchJson()

    }

    private fun initLayout(view:View)
    {
        recyclerview=view.findViewById(R.id.rvCategory)

        val mLayoutManager = GridLayoutManager(activity, 2, RecyclerView.VERTICAL, false)
        recyclerview?.layoutManager = mLayoutManager
        recyclerview?.setHasFixedSize(true)



    }

    private fun loadRecycleView()
    {
        val categoryAdapter = CategoryAdapter(listCategories,this)
        recyclerview!!.adapter =categoryAdapter

    }

    private fun fetchJson()
    {
        AndroidNetworking.get("https://www.themealdb.com/api/json/v1/1/categories.php")
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {

                    val playerArray = response?.getJSONArray("categories")
                    if (playerArray != null) {
                        for (i in 0 until playerArray.length()) {

                            val temp = playerArray.getJSONObject(i)
                            val category = Category()
                            category.strCategory = temp.getString("strCategory")
                            category.strCategoryThumb = temp.getString("strCategoryThumb")
                            category.strCategoryDescription = temp.getString("strCategoryDescription")
                            listCategories.add(category)
                            loadRecycleView()

                        }
                    }

                    displayToast("Response Successful")
                }

                override fun onError(anError: ANError?) {
                    displayToast("Response Failure")
                }
            })

    }

    private  fun loadFragment(fragment: Fragment)
    {
        val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.frame_layout,fragment)
        fragmentTransaction?.commit()
    }
    private fun displayToast(message: String) {
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
    }

    override fun onSelected(category: Category) {


        loadFragment(MealFragment.newInstance(category?.strCategory))
    }


}