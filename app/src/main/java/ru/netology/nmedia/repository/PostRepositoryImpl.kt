package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.domain.Post
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : PostRepository {

    private var posts = emptyList<Post>()

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun get(): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        posts = client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, typeToken.type)
            }
        return posts
    }

    override fun like(postId: Long) {
        val post = posts.find { post -> post.id == postId }
            ?: throw RuntimeException("post id: $postId not found")
        val likedByMe: Boolean = !post.likedByMe;

        val request: Request = if (likedByMe) {
            Request.Builder()
                .post("".toRequestBody())
                .url("${BASE_URL}/api/posts/${postId}/likes")
                .build()
        } else {
            Request.Builder()
                .delete(null)
                .url("${BASE_URL}/api/posts/${postId}/likes")
                .build()
        }

        client.newCall(request)
            .execute()
            .close()

        // TODO: считать и обновить
        posts = posts.map {
            if (it.id == postId) {
                it.copy(
                    likedByMe = likedByMe, likes = if (likedByMe) it.likes + 1 else it.likes - 1
                )
            } else {
                it
            }
        }
    }

    override fun share(postId: Long) {
        TODO("Not yet implemented")
    }

    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun save(post: Post) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/posts")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }
}