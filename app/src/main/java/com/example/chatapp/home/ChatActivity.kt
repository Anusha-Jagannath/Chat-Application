package com.example.chatapp.home

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.Constants
import com.example.chatapp.R
import com.example.chatapp.adapters.ChatAdaptor
import com.example.chatapp.model.Chat
import com.example.chatapp.service.AuthenticationService
import com.example.chatapp.service.Database
import com.example.chatapp.service.Notification
import com.example.chatapp.service.Storage
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_group.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalTime

class ChatActivity : AppCompatActivity() {
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdaptor: ChatAdaptor
    private lateinit var messageList: ArrayList<Chat>
    private lateinit var databaseRef: DatabaseReference
    private lateinit var browse: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var title: TextView
    private lateinit var back: ImageView
    lateinit var downloadUrl: String
    lateinit var imageUri: Uri
    var receiverRoom: String? = null
    var senderRoom: String? = null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sentButton)
        browse = findViewById(R.id.browse)
        progressBar = findViewById(R.id.progressBar)
        title = findViewById(R.id.titleView)
        back = findViewById(R.id.backImage)
        val name = intent.getStringExtra("name")
        title.setText(name)
        val receiverUid = intent.getStringExtra("uid")
        val senderUid = AuthenticationService().getUid()
        databaseRef = FirebaseDatabase.getInstance().reference
        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid
        messageList = ArrayList()
        messageAdaptor = ChatAdaptor(this, messageList)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdaptor

        //logic for adding data to recycler view
        databaseRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for(postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Chat::class.java)
                        messageList.add(message!!)
                    }
                    messageAdaptor.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        sendButton.setOnClickListener {
            Toast.makeText(this, "send button clicked", Toast.LENGTH_SHORT).show()
            val hour = LocalTime.now().hour
            val min = LocalTime.now().minute
            val time = "$hour:"+"$min"+" am"
            Log.d("Current time",time)
            val message = messageBox.text.toString()
            if(message.isNotEmpty()) {
                val chat = Chat(message, senderUid,time=time)
                databaseRef.child("chats").child(senderRoom!!).child("messages").push()
                    .setValue(chat).addOnSuccessListener {
                        Log.d("data", "added")
                        databaseRef.child("chats").child(receiverRoom!!).child("messages").push()
                            .setValue(chat)
                    }
                messageBox.setText("")
            }
            else if (::downloadUrl.isInitialized) {
                val chat = Chat(message = "photo",senderId = senderUid,imageUrl = downloadUrl,time=time)
                databaseRef.child("chats").child(senderRoom!!).child("messages").push()
                    .setValue(chat).addOnSuccessListener {
                        Log.d("image data", "added")
                        databaseRef.child("chats").child(receiverRoom!!).child("messages").push()
                            .setValue(chat)
                    }
            }
            var notify = Notification()
            val sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE)
            val messageToken = sharedPreferences.getString("notification_token", "")
            Log.d("DEVICE TOKEN TEST",messageToken.toString())
            if (receiverUid != null) {
                CoroutineScope(Dispatchers.Default).launch {
                    notify.notification(senderUid,receiverUid,message,messageToken.toString())
                }

            }

        }
        back.setOnClickListener {
            gotoHomeActivity()
        }
        browse.setOnClickListener {
            Toast.makeText(this,"browse clicked",Toast.LENGTH_SHORT).show()
            var intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, 100)
        }
    }

    private fun gotoHomeActivity() {
        var intent = Intent(this,HomeActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            imageUri = data?.data!!
            Log.d("IMAGE",imageUri.toString())
            progressBar.visibility = View.VISIBLE
            //val storage = Storage()
            val uid = AuthenticationService().getUid()
            //Log.d("UID", uid)
            uploadImage(uid, imageUri)
        }
    }

    fun uploadImage(uid: String, image: Uri) {

        val reference = FirebaseStorage.getInstance().reference.child("userPic/" + uid + "jpg")
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
                progressBar.visibility = View.GONE
            }
        }
    }

}