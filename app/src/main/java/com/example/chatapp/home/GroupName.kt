package com.example.chatapp.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.example.chatapp.R
import com.example.chatapp.service.Database
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class GroupName : AppCompatActivity() {
    lateinit var groupName: EditText
    private lateinit var back: ImageView
    lateinit var save: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_name)
        groupName = findViewById(R.id.groupName)
        save = findViewById(R.id.saveGroup)
        back = findViewById(R.id.backImage)
        save.setOnClickListener {
            var group = groupName.text.toString()
            var database = Database()
            database.saveGroup(group)
            var intent = Intent(this,SelectUser::class.java)
            startActivity(intent)
        }
        back.setOnClickListener {
            gotoHomeActivity()

        }
    }

    private fun gotoHomeActivity() {
        var intent = Intent(this,HomeActivity::class.java)
        startActivity(intent)
    }

    private fun saveToDB(group: String) {
        var uid = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseDatabase.getInstance().getReference("groups")
            .child(group).setValue("").addOnCompleteListener {
                if(it.isSuccessful) {
                    Log.d("Group","saved")
                }
            }
    }
}