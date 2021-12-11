package com.example.chatapp.home

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.Validator
import com.example.chatapp.adapters.ChatAdaptor
import com.example.chatapp.adapters.MessageAdaptor
import com.example.chatapp.model.Chat
import com.example.chatapp.model.UserDetails
import com.example.chatapp.service.AuthenticationService
import com.example.chatapp.service.Database
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_group.*
import java.time.LocalTime

class GroupActivity : AppCompatActivity() {
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var backButton: ImageView
    private lateinit var title: TextView
    private lateinit var userArrayList: ArrayList<UserDetails>
    private lateinit var db: FirebaseFirestore
    private lateinit var messageAdaptor: MessageAdaptor
    private lateinit var messageList: ArrayList<Chat>
    var receiverRoom: String? = null
    var senderRoom: String? = null
    var receiverHash: String? = null
    private lateinit var databaseReference: DatabaseReference
    private lateinit var browse: ImageView
    lateinit var imageUri: Uri
    lateinit var downloadUrl: String
    private lateinit var senderName: String


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)
        chatRecyclerView = findViewById(R.id.groupRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sentButton)
        backButton = findViewById(R.id.backBtn)
        title = findViewById(R.id.header)
        browse = findViewById(R.id.browseGroup)
        userArrayList = arrayListOf<UserDetails>()

        databaseReference = FirebaseDatabase.getInstance().getReference()
        messageList = ArrayList()
        messageAdaptor = MessageAdaptor(this, messageList)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdaptor

        val groupName = intent.getStringExtra("groupName")
        title.setText(groupName)
        getUserdata()

        val senderUid = AuthenticationService().getUid()
        getSender(senderUid)

        sendButton.setOnClickListener {
            senderRoom = userArrayList.get(1).userId + senderUid
            receiverRoom = senderUid + userArrayList.get(1).userId
            receiverHash = senderUid + userArrayList.get(0).userId

            val message = messageBox.text.toString()
            val hour = LocalTime.now().hour
            val min = LocalTime.now().minute
            val time = "$hour:"+"$min"+" am"
            Log.d("Current time",time)
            if(message.isNotEmpty()) {
                val messageObject = Chat(message, senderUid,time=time,senderName = senderName)
                databaseReference.child("GroupChats").child(senderRoom!!).child("GroupMessages")
                    .push().setValue(messageObject).addOnSuccessListener {
                        databaseReference.child("GroupChats").child(receiverRoom!!)
                            .child("GroupMessages")
                            .push().setValue(messageObject).addOnSuccessListener {
                                databaseReference.child("GroupChats").child(receiverHash!!)
                                    .child("GroupMessages").push().setValue(messageObject)
                            }
                    }
                messageBox.setText(" ")
            }
            else if(::downloadUrl.isInitialized) {
                val chat = Chat(message = "photo",senderId = senderUid,imageUrl = downloadUrl,time=time)
                databaseReference.child("GroupChats").child(senderRoom!!).child("GroupMessages")
                    .push().setValue(chat).addOnSuccessListener {
                        databaseReference.child("GroupChats").child(receiverRoom!!)
                            .child("GroupMessages")
                            .push().setValue(chat)
                    }
            }


            getAllMessages()
        }
        browse.setOnClickListener {
            Toast.makeText(this,"browse clicked", Toast.LENGTH_SHORT).show()
            var intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, 100)
        }
        backButton.setOnClickListener {
            var intent = Intent(this,HomeActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            imageUri = data?.data!!
            Log.d("IMAGE",imageUri.toString())
            val uid = AuthenticationService().getUid()
            uploadImage(uid, imageUri)
        }
    }

    private fun getAllMessages() {
        databaseReference.child("GroupChats").child(senderRoom!!).child("GroupMessages")
            .addValueEventListener(object: ValueEventListener{
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
    }

    private fun getUserdata() {
        db = FirebaseFirestore.getInstance()
        db.collection("users").addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    Log.d("FirebaseError", error.message.toString())
                    return
                }

                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val user = dc.document.toObject(UserDetails::class.java)
                        if (AuthenticationService().getUid() != user.userId)
                            userArrayList.add(user)
                        Log.d("GROUP USERS", userArrayList.toString())

                    }
                }
            }

        })
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
            }
        }
    }


    fun getSender(fUid: String) {
        val db = FirebaseFirestore.getInstance()
        var user = UserDetails("", "", "", "","")
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
                    Log.d("SENDER DATA", user.toString())
                    senderName = user.userName.toString()
                    Log.d("SENDER NAME",senderName)

                }
            }

        }
    }

}