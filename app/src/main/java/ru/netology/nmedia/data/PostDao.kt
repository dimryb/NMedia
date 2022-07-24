package ru.netology.nmedia.data

import ru.netology.nmedia.domain.Post

interface PostDao {
    fun getAll(): List<Post>
    fun save(post: Post): Post
    fun likeById(id: Long)
    fun removeById(id: Long)
}