package com.example.chatapp.networking.wrappers

data class PushMessage(
    val to:String,
    val notification: PushContent
)

data class PushContent(
    val body: String,
    val title: String
)
