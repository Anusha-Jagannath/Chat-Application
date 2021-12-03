package com.example.chatapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class LoginFragment : Fragment(R.layout.login_fragment){
    private lateinit var nextButton: Button
    private lateinit var phoneNoInput: EditText
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.login_fragment, container, false)
        nextButton = view.findViewById(R.id.nextButton)
        phoneNoInput = view.findViewById(R.id.phoneNumber)
        nextButton.setOnClickListener {
            Log.d("Next", "Button clicked")
            if (Validator.validatePhone(phoneNoInput)) {
                requireActivity().supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragmentContainer, AuthenticationFragment())
                    commit()
                }
            }
        }
        return view
    }
}