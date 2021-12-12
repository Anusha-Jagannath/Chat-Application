package com.example.chatapp.home

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.chatapp.Constants
import com.example.chatapp.R
import com.example.chatapp.Validator
import com.example.chatapp.model.UserDetails
import com.example.chatapp.service.AuthenticationService
import com.example.chatapp.service.Database
import com.example.chatapp.service.Storage
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

class ProfileActivity : AppCompatActivity() {
    private lateinit var back: ImageView
    private lateinit var profileImage: ImageView
    private lateinit var nameInput: EditText
    private lateinit var statusInput: EditText
    private lateinit var submit: Button
    lateinit var downloadUrl: String
    lateinit var imageUri: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        back = findViewById(R.id.back)
        profileImage = findViewById(R.id.profile)
        nameInput = findViewById(R.id.groupName)
        statusInput = findViewById(R.id.editTextStatus)
        submit = findViewById(R.id.save)
        back.setOnClickListener {
            gotoHomeActivity()
        }
        profileImage.setOnClickListener {
            var intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, 100)
        }
        submit.setOnClickListener {
            Toast.makeText(this, "submit clicked", Toast.LENGTH_SHORT).show()
            var name = nameInput.text.toString()
            var status = statusInput.text.toString()
            if (!::downloadUrl.isInitialized) {
                Toast.makeText(this, "Image cannot be empty", Toast.LENGTH_SHORT).show()
            } else if (name.isNotEmpty() || status.isNotEmpty()) {
                var database = Database()
                val sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE)
                val messageToken = sharedPreferences.getString("notification_token", "")
                if (messageToken != null) {
                    Log.d("Token", messageToken)
                }

                database.saveUserData(
                    name,
                    status,
                    downloadUrl,
                    "dDivonp2S8ilbPzkFb8cDH:APA91bF0bK80qApTC51Y2EgTXLZCq_NFbaChkawbV9V4uwsT1LdnstXxNDfIjDvJrCoGxWZJxYwt7-r0CF_uUdNrMToX9uXTaoosBdQtmWVvdGZ0xAR01twYbwRHFP6iYtnP1-2Llf-b\n"
                )

            } else {
                Toast.makeText(
                    this,
                    "name and staus fields should not be empty",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        displayIcon()
        var fUid = AuthenticationService().getUid()
        getUser(fUid)
        //displayProfile()
    }

    private fun displayProfile() {
        var database = Database()
        var fUid = AuthenticationService().getUid()
        var user = database.getUser(fUid)
        Log.d("profile", user.toString())
        nameInput.setText(user.userName)
        statusInput.setText(user.status)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            imageUri = data?.data!!
            val storage = Storage()
            val uid = AuthenticationService().getUid()
            Log.d("UID", uid)
            uploadImage(uid, imageUri)
        }
    }

    private fun displayIcon() {
        val storage = Storage()
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        storage.retrieveImage(uid) { status, image ->
            if (status) {
                profileImage.setImageBitmap(image)
            }
        }
    }

    private fun gotoHomeActivity() {
        var intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    fun getUser(fUid: String) {
        val db = FirebaseFirestore.getInstance()
        var user = UserDetails("", "", "", " ","")
        db.collection("users").document(fUid).get().addOnCompleteListener { status ->
            if (status.isSuccessful) {
                status.result?.also {
                    Log.d("F", it.data.toString())
                    var userDb: UserDetails = Validator.createUserFromHashMap(
                        it.data as HashMap<*, *>
                    )
                    user = UserDetails(
                        userDb.userId,
                        userDb.userName,
                        userDb.status,
                        userDb.downloadUrl,
                        userDb.firebaseMessagingToken
                    )
                    Log.d("DATA", user.toString())
                    nameInput.setText(user.userName)
                    statusInput.setText(user.status)
                }
            }

        }

    }

    fun uploadImage(uid: String, image: Uri) {

        val reference = FirebaseStorage.getInstance().reference.child("user/" + uid + "jpg")
        var uploadTask = reference.putFile(image)
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                Log.d("Error uploading file", task.exception.toString())
            }
            return@Continuation reference.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Uploaded", task.result.toString())
                downloadUrl = task.result.toString()
                Glide.with(this).load(downloadUrl).into(profileImage)
            }
        }
    }
}