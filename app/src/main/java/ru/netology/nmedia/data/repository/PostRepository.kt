package ru.netology.nmedia.data.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.domain.dto.Post
import ru.netology.nmedia.presentation.viewmodel.PhotoModel

interface PostRepository {
    val data: Flow<PagingData<Post>>
//    val dataVisible: Flow<List<Post>>
    fun getNewerCount(firstId: Long): Flow<Int>
    suspend fun getAll()
    suspend fun save(post: Post)
    suspend fun saveWithAttachment(post: Post, model: PhotoModel)
    suspend fun removeById(id: Long)
    suspend fun likeById(post: Post)
    suspend fun shareById(id: Long)
    suspend fun visibleAll()
}