package ru.netology.nmedia.data.repository

import retrofit2.Response
import retrofit2.http.Field
import ru.netology.nmedia.domain.dto.Token

interface AuthRepository {
    suspend fun updateUser(login: String, pass: String): Token
    suspend fun registerUser(login: String, pass: String, name: String): Token
}