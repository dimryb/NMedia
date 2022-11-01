package ru.netology.nmedia.data.repository

import ru.netology.nmedia.domain.dto.Token

interface AuthRepository {
    suspend fun updateUser(login: String, pass: String): Token
}