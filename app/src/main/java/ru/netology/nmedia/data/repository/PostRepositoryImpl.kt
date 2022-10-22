package ru.netology.nmedia.data.repository

import androidx.lifecycle.asLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okio.IOException
import ru.netology.nmedia.data.api.PostsApi
import ru.netology.nmedia.data.dao.PostDao
import ru.netology.nmedia.data.entity.PostEntity
import ru.netology.nmedia.data.entity.toDto
import ru.netology.nmedia.data.entity.toEntity
import ru.netology.nmedia.domain.Post
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError


class PostRepositoryImpl(private val postDao: PostDao) : PostRepository {

    override val data = postDao.getAll().map(List<PostEntity>::toDto).flowOn(Dispatchers.Default)

    override suspend fun getAll() {
        try {
            val response = PostsApi.retrofitService.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    private fun getMaxId(): Long =
        data.asLiveData(Dispatchers.Default).value?.let { post -> post.maxBy { it.id } }?.id ?: 0L

    override suspend fun save(post: Post) {
        try {
            val localPost = post.copy(
                id = if (post.id == 0L) getMaxId() + 1 else post.id,
                isLocal = true,
                author = "Student"
            )
            postDao.insert(PostEntity.fromDto(localPost))
            val response = PostsApi.retrofitService.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            if (localPost.id != body.id) {
                removeLocal(localPost)
            }
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    private suspend fun removeLocal(post: Post) = postDao.removeById(post.id)

    override suspend fun removeById(id: Long) {
        try {
            val response = PostsApi.retrofitService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            postDao.removeById(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(post: Post) {
        if (post.isLocal) return

        try {
            val localPost = post.copy(
                likedByMe = !post.likedByMe,
                likes = if (post.likedByMe) post.likes - 1 else post.likes + 1,
                isLocal = true,
            )
            postDao.insert(PostEntity.fromDto(localPost))
            val response = with(PostsApi.retrofitService) {
                if (post.likedByMe) ::dislikeById else ::likeById
            }(post.id)

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun shareById(id: Long) {
        TODO("Not yet implemented")
    }

}