package ru.netology.nmedia.data.repository

import ru.netology.nmedia.domain.dto.Token

interface AuthRepository {
    suspend fun authUser(login: String, pass: String): Token
    suspend fun registerUser(login: String, pass: String, name: String): Token
}