package ru.netology.nmedia.data.repository

import androidx.paging.*
import okio.IOException
import ru.netology.nmedia.data.api.ApiService
import ru.netology.nmedia.data.dao.PostDao
import ru.netology.nmedia.data.entity.PostEntity
import ru.netology.nmedia.domain.dto.Post
import ru.netology.nmedia.error.ApiError

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val service: ApiService,
    private val postDao: PostDao
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, PostEntity>): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    service.getLatest(state.config.pageSize)
                }
                LoadType.APPEND -> {
                    val id = state.lastItemOrNull()?.id ?: return MediatorResult.Success(false)
                    service.getAfter(id, state.config.pageSize)
                }
                LoadType.PREPEND -> {
                    val id = state.firstItemOrNull()?.id ?: return MediatorResult.Success(false)
                    service.getBefore(id, state.config.pageSize)
                }
            }

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(
                response.code(),
                response.message()
            )

            postDao.insert(body.map(PostEntity::fromDto))

            return MediatorResult.Success(body.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }
}