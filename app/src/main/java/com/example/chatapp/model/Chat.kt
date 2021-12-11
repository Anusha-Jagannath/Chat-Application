package com.example.chatapp.model

data class Chat(
   val message: String?=null,
   val senderId: String?=null,
   val imageUrl: String?=null,
   val time: String?=null,
   val senderName: String?=null
) {
}