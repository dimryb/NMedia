package ru.netology.nmedia.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.domain.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread

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

    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.postValue(FeedModel(loading = true))

        repository.getAllAsync(object : PostRepository.Callback<List<Post>> {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun like(post: Post) {
        repository.likeByIdAsync(
            post.id,
            !post.likedByMe,
            object : PostRepository.Callback<Post> {
                override fun onSuccess(result: Post) {
                    val old = _data.value?.posts.orEmpty()
                    val new = old.map {
                        if (result.id == it.id) result else it
                    }
                    _data.postValue(FeedModel(posts = new))
                }

                override fun onError(e: Exception) {
                    _data.postValue(FeedModel(error = true))
                }
            })
    }

    fun share(postId: Long) {
        thread { repository.share(postId) }
    }

    fun removeById(postId: Long) {
        thread {
            val old = _data.value?.posts.orEmpty()
            val posts = _data.value?.posts.orEmpty()
                .filter { it.id != postId }
            _data.postValue(
                _data.value?.copy(
                    posts = posts, empty = posts.isEmpty()
                )
            )
            try {
                repository.removeById(postId)
            } catch (e: IOException) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        }
    }

    fun save() {
        edited.value?.let { post ->
            thread {
                val newPost = repository.save(post)
                val old = _data.value?.posts.orEmpty()
                val posts = listOf(newPost) + old
                _data.postValue(FeedModel(posts = posts))
                _postCreated.postValue(Unit)
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