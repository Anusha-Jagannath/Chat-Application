package com.example.chatapp.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.chatapp.LoginFragment
import com.example.chatapp.R
import com.example.chatapp.adapters.ViewPagerAdaptor
import com.example.chatapp.service.AuthenticationService
import com.example.chatapp.viewmodels.HomeViewModel
import com.example.chatapp.viewmodels.HomeViewModelFactory
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var adaptor: ViewPagerAdaptor
    private lateinit var homeViewModel: HomeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        adaptor = ViewPagerAdaptor(supportFragmentManager, lifecycle)
        viewPager.adapter = adaptor
        homeViewModel = ViewModelProvider(this, HomeViewModelFactory())[HomeViewModel::class.java]
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Chats" //set name for the tabs
                }
                1 -> {
                    tab.text = "Group Chats"
                }

            }
        }.attach()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settings) {
            Toast.makeText(this, "settings clicked", Toast.LENGTH_SHORT).show()
            gotoProfileActivity()
        }
        if (item.itemId == R.id.logout) {
            Toast.makeText(this, "logout clicked", Toast.LENGTH_SHORT).show()
            homeViewModel.logout()
            gotoLoginPage()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun gotoLoginPage() {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, LoginFragment())
        fragmentTransaction.commit()

    }

    private fun gotoProfileActivity() {
        var intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }
}