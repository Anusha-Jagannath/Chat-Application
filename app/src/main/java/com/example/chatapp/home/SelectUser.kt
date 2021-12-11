package com.example.chatapp.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.adapters.UserSelectAdaptor
import com.example.chatapp.model.UserDetails
import com.example.chatapp.service.AuthenticationService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.*

class SelectUser : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<UserDetails>
    private lateinit var adapter: UserSelectAdaptor
    private lateinit var database: FirebaseFirestore
    private lateinit var back: ImageView
    private lateinit var saveBtn: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_user)
        back = findViewById(R.id.imageBack)
        saveBtn = findViewById(R.id.finalBtn)
        recyclerView = findViewById(R.id.selectUserRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        userArrayList = arrayListOf()
        adapter = UserSelectAdaptor(userArrayList)
        recyclerView.adapter = adapter
        getUser()
        back.setOnClickListener {
            gotoGroupNameActivity()
        }
        saveBtn.setOnClickListener {
            Toast.makeText(this,"group created successfully",Toast.LENGTH_SHORT).show()
            gotoHomeActivity()
        }
    }

    private fun gotoHomeActivity() {
       var intent = Intent(this,HomeActivity::class.java)
        startActivity(intent)
    }

    private fun gotoGroupNameActivity() {
       var intent = Intent(this,GroupName::class.java)
        startActivity(intent)
    }

    private fun getUser() {
        database = FirebaseFirestore.getInstance()
        database.collection("users").addSnapshotListener(object: EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if(error != null) {
                    Log.d("FirebaseError",error.message.toString())
                    return
                }

                for( dc: DocumentChange in value?.documentChanges!!) {
                    if(dc.type == DocumentChange.Type.ADDED) {
                        val user = dc.document.toObject(UserDetails::class.java)
                        if(AuthenticationService().getUid() != user.userId)
                            userArrayList.add(user)
                        Log.d("USER",userArrayList.toString())
                    }
                }
                adapter.notifyDataSetChanged()
            }

        })
    }
}