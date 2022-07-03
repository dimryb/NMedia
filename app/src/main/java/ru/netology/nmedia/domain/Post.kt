package ru.netology.nmedia.domain

data class Post(
    val id: Long = 0,
    val author: String,
    val authorAvatar: String,
    val published: String,
    val content: String,
    val likedByMe: Boolean = false,
    val likesCount: Long = 0,
    val sharedCount: Long = 0,
    val viewCount: Long = 0,
)
