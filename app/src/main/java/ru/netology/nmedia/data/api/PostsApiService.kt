package ru.netology.nmedia.data.api

import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.domain.dto.Media
import ru.netology.nmedia.domain.dto.Post

interface PostsApiService {
    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long): Response<List<Post>>

    @GET("posts/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Post>

    @POST("posts")
    suspend fun save(@Body post: Post): Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response<Post>

    @Multipart
    @POST("media")
    suspend fun uploadPhoto(@Part part: MultipartBody.Part): Response<Media>
}

interface MediaApi {
    @Multipart
    @POST("media")
    suspend fun uploadPhoto(@Part part: MultipartBody.Part): Response<Media>
}

object PostsApi {
    private const val BASE_URL = "${BuildConfig.BASE_URL}/api/slow/"

    private val logging = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val authInterceptor =  Interceptor { chain ->
        val request = AppAuth.getInstance().data.value?.token?.let {
            chain.request()
                .newBuilder()
                .addHeader("Authorisation", it)
                .build()
        } ?: chain.request()

        chain.proceed(request)
    }

    private val okhttp = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(authInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okhttp)
        .build()

    val service: PostsApiService by lazy {
        retrofit.create(PostsApiService::class.java)
    }

    val serviceMedia: MediaApi by lazy {
        retrofit.create(MediaApi::class.java)
    }
}