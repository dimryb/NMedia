package ru.netology.nmedia.domain

data class Post(
    val id: Long = 0,
    val author: String,
    val authorAvatar: String,
    val published: String,
    val content: String,
    val liked: Boolean = false,
    val likesCount: Int = 0,
    val sharedCount: Int = 0,
    var viewCount: Int = 0,
)
