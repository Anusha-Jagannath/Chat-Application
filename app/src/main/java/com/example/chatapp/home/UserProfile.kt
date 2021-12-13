package com.example.chatapp.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.chatapp.Constants
import com.example.chatapp.R

class UserProfile : AppCompatActivity() {
    private lateinit var back: ImageView
    private lateinit var profile: ImageView
    private lateinit var nameInput: EditText
    private lateinit var statusInput: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        nameInput = findViewById(R.id.name)
        profile = findViewById(R.id.profileImg)
        statusInput = findViewById(R.id.status)
        val name = intent.getStringExtra(Constants.NAME)
        val status = intent.getStringExtra(Constants.STATUS)
        val imageUrl = intent.getStringExtra(Constants.IMAGE_URL)
        Log.d("NAME",name.toString())
        Log.d("STATUS",status.toString())
        Log.d("IMAGEURL",imageUrl.toString())

        nameInput.setText(name)
        statusInput.setText(status)
        Glide.with(this).load(imageUrl).placeholder(R.drawable.ic_baseline_person_24_white).into(profile)

        back = findViewById(R.id.goBack)
        back.setOnClickListener {
            gotoHomeActivity()
        }
    }

    private fun gotoHomeActivity() {
       var intent = Intent(this,HomeActivity::class.java)
        startActivity(intent)
    }
}