package ru.netology.nmedia.data.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.domain.dto.FeedItem
import ru.netology.nmedia.domain.dto.MediaUpload
import ru.netology.nmedia.domain.dto.Post

interface PostRepository {
    val data: Flow<PagingData<FeedItem>>
    fun getNewerCount(firstId: Long): Flow<Int>
    suspend fun getAll()
    suspend fun save(post: Post, upload: MediaUpload?)
    suspend fun removeById(id: Long)
    suspend fun likeById(post: Post)
    suspend fun shareById(id: Long)
    suspend fun visibleAll()
}