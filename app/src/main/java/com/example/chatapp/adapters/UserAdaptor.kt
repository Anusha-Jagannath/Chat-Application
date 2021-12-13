package com.example.chatapp.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.Constants
import com.example.chatapp.R
import com.example.chatapp.home.ChatActivity
import com.example.chatapp.home.UserProfile
import com.example.chatapp.model.UserDetails
import com.example.chatapp.service.AuthenticationService
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdaptor(val context: Context, private val userList: ArrayList<UserDetails>): RecyclerView.Adapter<UserAdaptor.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdaptor.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_user,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserAdaptor.MyViewHolder, position: Int) {
       val user: UserDetails = userList[position]
        holder.name.text = user.userName
        holder.userStatus.text = user.status
        Picasso.get().load(user.downloadUrl).placeholder(R.drawable.black_background).into(holder.imageUrl)
        holder.itemView.setOnClickListener {
            val intent = Intent(context,ChatActivity::class.java)
            intent.putExtra("name",user.userName)
            intent.putExtra("uid",user.userId)
            context.startActivity(intent)
        }
        holder.imageUrl.setOnClickListener {
            Log.d("Image clicked","Image")
            val intent = Intent(context, UserProfile::class.java)
            intent.putExtra(Constants.NAME,user.userName)
            intent.putExtra(Constants.STATUS,user.status)
            intent.putExtra(Constants.IMAGE_URL,user.downloadUrl)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
       return userList.size
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.titleTv)
        val userStatus: TextView = itemView.findViewById(R.id.subTitleTv)
        var imageUrl: CircleImageView = itemView.findViewById(R.id.userImgView)
    }
}