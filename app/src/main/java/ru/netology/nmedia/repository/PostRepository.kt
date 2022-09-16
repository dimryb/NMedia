package ru.netology.nmedia.repository

import ru.netology.nmedia.domain.Post

interface PostRepository {
    fun get(): List<Post>
    fun like(postId: Long): Post
    fun share(postId: Long)
    fun removeById(id: Long)
    fun save(post: Post)

    fun getAllAsync(callback: GetAllCallback)

    interface GetAllCallback {
        fun onSuccess(posts: List<Post>) {}
        fun onError(e: Exception) {}
    }
}