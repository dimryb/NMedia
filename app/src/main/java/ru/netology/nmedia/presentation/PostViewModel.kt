package ru.netology.nmedia.presentation

import androidx.lifecycle.ViewModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl

class PostViewModel : ViewModel() {

    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.get()
    fun like(postId: Long) = repository.like(postId)
    fun share(postId: Long) = repository.share(postId)
    fun removeById(postId: Long) = repository.removeById(postId)
}