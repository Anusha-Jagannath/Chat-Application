package com.example.chatapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.chatapp.Constants
import com.example.chatapp.R
import com.example.chatapp.home.HomeActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.authentication_fragment.*
import java.util.concurrent.TimeUnit

class AuthenticationActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var phoneNo: String
    private lateinit var phoneText: TextView
    private lateinit var otpText: EditText
    private lateinit var submit: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        phoneText = findViewById(R.id.textViewPhone)
        otpText = findViewById(R.id.otp)
        submit = findViewById(R.id.submitButton)
        phoneNo = intent.getStringExtra(Constants.PHONE_NUMBER).toString()
        val data = phoneNo
        Log.d("Phone", "$phoneNo")
        phoneText.setText("Verify ${data.toString()}")
        Toast.makeText(this, "$data", Toast.LENGTH_SHORT).show()
        init()
    }
    private fun init() {
        submit.setOnClickListener {
            loading.visibility = View.VISIBLE
            val credential =
                PhoneAuthProvider.getCredential(storedVerificationId!!, otpText.text.toString())
            signInWithAuth(credential)
            //gotoHomeActivity()
        }
        auth = Firebase.auth
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(
                verificationId: String,
                p1: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, p1)
                Log.d("OTP", "sent successfuly")
                Log.d("OTP", verificationId)
                storedVerificationId = verificationId
            }

            override fun onCodeAutoRetrievalTimeOut(p0: String) {
                super.onCodeAutoRetrievalTimeOut(p0)
                loading.visibility = View.GONE
            }

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                loading.visibility = View.GONE
                val smsCode = credential.smsCode
                Log.d("Otp", "verification complete")
                otpText.setText(smsCode)
                signInWithAuth(credential)

            }

            override fun onVerificationFailed(p0: FirebaseException) {
                loading.visibility = View.GONE
                Log.w("Failed", "onVerificationFailed", p0)

                if (p0 is FirebaseAuthInvalidCredentialsException) {

                } else if (p0 is FirebaseTooManyRequestsException) {

                }
            }
        }
        startPhoneNumberVerification("+918296846052")
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        Log.d("verify", "phone")
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithAuth(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("Sign","success")
                    if(it.result?.additionalUserInfo?.isNewUser == true) {
                        gotoLoginActivity()
                    }
                    else {
                        gotoHomeActivity()
                    }
                }
                else {
                    Log.d("Sign","Phone number verification failed")
                }
            }
    }

    private fun gotoHomeActivity() {
        var intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
    private fun gotoLoginActivity() {
        var intent = Intent(this,LoginActivity::class.java)
       startActivity(intent)
    }
}
