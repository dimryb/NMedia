package ru.netology.nmedia.presentation.viewholder

import ru.netology.nmedia.domain.dto.Post

interface OnInteractionListener {
    fun onLike(post: Post)
    fun onShare(post: Post)
    fun onEdit(post: Post)
    fun onRemove(post: Post)
    fun onMedia(post: Post)
    fun onDetails(post: Post)
}