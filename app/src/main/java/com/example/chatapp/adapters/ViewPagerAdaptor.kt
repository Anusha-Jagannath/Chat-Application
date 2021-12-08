package com.example.chatapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.chatapp.home.ChatFragment
import com.example.chatapp.home.GroupChatFragment

class ViewPagerAdaptor(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2 //no of fragment we have
    }
    override fun createFragment(position: Int): Fragment {
        //switch between fragments
        return when (position) {
            0 -> {
                ChatFragment()
            }
            1 -> {
                GroupChatFragment()
            }
            else -> {
                Fragment()
            }
        }
    }
}