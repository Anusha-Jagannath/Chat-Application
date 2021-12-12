package com.example.chatapp.networking


import com.example.chatapp.networking.wrappers.PushMessage
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class PushNotificationSenderService {
    suspend fun sendPushNotification(message: PushMessage): PushResponse {
        val api = Retrofit.Builder().baseUrl("https://fcm.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create<PushNotificationApi>()

        return api.sendPushNotification(
            "key=AAAA5hLbtes:APA91bH4TmUDPaDooXstsiX-nfjZ-T7_u5CcR8pj5yP6Ch7G2uH0QVHvmCFMHKYkbZTtsHIH7mM-m8DkrBH2umV4htufhcGrEqcoaldV1R3LO5wKhuAbxOXPc6tBWqbZDsPGDjFufICG",
            message
        )
    }
}