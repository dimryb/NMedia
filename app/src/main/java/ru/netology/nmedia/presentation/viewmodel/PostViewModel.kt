package ru.netology.nmedia.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.data.AppDb
import ru.netology.nmedia.data.repository.PostRepository
import ru.netology.nmedia.data.repository.PostRepositoryImpl
import ru.netology.nmedia.domain.Post
import ru.netology.nmedia.presentation.model.FeedModel
import ru.netology.nmedia.util.SingleLiveEvent

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val empty = Post(
        id = 0,
        content = "",
        author = "",
        authorAvatar = "",
        likedByMe = false,
        likes = 0,
        published = ""
    )

    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(application).postDao())
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts(swipeRefresh = false)
    }

    fun loadPosts(swipeRefresh: Boolean) {
        viewModelScope.launch {
            val old = _data.value?.posts.orEmpty()
            _data.postValue(FeedModel(posts = old, loading = true, swipeRefresh = swipeRefresh))
            try {
                val posts = repository.getAll()
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            } catch (e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        }
    }

    fun like(post: Post) {
        viewModelScope.launch {
            try {
                val post = repository.likeById(post.id, !post.likedByMe)
                val old = _data.value?.posts.orEmpty()
                val new = old.map {
                    if (post.id == it.id) post else it
                }
                _data.postValue(FeedModel(posts = new))
            } catch (e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        }
    }

    fun share(id: Long) {
        viewModelScope.launch {
            repository.shareById(id)
        }
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            repository.removeById(id)
        }
    }

    fun save() {
        viewModelScope.launch {
            try {
                edited.value?.let { post ->
                    val result = repository.save(post)
                    val old = _data.value?.posts.orEmpty()
                    val new = old.map {
                        if (result.id == it.id) result else it
                    }
                    val posts = if (post.id == 0L) listOf(result) + old else new
                    _data.postValue(FeedModel(posts = posts))
                    _postCreated.postValue(Unit)
                }
            } catch (e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun editContent(content: String) {
        val value = edited.value
        value?.let {
            val text = content.trim()
            if (it.content == text) {
                return
            }
            edited.value = it.copy(content = text)
        }
    }
}