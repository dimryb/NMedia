package ru.netology.nmedia.presentation.viewmodel

import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.data.repository.PostRepository
import ru.netology.nmedia.domain.dto.MediaUpload
import ru.netology.nmedia.domain.dto.Post
import ru.netology.nmedia.presentation.model.FeedModelState
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File
import javax.inject.Inject

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorId = 0L,
    authorAvatar = "",
    likedByMe = false,
    likes = 0,
    published = ""
)

private val noPhoto = PhotoModel()

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    private val appAuth: AppAuth,
) : ViewModel() {

    val data: Flow<PagingData<Post>> = dataAuth(repository.data)
//    val dataVisible: LiveData<FeedModel> = dataAuth(repository.dataVisible)

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun dataAuth(repositoryData: Flow<PagingData<Post>>): Flow<PagingData<Post>> =
        appAuth
            .authStateFlow
            .flatMapLatest { (myId, _) ->
                repositoryData
                    .map { posts ->
                            posts.map { it.copy(ownerByMe = it.authorId == myId) }
                    }
            }.flowOn(Dispatchers.Default)

//    val invisibleCount: LiveData<Int> =
//        repository.data.map { posts -> posts.count { !it.visible } }.asLiveData(Dispatchers.Default)

    private val _state = MutableLiveData<FeedModelState>()
    val state: LiveData<FeedModelState>
        get() = _state

//    val newerCount: LiveData<Int> = data.switchMap {
//        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
//            .asLiveData(Dispatchers.Default)
//    }

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

//    fun share(id: Long) {
//        TODO("Not yet implemented")
//        viewModelScope.launch {
//            repository.shareById(id)
//        }
//    }

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
                    repository.save(
                        post, _photo.value?.uri?.let { MediaUpload(it.toFile()) }
                    )
                    _state.value = FeedModelState.Idle
                } catch (e: Exception) {
                    _state.value = FeedModelState.Error
                }
            }
        }
        edited.value = empty
        _photo.value = noPhoto
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

    fun changePhoto(uri: Uri?) {
        _photo.value = PhotoModel(uri)
    }
}