package com.example.chatapp.language

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import com.example.chatapp.Constants
import com.example.chatapp.R
import com.example.chatapp.home.HomeActivity
import kotlinx.android.synthetic.main.activity_language.*
import java.util.*

class LanguageActivity : AppCompatActivity() {
    private lateinit var back: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language)
        back = findViewById(R.id.backToHome)
        loadLocale()

        back.setOnClickListener {
            gotoHomeActivity()
        }
        radioGroup.setOnCheckedChangeListener { radioGroup, i ->
            var choice = findViewById<RadioButton>(i)
            if(choice != null) {
                Log.d("Language",choice.text.toString())
                val sharedPreferences = getSharedPreferences(Constants.LANG_PREFS, Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString(Constants.LANGUAGE, choice.text.toString())
                editor.apply()

                if(choice.text.toString() == "English") {
                    setLocale("en")
                    recreate()

                }
                else if(choice.text.toString() == "Hindi") {
                    Log.d("Hindi","hi")
                    setLocale("hi")
                    recreate()
                }
            }
        }
    }

    private fun setLocale(lang: String) {
        var locale = Locale(lang)
        Locale.setDefault(locale)
        var config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config,baseContext.resources.displayMetrics)

        val sharedPreferences = getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(Constants.LANG, lang)
        editor.apply()
    }

    fun loadLocale() {
        val sharedPreferences = getSharedPreferences(Constants.SETTINGS, MODE_PRIVATE)
        val language = sharedPreferences.getString(Constants.LANG, "")
        if (language != null) {
            Log.d("HOME LANGUAGE",language)
        }

        if (language != null) {
            setLocale(language)
        }
    }

    private fun gotoHomeActivity() {
       var intent = Intent(this,HomeActivity::class.java)
        startActivity(intent)
    }
}