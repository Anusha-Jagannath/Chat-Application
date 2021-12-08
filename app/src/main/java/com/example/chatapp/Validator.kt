package com.example.chatapp

import android.widget.EditText
import com.example.chatapp.model.UserDetails

object Validator {
    fun validatePhone(phoneNo: EditText): Boolean {
        val inputPhoneNo = phoneNo.text.toString()
        if (inputPhoneNo.isEmpty() || inputPhoneNo.length != 10) {
            showError(phoneNo, "Your phone no is not valid")
            return false
        }
        return true
    }

    private fun showError(input: EditText, s: String) {
        input.setError(s)
        input.requestFocus()
    }

    fun createUserFromHashMap(userMap: HashMap<*, *>): UserDetails {
        return UserDetails(
            userMap["userId"].toString(),
            userMap["userName"].toString(),
            userMap["status"].toString(),
            userMap["downloadUrl"].toString()
        )
    }
}