package ru.netology.nmedia.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.domain.Post
import ru.netology.nmedia.presentation.model.FeedModel
import ru.netology.nmedia.data.repository.PostRepository
import ru.netology.nmedia.data.repository.PostRepositoryImpl
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

    private val repository: PostRepository = PostRepositoryImpl()
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
        val old = _data.value?.posts.orEmpty()
        _data.postValue(FeedModel(posts = old, loading = true, swipeRefresh = swipeRefresh))

        repository.getAllAsync(object : PostRepository.Callback<List<Post>> {
            override fun onSuccess(result: List<Post>) {
                _data.postValue(FeedModel(posts = result, empty = result.isEmpty()))
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
        repository.shareByIdAsync(postId, object : PostRepository.Callback<Post> {
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

    fun removeById(postId: Long) {
        val old = _data.value?.posts.orEmpty()
        val posts = _data.value?.posts.orEmpty()
            .filter { it.id != postId }
        _data.postValue(
            _data.value?.copy(
                posts = posts, empty = posts.isEmpty()
            )
        )

        repository.removeByIdAsync(postId, object : PostRepository.Callback<Unit> {
            override fun onSuccess(result: Unit) {
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(posts = old, error = true))
            }
        })
    }

    fun save() {
        edited.value?.let { post ->
            repository.savePostAsync(
                post,
                object : PostRepository.Callback<Post> {
                    override fun onSuccess(result: Post) {
                        val old = _data.value?.posts.orEmpty()
                        val new = old.map {
                            if (result.id == it.id) result else it
                        }
                        val posts = if (post.id == 0L) listOf(result) + old else new
                        _data.postValue(FeedModel(posts = posts))
                        _postCreated.postValue(Unit)
                    }

                    override fun onError(e: Exception) {
                        _data.postValue(FeedModel(error = true))
                    }
                })
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