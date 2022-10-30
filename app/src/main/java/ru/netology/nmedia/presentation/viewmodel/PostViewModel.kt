package ru.netology.nmedia.presentation.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.data.db.AppDb
import ru.netology.nmedia.data.repository.PostRepository
import ru.netology.nmedia.data.repository.PostRepositoryImpl
import ru.netology.nmedia.domain.dto.Post
import ru.netology.nmedia.presentation.model.FeedModel
import ru.netology.nmedia.presentation.model.FeedModelState
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File

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
    val data: LiveData<FeedModel> = repository.data.map(::FeedModel).asLiveData(Dispatchers.Default)

    val dataVisible: LiveData<FeedModel> =
        repository.dataVisible.map(::FeedModel).asLiveData(Dispatchers.Default)

    val invisibleCount: LiveData<Int> =
        repository.data.map { posts -> posts.count { !it.visible } }.asLiveData(Dispatchers.Default)

    private val _state = MutableLiveData<FeedModelState>()
    val state: LiveData<FeedModelState>
        get() = _state

    val newerCount: LiveData<Int> = data.switchMap {
        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
            .asLiveData(Dispatchers.Default)
    }

    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _photo = MutableLiveData<PhotoModel?>(null)
    val photo: LiveData<PhotoModel?>
        get() = _photo

    init {
        loadPosts()
        showNewPosts()
    }

    fun showNewPosts() {
        viewModelScope.launch {
            try {
                repository.visibleAll()
                _state.value = FeedModelState.Idle
            } catch (e: Exception) {
                _state.value = FeedModelState.Error
            }
        }
    }

    fun loadPosts() {
        viewModelScope.launch {
            _state.value = FeedModelState.Loading
            try {
                repository.getAll()
                _state.value = FeedModelState.Idle
            } catch (e: Exception) {
                _state.value = FeedModelState.Error
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = FeedModelState.Refresh
            try {
                repository.getAll()
                _state.value = FeedModelState.Idle
            } catch (e: Exception) {
                _state.value = FeedModelState.Error
            }
        }
    }

    fun like(post: Post) {
        viewModelScope.launch {
            try {
                repository.likeById(post)
                _state.value = FeedModelState.Idle
            } catch (e: Exception) {
                _state.value = FeedModelState.Error
            }
        }
    }

    fun share(id: Long) {
//        TODO("Not yet implemented")
//        viewModelScope.launch {
//            repository.shareById(id)
//        }
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.removeById(id)
                _state.value = FeedModelState.Idle
            } catch (e: Exception) {
                _state.value = FeedModelState.Error
            }
        }
    }

    fun save() {
        edited.value?.let { post ->
            viewModelScope.launch {
                _postCreated.value = Unit
                try {
                    _photo.value?.let { photoModel ->
                        repository.saveWithAttachment(post, photoModel)
                    } ?: run {
                        repository.save(post)
                    }
                    _state.value = FeedModelState.Idle
                } catch (e: Exception) {
                    _state.value = FeedModelState.Error
                }
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
            edited.value = it.copy(content = text, visible = true)
        }
    }

    fun changePhoto(uri: Uri?, toFile: File?) {
        _photo.value = if (uri != null && toFile != null){
            PhotoModel(uri, toFile)
        } else {
            null
        }
    }
}