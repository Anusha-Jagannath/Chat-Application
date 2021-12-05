package com.example.chatapp.viewmodels

import androidx.lifecycle.ViewModel
import com.example.chatapp.service.AuthenticationService
import com.example.chatapp.service.Database

class HomeViewModel: ViewModel() {

    fun logout() {
        var authenticationService = AuthenticationService()
        authenticationService.logout()
    }
}