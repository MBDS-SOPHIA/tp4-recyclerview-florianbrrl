package com.openclassrooms.magicgithub.ui.user_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.magicgithub.databinding.ItemListUserBinding
import com.openclassrooms.magicgithub.model.User
import com.openclassrooms.magicgithub.utils.UserDiffCallback

class UserListAdapter(private val callback: Listener) :
    RecyclerView.Adapter<ListUserViewHolder>() {

    private var users: MutableList<User> = ArrayList()  // Changé en MutableList

    interface Listener {
        fun onClickDelete(user: User)
        fun onMoveUser(fromPosition: Int, toPosition: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListUserViewHolder {
        val binding = ItemListUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ListUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListUserViewHolder, position: Int) {
        holder.bind(users[position], callback)
    }

    override fun getItemCount(): Int = users.size

    fun toggleUserActiveState(position: Int) {
        if (position in users.indices) {
            (users as? MutableList)?.get(position)?.let { user ->
                user.isActive = !user.isActive
                notifyItemChanged(position)
            }
        }
    }

    fun updateList(newList: List<User>) {
        val diffResult = DiffUtil.calculateDiff(UserDiffCallback(newList, users))
        users = newList.toMutableList()  // Convertir en MutableList
        diffResult.dispatchUpdatesTo(this)
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        val user = users.removeAt(fromPosition)
        users.add(toPosition, user)
        notifyItemMoved(fromPosition, toPosition)
    }
}