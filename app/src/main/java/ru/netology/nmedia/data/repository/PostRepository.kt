package ru.netology.nmedia.data.repository

import ru.netology.nmedia.domain.Post

interface PostRepository {
    fun getAllAsync(callback: Callback<List<Post>>)
    fun likeByIdAsync(postId: Long, likedByMe: Boolean, callback: Callback<Post>)
    fun shareByIdAsync(postId: Long, callback: Callback<Post>)
    fun removeByIdAsync(postId: Long, callback: Callback<Unit>)
    fun saveByIdAsync(post: Post, callback: Callback<Post>)

    interface Callback<T> {
        fun onSuccess(result: T) {}
        fun onError(e: Exception) {}
    }
}