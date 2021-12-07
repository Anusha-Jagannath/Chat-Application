package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.chatapp.home.HomeActivity
import com.example.chatapp.service.AuthenticationService
import com.example.chatapp.ui.LoginActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        if (AuthenticationService().getUid() != null) {
//            gotoHomeActivity()
//        } else {
//            replaceFragment(LoginFragment())
//        }
       replaceFragment(LoginFragment())
    }

    private fun gotoHomeActivity() {
        var intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun replaceFragment(fragment: Fragment) {
       val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer,fragment)
        fragmentTransaction.commit()
    }
}