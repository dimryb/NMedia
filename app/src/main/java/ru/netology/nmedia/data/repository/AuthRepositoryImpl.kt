package ru.netology.nmedia.data.repository

import okio.IOException
import ru.netology.nmedia.data.api.PostsApi
import ru.netology.nmedia.domain.dto.Token
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError

class AuthRepositoryImpl: AuthRepository {
    override suspend fun updateUser(login: String, pass: String): Token {
        try {
            val response = PostsApi.serviceAuth.updateUser(login, pass)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun registerUser(login: String, pass: String, name: String): Token {
        try {
            val response = PostsApi.serviceAuth.registerUser(login, pass, name)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}
