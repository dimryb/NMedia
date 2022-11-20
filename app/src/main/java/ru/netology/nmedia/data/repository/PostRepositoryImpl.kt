package ru.netology.nmedia.data.repository

import androidx.paging.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.IOException
import ru.netology.nmedia.data.api.ApiService
import ru.netology.nmedia.data.dao.PostDao
import ru.netology.nmedia.data.dao.PostRemoteKeyDao
import ru.netology.nmedia.data.db.AppDb
import ru.netology.nmedia.data.entity.PostEntity
import ru.netology.nmedia.data.entity.toEntity
import ru.netology.nmedia.domain.Attachment
import ru.netology.nmedia.domain.dto.*
import ru.netology.nmedia.domain.enumeration.AttachmentType
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.presentation.viewmodel.PhotoModel
import javax.inject.Inject
import kotlin.random.Random


class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val apiService: ApiService,
    postRemoteKeyDao: PostRemoteKeyDao,
    appDb: AppDb,
) : PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 10),
        pagingSourceFactory = postDao::getPagingSource,
        remoteMediator = PostRemoteMediator(
            service = apiService,
            postDao = postDao,
            postRemoteKeyDao = postRemoteKeyDao,
            appDb = appDb,
        )
    ).flow.map { pagingData ->
        pagingData.map(PostEntity::toDto)
            .insertSeparators { previous: Post?, _ ->
                if (previous?.id?.rem(5) == 0L) {
                    Ad(Random.nextLong(), "figma.jpg")
                } else {
                    null
            }
            }
    }

    override fun getNewerCount(firstId: Long): Flow<Int> = flow {
        try {
            while (true) {
                delay(20_000L)
                val response = apiService.getNewer(firstId)
                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                }

                val body = response.body() ?: throw ApiError(response.code(), response.message())
                postDao.insert(body.toEntity())
                emit(body.size)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
        .catch { e -> throw AppError.from(e) }
        .flowOn(Dispatchers.Default)

    override suspend fun getAll() {
        try {
            val response = apiService.getAll()
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

    override suspend fun save(post: Post, upload: MediaUpload?) {
        try {
            val postWithAttachment = upload?.let {
                uploadFile(it)
            }?.let {
                post.copy(attachment = Attachment(it.id, "", AttachmentType.IMAGE))
            }
            val response = apiService.save(postWithAttachment ?: post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: java.io.IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    private suspend fun uploadFile(upload: MediaUpload): Media {
        try {
            val part = MultipartBody.Part.createFormData(
                "file", upload.file.name, upload.file.asRequestBody()
            )
            val response = apiService.uploadPhoto(part)
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            val response = apiService.removeById(id)
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
            val response = with(apiService) {
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