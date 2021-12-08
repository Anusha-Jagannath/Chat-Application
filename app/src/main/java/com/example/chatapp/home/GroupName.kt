package com.example.chatapp.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.example.chatapp.R
import com.example.chatapp.service.Database
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class GroupName : AppCompatActivity() {
    lateinit var groupName: EditText
    lateinit var save: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_name)
        groupName = findViewById(R.id.groupName)
        save = findViewById(R.id.saveBtn)
        save.setOnClickListener {
            var group = groupName.text.toString()
            var database = Database()
            //database.saveGroup(group)
            saveToDB(group)
        }
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