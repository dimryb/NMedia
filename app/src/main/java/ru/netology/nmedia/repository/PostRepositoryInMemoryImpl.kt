package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.domain.Post

class PostRepositoryInMemoryImpl : PostRepository {
    private var posts = listOf(
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

    private var data = MutableLiveData(posts)

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
    }

    override fun share(postId: Long) {
        posts = posts.map { post ->
            if (post.id != postId) post else post.copy(sharedCount = post.sharedCount + 1)
        }
        data.value = posts
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
    }
}