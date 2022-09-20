package ru.netology.nmedia.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    val id: Long = 0,
    val author: String,
    val authorAvatar: String,
    val published: String,
    val content: String,
    val likedByMe: Boolean = false,
    val likes: Long = 0,
    val sharedCount: Long = 0,
    val viewCount: Long = 0,
    val video: String? = null,
    val attachment: Attachment? = null,
) : Parcelable

