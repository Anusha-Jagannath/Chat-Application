package com.example.chatapp.service

import android.util.Log
import com.example.chatapp.Validator
import com.example.chatapp.model.Chat
import com.example.chatapp.model.Group
import com.example.chatapp.model.UserDetails
import com.google.firebase.firestore.*

class Database {

    fun saveUserData(name: String, status: String, downloadUrl: String) {
        val userId = AuthenticationService().getUid()
        var user = UserDetails(userId, name, status, downloadUrl)
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId).set(user).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("Database", "added user information")
            } else {
                Log.d("Database", "not added user information")
            }
        }

    }

    fun getUser(fUid: String): UserDetails {
        val db = FirebaseFirestore.getInstance()
        var user = UserDetails("", "", "", "")
        db.collection("users").document(fUid).get().addOnCompleteListener { status ->
            if (status.isSuccessful) {
                status.result?.also {
                    Log.d("F", it.data.toString())
                    var userDb: UserDetails = Validator.createUserFromHashMap(
                        it.data as HashMap<*, *>
                    )
                    user = UserDetails(
                        userDb.userId,
                        userDb.userName,
                        userDb.status,
                        userDb.downloadUrl
                    )
                    Log.d("DATA", user.toString())
                }
            }

        }
        return user
    }

    fun saveGroup(name: String) {
        val userId = AuthenticationService().getUid()
        val group = Group(userId, name)
        val db = FirebaseFirestore.getInstance()
        db.collection("group").document(userId).set(group).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("Database", "added group information")
            } else {
                Log.d("Database", "not added group information")
            }
        }
    }


    fun saveChat(message: String, senderUid: String, senderRoom: String) {
        val db = FirebaseFirestore.getInstance()
        val chat = Chat(message, senderUid)
        db.collection("chats").document(senderRoom).set(chat).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("Database", "added chat information")
            } else {
                Log.d("Database", "not added chat information")
            }
        }
    }

    fun getChats(): ArrayList<Chat> {
        val chatArrayList: ArrayList<Chat> = arrayListOf()
        val db = FirebaseFirestore.getInstance()
        db.collection("chats").addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    Log.d("FirebaseError", error.message.toString())
                    return
                }

                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val chat = dc.document.toObject(Chat::class.java)
                        chatArrayList.add(chat)
                    }
                }
            }

        })
        return chatArrayList
    }

    fun saveGroupChat(message: String, senderUid: String, senderRoom: String) {
        val db = FirebaseFirestore.getInstance()
        val chat = Chat(message, senderUid)
        db.collection("groupChats").document(senderRoom).set(chat).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("Database", "added chat information")
            } else {
                Log.d("Database", "not added chat information")
            }
        }
    }

    fun getGroupChats(): ArrayList<Chat> {
        val chatArrayList: ArrayList<Chat> = arrayListOf()
        val db = FirebaseFirestore.getInstance()
        db.collection("groupChats").addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    Log.d("FirebaseError", error.message.toString())
                    return
                }

                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val chat = dc.document.toObject(Chat::class.java)
                        chatArrayList.add(chat)
                    }
                }
            }

        })
        return chatArrayList
    }
}