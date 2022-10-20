package ru.netology.nmedia.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import okio.IOException
import ru.netology.nmedia.data.api.PostsApi
import ru.netology.nmedia.data.dao.PostDao
import ru.netology.nmedia.data.entity.PostEntity
import ru.netology.nmedia.data.entity.toEntity
import ru.netology.nmedia.domain.Post
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError


class PostRepositoryImpl(private val postDao: PostDao) : PostRepository {
    override val data: LiveData<List<Post>> = postDao.getAll().map {
        it.map(PostEntity::toDto)
    }

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

    override suspend fun save(post: Post){
        try {
            val localPost = if(post.id == 0L){
                post.copy(
                    localId = 1,
                    isLocal = true
                )
            } else post
            postDao.insert(PostEntity.fromDto(localPost))
            val response = PostsApi.retrofitService.save(post)
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

    override suspend fun likeById(id: Long, likedByMe: Boolean){
        try {
            val response = if (likedByMe)
                PostsApi.retrofitService.likeById(id)
            else
                PostsApi.retrofitService.dislikeById(id)

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