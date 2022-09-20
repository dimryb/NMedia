package ru.netology.nmedia.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.domain.Post
import java.io.IOException
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}


    override fun getAllAsync(callback: PostRepository.Callback<List<Post>>) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string() ?: throw RuntimeException("body is null")
                    try {
                        callback.onSuccess(
                            urlAdapter(
                                gson.fromJson<List<Post>>(
                                    body,
                                    typeToken.type
                                )
                            )
                        )
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun likeByIdAsync(
        postId: Long,
        likedByMe: Boolean,
        callback: PostRepository.Callback<Post>
    ) {
        val likesUrl = "${BASE_URL}/api/posts/${postId}/likes"
        val request: Request = if (likedByMe) {
            Request.Builder()
                .post("".toRequestBody())
                .url(likesUrl)
                .build()
        } else {
            Request.Builder()
                .delete(null)
                .url(likesUrl)
                .build()
        }

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

    override fun shareByIdAsync(postId: Long, callback: PostRepository.Callback<Post>) {
        //TODO("Not yet implemented")
        Log.e("PostRepositoryImpl", "Share is not yet implemented")
    }

    override fun removeByIdAsync(postId: Long, callback: PostRepository.Callback<Unit>) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$postId")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    response.body?.string() ?: throw RuntimeException("body is null")
                    try {
                        callback.onSuccess(Unit)
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
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