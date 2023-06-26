package com.sagauxassignment.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.sagauxassignment.databinding.ItemUserBinding
import com.sagauxassignment.domain.Users

class UserListAdapter(private val itemCallback: ((Users.ApiUser) -> Unit)? = null) :
    RecyclerView.Adapter<UserListAdapter.MyViewHolder>() {

    private var userList: List<Users.ApiUser>? = null

    fun setUserList(userList: List<Users.ApiUser>?) {
        this.userList = userList
    }

    inner class MyViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: Users.ApiUser?) {
            user?.let {
                binding.run {
                    ivUserProfile.load(it.profileUrl)
                    tvUserId.text = it.id
                    tvUserName.text = it.name
                    tvUserEmail.text = it.email
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemUserBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun getItemCount(): Int {
        return userList?.size ?: 0
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(user = userList?.get(position))
        holder.itemView.setOnClickListener {
            userList?.get(position)?.let {
                itemCallback?.invoke(it)
            }
        }
    }
}