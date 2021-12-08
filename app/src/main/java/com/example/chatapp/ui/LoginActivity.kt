package com.example.chatapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.example.chatapp.AuthenticationFragment
import com.example.chatapp.Constants
import com.example.chatapp.R
import com.example.chatapp.Validator
import com.hbb20.CountryCodePicker

class LoginActivity : AppCompatActivity() {
    private lateinit var nextButton: Button
    private lateinit var phoneNoInput: EditText
    private lateinit var phoneNo: String
    private lateinit var code: String
    private lateinit var countryCode: CountryCodePicker
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        nextButton = findViewById(R.id.nextButton)
        phoneNoInput = findViewById(R.id.phoneNumber)
        countryCode = findViewById(R.id.countryCode)

        nextButton.setOnClickListener {
            Log.d("Next", "Button clicked")
            code = countryCode.selectedCountryCodeWithPlus
            phoneNo = code + phoneNoInput.text.toString()
            if (Validator.validatePhone(phoneNoInput)) {
                var intent = Intent(this,AuthenticationActivity::class.java)
                intent.putExtra(Constants.PHONE_NUMBER,phoneNo)
                startActivity(intent)
            }
        }
    }
}