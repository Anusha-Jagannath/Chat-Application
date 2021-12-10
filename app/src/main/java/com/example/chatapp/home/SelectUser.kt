package com.example.chatapp.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.adapters.UserSelectAdaptor
import com.example.chatapp.model.UserDetails
import com.example.chatapp.service.AuthenticationService
import com.google.firebase.firestore.*

class SelectUser : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<UserDetails>
    private lateinit var adapter: UserSelectAdaptor
    private lateinit var database: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_user)
        recyclerView = findViewById(R.id.selectUserRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        userArrayList = arrayListOf()
        adapter = UserSelectAdaptor(userArrayList)
        recyclerView.adapter = adapter
        getUser()
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