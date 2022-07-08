package ru.netology.nmedia.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.domain.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl

class PostViewModel : ViewModel() {

    private val empty = Post(
        id = 0,
        content = "",
        author = "",
        authorAvatar = "",
        likedByMe = false,
        published = ""
    )

    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.get()
    val edited = MutableLiveData(empty)

    fun like(postId: Long) = repository.like(postId)
    fun share(postId: Long) = repository.share(postId)
    fun removeById(postId: Long) = repository.removeById(postId)

    fun save() {
        edited.value?.let {
            repository.save(it)
        }
        edited.value = empty
    }

    fun editContent(content: String){
        edited.value?.let {
            val text = content.trim()
            if (it.content == text) {
                return
            }
            edited.value = it.copy(content = text)
        }
    }
}