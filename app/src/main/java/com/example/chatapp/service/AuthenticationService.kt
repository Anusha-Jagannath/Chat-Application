package com.example.chatapp.service

import com.google.firebase.auth.FirebaseAuth

class AuthenticationService {

    fun getUid()  = FirebaseAuth.getInstance().currentUser!!.uid

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }

}