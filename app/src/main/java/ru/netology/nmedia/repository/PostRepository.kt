package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.domain.Post

interface PostRepository {
    fun get(): List<Post>
    fun like(postId: Long)
    fun share(postId: Long)
    fun removeById(id: Long)
    fun save(post: Post)
}