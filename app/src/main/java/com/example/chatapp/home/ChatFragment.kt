package com.example.chatapp.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.adapters.UserAdaptor
import com.example.chatapp.model.UserDetails
import com.google.firebase.firestore.*

class ChatFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<UserDetails>
    private lateinit var adapter: UserAdaptor
    private lateinit var db: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view =  inflater.inflate(R.layout.fragment_chat, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        userArrayList = arrayListOf<UserDetails>()
        adapter = UserAdaptor(userArrayList)
        recyclerView.adapter = adapter
        getUserdata()
        return view
    }

    private fun getUserdata() {
        db = FirebaseFirestore.getInstance()
        db.collection("users").addSnapshotListener(object: EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
               if(error != null) {
                   Log.d("FirebaseError",error.message.toString())
                   return
               }

                for( dc: DocumentChange in value?.documentChanges!!) {
                    if(dc.type == DocumentChange.Type.ADDED) {
                        userArrayList.add(dc.document.toObject(UserDetails::class.java))
                        Log.d("USER",userArrayList.toString())

                    }
                }
                adapter.notifyDataSetChanged()
            }

        })
    }
}