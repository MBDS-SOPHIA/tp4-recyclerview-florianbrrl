package com.openclassrooms.magicgithub.ui.user_list

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.openclassrooms.magicgithub.databinding.ItemListUserBinding
import com.openclassrooms.magicgithub.model.User

class ListUserViewHolder(private val binding: ItemListUserBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User, callback: UserListAdapter.Listener) {
        Glide.with(itemView.context)
            .load(user.avatarUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(binding.itemListUserAvatar)

        binding.itemListUserUsername.text = user.login
        binding.itemListUserDeleteButton.setOnClickListener {
            callback.onClickDelete(user)
        }

        // Mettre à jour l'apparence selon l'état
        itemView.setBackgroundColor(
            ContextCompat.getColor(
                itemView.context,
                if (user.isActive) android.R.color.white
                else android.R.color.holo_red_light
            )
        )

        // Effet visuel pour l'état inactif
        binding.apply {
            itemListUserUsername.alpha = if (user.isActive) 1.0f else 0.5f
            itemListUserAvatar.alpha = if (user.isActive) 1.0f else 0.5f
        }
    }
}