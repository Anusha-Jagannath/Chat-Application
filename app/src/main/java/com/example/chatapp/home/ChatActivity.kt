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
    private lateinit var progress: ProgressBar
    var isLoading = false
    var key: String = ""
    var isScrolling = false
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
        progress = findViewById(R.id.progressPage)
        val name = intent.getStringExtra(Constants.USER_NAME)
        title.setText(name)
        val receiverUid = intent.getStringExtra(Constants.UID)
        val senderUid = AuthenticationService().getUid()
        databaseRef = FirebaseDatabase.getInstance().reference
        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid
        messageList = ArrayList()
        messageAdaptor = ChatAdaptor(this, messageList)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdaptor

        //logic for adding data to recycler view impt
        databaseRef.child(Constants.CHATS).child(senderRoom!!).child(Constants.MESSAGES)
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
            val hour = LocalTime.now().hour
            val min = LocalTime.now().minute
            val time = "$hour:"+"$min"+" pm"
            Log.d("Current time",time)
            val message = messageBox.text.toString()
            if(message.isNotEmpty()) {
                val chat = Chat(message, senderUid,time=time)
                databaseRef.child(Constants.CHATS).child(senderRoom!!).child(Constants.MESSAGES).push()
                    .setValue(chat).addOnSuccessListener {
                        Log.d("data", "added")
                        databaseRef.child(Constants.CHATS).child(receiverRoom!!).child(Constants.MESSAGES).push()
                            .setValue(chat)
                    }
                messageBox.setText("")
            }
            else if (::downloadUrl.isInitialized) {
                val chat = Chat(message = "photo",senderId = senderUid,imageUrl = downloadUrl,time=time)
                databaseRef.child(Constants.CHATS).child(senderRoom!!).child(Constants.MESSAGES).push()
                    .setValue(chat).addOnSuccessListener {
                        Log.d("image data", "added")
                        databaseRef.child(Constants.CHATS).child(receiverRoom!!).child(Constants.MESSAGES).push()
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

        chatRecyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.d("SCROLL","check")
                isScrolling = true
            }
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                Log.i("working","increment page test")
                val visibleItemCount = (chatRecyclerView.layoutManager as LinearLayoutManager).childCount
                val pastVisibleItem = (chatRecyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                val total = messageAdaptor.itemCount
                Log.d("visibleItemCount",visibleItemCount.toString())
                Log.d("pastVisibleItemCount",pastVisibleItem.toString())
                Log.d("Total",total.toString())

                if(total < pastVisibleItem+visibleItemCount && isScrolling) {
                    progress.visibility = View.VISIBLE
                    Log.d("Paginate",pastVisibleItem.toString())
                    if(total == pastVisibleItem+1) {
                        Log.d("PAST",pastVisibleItem.toString())
                        //isScrolling = false
                        progress.visibility = View.GONE
                    }
                }
            }
        })

        back.setOnClickListener {
            gotoHomeActivity()
        }
        browse.setOnClickListener {
            var intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, 100)
        }
    }

    private fun loadChats() {
        Log.d("set progress visibility", "visible")
        progress.visibility = View.VISIBLE
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
            val uid = AuthenticationService().getUid()
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