package ru.netology.nmedia.data.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.domain.Post

interface PostRepository {
    val data: Flow<List<Post>>
    val dataVisible: Flow<List<Post>>
    fun getNewerCount(firstId: Long): Flow<Int>
    suspend fun getAll()
    suspend fun save(post: Post)
    suspend fun removeById(id: Long)
    suspend fun likeById(post: Post)
    suspend fun shareById(id: Long)
    suspend fun visibleAll()
}