package com.example.chatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.model.UserDetails
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso

class UserAdaptor(private val userList: ArrayList<UserDetails>): RecyclerView.Adapter<UserAdaptor.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdaptor.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_user,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserAdaptor.MyViewHolder, position: Int) {
       val user: UserDetails = userList[position]
        holder.name.text = user.userName
        holder.userStatus.text = user.status

    }

    override fun getItemCount(): Int {
       return userList.size
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.titleTv)
        val userStatus: TextView = itemView.findViewById(R.id.subTitleTv)
        var imageUrl: ShapeableImageView = itemView.findViewById(R.id.userImgView)

    }
}