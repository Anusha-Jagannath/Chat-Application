package com.example.chatapp.model

data class UserDetails(
    val userId: String? = null,
    val userName: String? = null,
    val status: String ?= null,
    val downloadUrl: String? = null,
    val firebaseMessagingToken: String?=null
) {
}