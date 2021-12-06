package com.example.chatapp.service

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage

class Storage {

    fun uploadImage(uid: String,uri: Uri) {
        val reference = FirebaseStorage.getInstance().reference
        val fileReference = reference.child("user/"+uid+"jpg")
        fileReference.putFile(uri).addOnSuccessListener {
            Log.d("Storage","Image uploaded")
            Log.d("Image",it.task.result.toString())
            Log.d("Image",uri.toString())
        }
    }


    fun retrieveImage(uid: String,listener:(Boolean, Bitmap) -> Unit) {
        val reference = FirebaseStorage.getInstance().reference
        val fileReference = reference.child("user/"+uid+"jpg")
        fileReference.getBytes(5000000).addOnSuccessListener {
            Log.d("Storage","Image downloaded")
            val bitmap = BitmapFactory.decodeByteArray(it,0,it.size)
            listener(true,bitmap)

        }
    }
}