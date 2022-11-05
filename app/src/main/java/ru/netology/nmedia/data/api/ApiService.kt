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
import ru.netology.nmedia.domain.dto.PushToken
import ru.netology.nmedia.domain.dto.Token

interface ApiService {
    @POST("users/push-tokens")
    suspend fun sendPushToken(@Body token: PushToken): Response<Unit>

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

interface AuthApi {
    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun updateUser(
        @Field("login") login: String,
        @Field("pass") pass: String
    ): Response<Token>

    @FormUrlEncoded
    @POST("users/registration")
    suspend fun registerUser(
        @Field("login") login: String,
        @Field("pass") pass: String,
        @Field("name") name: String
    ): Response<Token>
}

object PostsApi {
    private const val BASE_SLOW_URL = "${BuildConfig.BASE_URL}/api/slow/"
    private const val BASE_URL = "${BuildConfig.BASE_URL}/api/"

    private val logging = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val authInterceptor = Interceptor { chain ->
        val request = AppAuth.getInstance().data.value?.token?.let {
            chain.request()
                .newBuilder()
                .addHeader("Authorization", it)
                .build()
        } ?: chain.request()

        chain.proceed(request)
    }

    private val okhttp = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(authInterceptor)
        .build()

    private val retrofitPosts = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_SLOW_URL)
        .client(okhttp)
        .build()

    private val retrofitAuth = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okhttp)
        .build()

    val service: ApiService by lazy {
        retrofitPosts.create(ApiService::class.java)
    }

    val serviceMedia: MediaApi by lazy {
        retrofitPosts.create(MediaApi::class.java)
    }

    val serviceAuth: AuthApi by lazy {
        retrofitAuth.create(AuthApi::class.java)
    }
}