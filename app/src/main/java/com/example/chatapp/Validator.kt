package com.example.chatapp

import android.widget.EditText

object Validator {
    fun validatePhone(phoneNo: EditText): Boolean {
        val inputPhoneNo = phoneNo.text.toString()
        if (inputPhoneNo.isEmpty() || inputPhoneNo.length != 10) {
            showError(phoneNo, "Your email id not valid")
            return false
        }
        return true
    }

    private fun showError(input: EditText, s: String) {
        input.setError(s)
        input.requestFocus()
    }
}