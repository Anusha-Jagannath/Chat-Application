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
import com.hbb20.CountryCodePicker

class LoginFragment : Fragment(R.layout.login_fragment){
    private lateinit var nextButton: Button
    private lateinit var phoneNoInput: EditText
    private lateinit var phoneNo: String
    private lateinit var code: String
    private lateinit var countryCode: CountryCodePicker
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.login_fragment, container, false)
        nextButton = view.findViewById(R.id.sendButton)
        phoneNoInput = view.findViewById(R.id.phoneLoginEditText)
        countryCode = view.findViewById(R.id.countryCode)

        nextButton.setOnClickListener {
            Log.d("Next", "Button clicked")
            code = countryCode.selectedCountryCodeWithPlus
            phoneNo = code + phoneNoInput.text.toString()
            if (Validator.validatePhone(phoneNoInput)) {
                val bundle = Bundle()
                bundle.putString(Constants.PHONE_NUMBER,phoneNo)
                val authFragment = AuthenticationFragment()
                authFragment.arguments = bundle
                requireActivity().supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragmentContainer, authFragment)
                    commit()

                }
            }
        }
        return view
    }
}