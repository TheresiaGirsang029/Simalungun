package com.dicoding.simalungun


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.dicoding.simalungun.fragments.InfoFragment
import com.dicoding.simalungun.fragments.LibraryFragment
import com.dicoding.simalungun.fragments.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val libraryFragment = LibraryFragment()
    private val searchFragment = SearchFragment()
    private val infoFragment = InfoFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceFragment(libraryFragment)

        val bottomNavigation= findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.ic_library -> replaceFragment(libraryFragment)
                R.id.ic_search -> replaceFragment(searchFragment)
                R.id.ic_info -> replaceFragment(infoFragment)
            }
            true

        }
    }

    private fun replaceFragment(fragment: Fragment){
        if(fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }

    }

}