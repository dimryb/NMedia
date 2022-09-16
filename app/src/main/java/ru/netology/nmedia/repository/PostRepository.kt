package ru.netology.nmedia.repository

import ru.netology.nmedia.domain.Post

interface PostRepository {
    fun share(postId: Long)
    fun removeById(id: Long)
    fun save(post: Post): Post

    fun getAllAsync(callback: Callback<List<Post>>)
    fun likeByIdAsync(postId: Long, likedByMe: Boolean, callback: Callback<Post>)

    interface Callback<T> {
        fun onSuccess(result: T) {}
        fun onError(e: Exception) {}
    }
}