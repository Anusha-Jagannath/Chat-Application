package com.example.chatapp.service

import android.util.Log
import com.example.chatapp.Constants
import com.example.chatapp.networking.PushNotificationSenderService
import com.example.chatapp.networking.wrappers.PushContent
import com.example.chatapp.networking.wrappers.PushMessage

class Notification {
    private val pushNotificationSenderService = PushNotificationSenderService()
    suspend fun notification(senderId: String, receiverId: String, message: String,messageToken: String) {
        if (messageToken.equals(Constants.DEVICE_EMULATOR1)) {
            val pushMessage = PushMessage(
                to = "dDivonp2S8ilbPzkFb8cDH:APA91bF0bK80qApTC51Y2EgTXLZCq_NFbaChkawbV9V4uwsT1LdnstXxNDfIjDvJrCoGxWZJxYwt7-r0CF_uUdNrMToX9uXTaoosBdQtmWVvdGZ0xAR01twYbwRHFP6iYtnP1-2Llf-b",
                notification = PushContent(body = message, title = message)
            )
            pushNotificationSenderService.sendPushNotification(pushMessage)
        }
        else if(messageToken.equals(Constants.DEVICE_EMULATOR2)) {
            val pushMessage = PushMessage(
                to = "dFG-9uQdTg6kclTJd2pvZX:APA91bEeq7qAj7dVwyB5Ypig3mymbvMh4wDHEHJkXEuH_ky94S2fdgcsgCTau-KZrNRMnh0OanFWms_F0BVkXn7UbKiPwCkvh5iCR_sqA9tZi_3NdzC5LJt6ud6pp7ThrkpVLeNMCaor",
                notification = PushContent(body = message, title = message)
            )
            pushNotificationSenderService.sendPushNotification(pushMessage)
        }
    }
}