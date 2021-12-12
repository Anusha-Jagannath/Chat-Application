package com.example.chatapp

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class ChatApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("Notification", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = token.toString()
            Log.d("Notification", msg)
            val sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("notification_token",msg)
            editor.apply()
            Log.d("DEVICE",msg)
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })
    }
}