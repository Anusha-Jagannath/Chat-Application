package com.example.chatapp.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.adapters.ChatAdaptor
import com.example.chatapp.model.Chat
import com.example.chatapp.model.UserDetails
import com.example.chatapp.service.AuthenticationService
import com.google.firebase.database.*
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_group.*

class GroupActivity : AppCompatActivity() {
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var userArrayList: ArrayList<UserDetails>
    private lateinit var db: FirebaseFirestore

    private lateinit var messageAdaptor: ChatAdaptor
    private lateinit var messageList: ArrayList<Chat>

    var receiverRoom: String? = null
    var senderRoom: String? = null
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)
        chatRecyclerView = findViewById(R.id.groupRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sentButton)
        userArrayList = arrayListOf<UserDetails>()

        databaseReference = FirebaseDatabase.getInstance().getReference()
        messageList = ArrayList()
        messageAdaptor = ChatAdaptor(this, messageList)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdaptor

        val groupName = intent.getStringExtra("groupName")
        setSupportActionBar(toolbar3)
        supportActionBar?.title = groupName
        getUserdata()

        val senderUid = AuthenticationService().getUid()
        //senderRoom = userArrayList.get(0).userId + senderUid
        //receiverRoom = senderUid + userArrayList.get(0).userId

        sendButton.setOnClickListener {
            senderRoom = userArrayList.get(0).userId + senderUid
            receiverRoom = senderUid + userArrayList.get(0).userId

            val message = messageBox.text.toString()
            val messageObject = Chat(message, senderUid)
            databaseReference.child("GroupChats").child(senderRoom!!).child("GroupMessages")
                .push().setValue(messageObject).addOnSuccessListener {
                    databaseReference.child("GroupChats").child(receiverRoom!!).child("GroupMessages")
                        .push().setValue(messageObject)
                }
            messageBox.setText(" ")
            getAllMessages()
        }

    }

    private fun getAllMessages() {
        databaseReference.child("GroupChats").child(senderRoom!!).child("GroupMessages")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for(postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Chat::class.java)
                        messageList.add(message!!)
                    }
                    messageAdaptor.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun getUserdata() {
        db = FirebaseFirestore.getInstance()
        db.collection("users").addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    Log.d("FirebaseError", error.message.toString())
                    return
                }

                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val user = dc.document.toObject(UserDetails::class.java)
                        if (AuthenticationService().getUid() != user.userId)
                            userArrayList.add(user)
                        Log.d("GROUP USERS", userArrayList.toString())

                    }
                }
            }

        })
    }
}