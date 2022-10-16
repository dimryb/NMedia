package ru.netology.nmedia.data.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.domain.Post

interface PostRepository {
    val data: LiveData<List<Post>>
    suspend fun getAll(): List<Post>
    suspend fun save(post: Post): Post
    suspend fun removeById(id: Long): Unit
    suspend fun likeById(id: Long, likedByMe: Boolean): Post
    suspend fun shareById(id: Long): Post
}