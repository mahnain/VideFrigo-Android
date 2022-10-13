package com.example.videfrigo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment

import com.example.videfrigo.activity.*
import com.example.videfrigo.ProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

     private lateinit var  bottomNav :BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        loadFragment(HomeFragment())
        initLayout()
        setListners()

    }


    private fun initLayout()
    {

        bottomNav=findViewById(R.id.bottom_navigation)

    }

    private fun setListners() {

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {

                R.id.home -> loadFragment(HomeFragment())
                R.id.category -> loadFragment(CategoryFragment())
                R.id.favorite -> loadFragment(FavoriteFragment())
                R.id.profils -> loadFragment(ProfilFragment())

                else->{}

            }
            true
        }
    }
    private  fun loadFragment(fragment: Fragment)
    {

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }


    private fun displayToast(message: String) {
        Toast.makeText(this, "Hey, you selected " + message + "!",
            Toast.LENGTH_SHORT).show()
    }




}