package ru.netology.nmedia.repository

import android.util.Log
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.domain.Post
import java.io.IOException
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()

    override fun getAllAsync(callback: PostRepository.Callback<List<Post>>) {
        PostsApi.retrofitService.getAll().enqueue(object : retrofit2.Callback<List<Post>> {
            override fun onResponse(
                call: retrofit2.Call<List<Post>>,
                response: retrofit2.Response<List<Post>>
            ) {
                if (!response.isSuccessful) {
                    callback.onError(java.lang.RuntimeException(response.message()))
                    return
                }

                callback.onSuccess(
                    urlAdapter(
                        response.body() ?: throw java.lang.RuntimeException("body is null")
                    )
                )
            }

            override fun onFailure(call: retrofit2.Call<List<Post>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun likeByIdAsync(
        postId: Long,
        likedByMe: Boolean,
        callback: PostRepository.Callback<Post>
    ) {
//        fun likeFun(postId: Long) = if (likedByMe)
//            PostsApi.retrofitService.likeById(postId)
//        else
//            PostsApi.retrofitService.dislikeById(postId)

        val likeFun =
            with(PostsApi.retrofitService) { if (likedByMe) ::likeById else ::dislikeById }

        likeFun(postId).enqueue(object : retrofit2.Callback<Post> {
            override fun onResponse(
                call: retrofit2.Call<Post>,
                response: retrofit2.Response<Post>
            ) {
                if (!response.isSuccessful) {
                    callback.onError(java.lang.RuntimeException(response.message()))
                    return
                }

                callback.onSuccess(
                    urlAdapter(
                        response.body() ?: throw java.lang.RuntimeException("body is null")
                    )
                )
            }

            override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun shareByIdAsync(postId: Long, callback: PostRepository.Callback<Post>) {
        //TODO("Not yet implemented")
        Log.e("PostRepositoryImpl", "Share is not yet implemented")
    }

    override fun removeByIdAsync(postId: Long, callback: PostRepository.Callback<Unit>) {
        PostsApi.retrofitService.removeById(postId).enqueue(object : retrofit2.Callback<Unit> {
            override fun onResponse(
                call: retrofit2.Call<Unit>,
                response: retrofit2.Response<Unit>
            ) {
                if (!response.isSuccessful) {
                    callback.onError(java.lang.RuntimeException(response.message()))
                    return
                }

                callback.onSuccess(
                    response.body() ?: throw java.lang.RuntimeException("body is null")
                )
            }

            override fun onFailure(call: retrofit2.Call<Unit>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun saveByIdAsync(post: Post, callback: PostRepository.Callback<Post>) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string() ?: throw RuntimeException("body is null")
                    try {
                        callback.onSuccess(urlAdapter(gson.fromJson(body, Post::class.java)))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()

        private fun urlAdapter(post: Post): Post {
            return post.copy(
                authorAvatar = "${BASE_URL}/avatars/${post.authorAvatar}",
                attachment = if (post.attachment != null) {
                    post.attachment.copy(url = "${BASE_URL}/images/${post.attachment.url}")
                } else null
            )
        }

        private fun urlAdapter(posts: List<Post>): List<Post> {
            return posts.map { urlAdapter(it) }
        }
    }
}