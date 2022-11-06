package ru.netology.nmedia.data.repository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.IOException
import ru.netology.nmedia.data.api.PostsApi
import ru.netology.nmedia.data.dao.PostDao
import ru.netology.nmedia.data.entity.PostEntity
import ru.netology.nmedia.data.entity.toDto
import ru.netology.nmedia.data.entity.toEntity
import ru.netology.nmedia.domain.Attachment
import ru.netology.nmedia.domain.dto.Media
import ru.netology.nmedia.domain.dto.Post
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.presentation.viewmodel.PhotoModel


class PostRepositoryImpl(private val postDao: PostDao) : PostRepository {

    override val data = postDao.getAll().map(List<PostEntity>::toDto).flowOn(Dispatchers.Default)

    override val dataVisible =
        postDao.getVisible().map(List<PostEntity>::toDto).flowOn(Dispatchers.Default)

    override fun getNewerCount(firstId: Long): Flow<Int> = flow {
        try {
            while (true) {
                delay(2_000L)
                val response = PostsApi.service.getNewer(firstId)
                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                }

                val body = response.body() ?: throw ApiError(response.code(), response.message())
                postDao.insert(body.toEntity().map {
                    it.copy(visible = false)
                })

                emit(body.size)
                println("newerCount ${body.size}")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getAll() {
        try {
            val response = PostsApi.service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(body.map { it.copy(visible = true) }.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    private suspend fun getMaxId(): Long {
        try {
            return postDao.getMaxId()
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(post: Post) {
        try {
            visibleAll()
            val localPost = post.copy(
                id = if (post.id == 0L) getMaxId() + 1 else post.id,
                isLocal = true,
            )
            postDao.insert(PostEntity.fromDto(localPost))
            val response = PostsApi.service.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            if (localPost.id != body.id) {
                removeLocal(localPost)
            }
            postDao.insert(PostEntity.fromDto(body.copy(visible = true)))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveWithAttachment(post: Post, model: PhotoModel) {
        try {
            visibleAll()
            val file = uploadFile(model)
            val localPost = post.copy(
                id = if (post.id == 0L) getMaxId() + 1 else post.id,
                isLocal = true,
                attachment = Attachment(url = file.id)
            )
            postDao.insert(PostEntity.fromDto(localPost))
            val response = PostsApi.service.save(post.copy(attachment = Attachment(url = file.id)))
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            if (localPost.id != body.id) {
                removeLocal(localPost)
            }
            postDao.insert(PostEntity.fromDto(body.copy(visible = true)))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    private suspend fun uploadFile(model: PhotoModel): Media {
        try {
            val part = MultipartBody.Part.createFormData(
                "file", model.file.name, model.file.asRequestBody()
            )
            val response = PostsApi.service.uploadPhoto(part)
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    private suspend fun removeLocal(post: Post) = postDao.removeById(post.id)

    override suspend fun removeById(id: Long) {
        try {
            val response = PostsApi.service.removeById(id)
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
            val response = with(PostsApi.service) {
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

    override suspend fun visibleAll() {
        try {
            postDao.updateVisibleAll()
        } catch (e: Exception) {
            throw UnknownError
        }
    }

}