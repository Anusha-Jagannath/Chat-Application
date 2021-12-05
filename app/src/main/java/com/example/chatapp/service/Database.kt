package com.example.chatapp.service

import android.util.Log
import com.example.chatapp.Validator
import com.example.chatapp.model.UserDetails
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

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

}