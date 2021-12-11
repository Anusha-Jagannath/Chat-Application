package com.example.chatapp.viewmodels

import androidx.lifecycle.ViewModel
import com.example.chatapp.model.Chat
import com.example.chatapp.service.AuthenticationService
import com.example.chatapp.service.Database

class HomeViewModel: ViewModel() {

    fun logout() {
        var authenticationService = AuthenticationService()
        authenticationService.logout()
    }

    fun saveChat(message: String,senderUid: String,senderRoom: String) {
        var database = Database()
        database.saveChat(message,senderUid,senderRoom)
    }

    fun getChats() {
        var database = Database()
        database.getChats()
    }

    fun saveGroupChat(message: String,senderUid: String,senderRoom: String) {
        var database = Database()
        database.saveGroupChat(message,senderUid,senderRoom)
    }

    fun getgroupChats() {
        var database = Database()
        database.getGroupChats()
    }

    fun saveGroup(groupName: String) {
        var database = Database()
        database.saveGroup(groupName)
    }
}