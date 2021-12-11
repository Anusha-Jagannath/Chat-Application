package com.example.chatapp.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.chatapp.R
import com.example.chatapp.service.Database
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class GroupNameFragment : Fragment() {
    lateinit var groupName: EditText
    lateinit var save: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_group_name, container, false)
        groupName = view.findViewById(R.id.groupName)
        save = view.findViewById(R.id.saveBtn)
        save.setOnClickListener {
            var group = groupName.text.toString()
            var database = Database()
            database.saveGroup(group)
        }
        return view
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