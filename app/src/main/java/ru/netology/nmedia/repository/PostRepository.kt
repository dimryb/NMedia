package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.domain.Post

interface PostRepository {
    fun get(): LiveData<List<Post>>
    fun like(postId: Long)
    fun share(postId: Long)
}