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
import com.example.chatapp.adapters.GroupAdaptor
import com.example.chatapp.model.Group
import com.example.chatapp.model.UserDetails
import com.example.chatapp.service.AuthenticationService
import com.google.firebase.firestore.*

class GroupChatFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var groupArrayList: ArrayList<Group>
    private lateinit var groupAdaptor: GroupAdaptor
    private lateinit var db: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_group_chat, container, false)
        recyclerView = view.findViewById(R.id.groupRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        groupArrayList = arrayListOf()
        groupAdaptor = GroupAdaptor(groupArrayList)
        recyclerView.adapter = groupAdaptor
        eventChangeListener()
        return view
    }

    private fun eventChangeListener() {
        db = FirebaseFirestore.getInstance()
        db.collection("group").addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    Log.d("FirebaseError", error.message.toString())
                    return
                }

                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val user = dc.document.toObject(Group::class.java)
                        groupArrayList.add(user)
                        Log.d("USER", groupArrayList.toString())

                    }
                }
                groupAdaptor.notifyDataSetChanged()
            }

        })
    }

}