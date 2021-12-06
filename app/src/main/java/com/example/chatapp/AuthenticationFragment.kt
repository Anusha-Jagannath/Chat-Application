package com.example.chatapp

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.chatapp.home.HomeActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.authentication_fragment.*
import java.util.concurrent.TimeUnit

class AuthenticationFragment : Fragment(R.layout.authentication_fragment) {

    private lateinit var auth: FirebaseAuth
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var phoneNo: String
    private lateinit var phoneText: TextView
    private lateinit var otpText: EditText
    private lateinit var submit: Button
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.authentication_fragment, container, false)
        phoneText = view.findViewById(R.id.textViewPhone)
        otpText = view.findViewById(R.id.otp)
        submit = view.findViewById(R.id.submitButton)
        val args = this.arguments
        val data = args?.get(Constants.PHONE_NUMBER)
        phoneNo = data.toString()
        Log.d("Phone", "$phoneNo")
        phoneText.setText("Verify ${data.toString()}")
        Toast.makeText(requireContext(), "$data", Toast.LENGTH_SHORT).show()
        init()
        return view
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
                    Toast.makeText(requireContext(), "Invalid phone no", Toast.LENGTH_SHORT).show()
                } else if (p0 is FirebaseTooManyRequestsException) {
                    Toast.makeText(requireContext(), "Invalid phone no", Toast.LENGTH_SHORT).show()
                }
            }
        }
        startPhoneNumberVerification(phoneNo)
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        Log.d("verify", "phone")
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())                 // Activity (for callback binding)
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
        Log.d("Tag","I am here")
        var intent = Intent(requireContext(), HomeActivity::class.java)
        startActivity(intent)
    }

    private fun gotoLoginActivity() {
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, LoginFragment())
            commit()
        }
    }

}