package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.domain.Post

class PostRepositoryFileImpl(
    private val context: Context
) : PostRepository {
    private val gson = Gson()
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private val filename = "posts.json"

    private var posts = listOf(
        Post(
            id = 3,
            author = "Нетология. Университет интернет-профессий",
            authorAvatar = "",
            published = "16 июля в 18:20",
            content = """Пост с иллюстрацией в медиа
                    |""".trimMargin(),
            likesCount = 5,
            sharedCount = 7,
            viewCount = 367,
            video = "https://www.youtube.com/watch?v=WhWc3b3KhnY"
        ),
        Post(
            id = 2,
            author = "Нетология. Университет интернет-профессий",
            authorAvatar = "",
            published = "03 июля в 15:17",
            content = """Знаний хватит на всех: на следующей неделе разбираемся с..
                |""".trimMargin(),
            likesCount = 0,
            sharedCount = 123,
            viewCount = 9_900,
        ),
        Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий",
            authorAvatar = "",
            published = "24 июня в 21:11",
            content = """Привет, это новая нетология! Когда-то Нетология начиналась с интенсивов по 
                    |онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и 
                    |управлению. Мы растем сами и помогаем расти студентам: от новичков до уверенный 
                    |профессионалов. Но самое важное остается с нами: мы верим, что в каждом уже есть 
                    |сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия 
                    |- помочь встать на путь роста и начать цепочку перемен 
                    |→ http://netolo.gy/fyb""".trimMargin(),
            likesCount = 10,
            sharedCount = 1099,
            viewCount = 1_567_890,
        ),
    )

    private var nextId: Long = 0
    private var data = MutableLiveData(posts)

    init {
        val file = context.filesDir.resolve(filename)
        if (file.exists()) {
            context.openFileInput(filename).bufferedReader().use {
                posts = gson.fromJson(it, type)
                data.value = posts
                nextId = 1 + posts.maxOf { post -> post.id }
            }
        } else {
            sync()
        }
    }

    override fun get(): LiveData<List<Post>> = data

    override fun like(postId: Long) {
        val calkNewLikeCount = fun(oldCount: Long, isLiked: Boolean): Long {
            return if (isLiked) {
                if (oldCount > 0) oldCount - 1 else oldCount
            } else {
                oldCount + 1
            }
        }
        posts = posts.map { post ->
            if (post.id != postId) post else {
                post.copy(
                    likesCount = calkNewLikeCount(post.likesCount, post.likedByMe),
                    likedByMe = !post.likedByMe
                )
            }
        }
        data.value = posts
        sync()
    }

    override fun share(postId: Long) {
        posts = posts.map { post ->
            if (post.id != postId) post else post.copy(sharedCount = post.sharedCount + 1)
        }
        data.value = posts
        sync()
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
        sync()
    }

    override fun save(post: Post) {
        posts = if (post.id == 0L) {
            listOf(
                post.copy(
                    id = nextId++,
                    author = "me",
                    likedByMe = false,
                    published = "now"
                )
            ) + posts
        } else {
            posts.map {
                if (it.id == post.id) it.copy(content = post.content) else it
            }
        }
        data.value = posts
        sync()
    }

    private fun sync() {
        context.openFileOutput(filename, Context.MODE_PRIVATE).bufferedWriter().use {
            it.write(gson.toJson(posts))
        }
    }
}