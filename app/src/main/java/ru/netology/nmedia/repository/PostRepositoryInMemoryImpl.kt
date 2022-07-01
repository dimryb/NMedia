package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.domain.Post

class PostRepositoryInMemoryImpl : PostRepository {
    private var post = Post(
        author = "Нетология. Университет интернет-профессий",
        authorAvatar = "",
        published = "24 июня в 21:11",
        content = """Привет, это новая нетология! когда-то Нетология начиналась с интенсивов по 
                |онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и 
                |управлению. Мы растем сами и помогаем расти студентам: от новичков до уверенный 
                |профессионалов. Но самое важное остается с нами: мы верим, что в каждом уже есть 
                |сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия 
                |- помочь встать на путь роста и начать цепочку перемен 
                |→ http://netolo.gy/fyb""".trimMargin(),
        likesCount = 10,
        sharedCount = 1099,
        viewCount = 1_567_890,
    )

    private var data = MutableLiveData(post)

    override fun get(): LiveData<Post> = data

    override fun like() {
        val calkNewLikeCount = fun(oldCount: Long, isLiked: Boolean): Long {
            return if (isLiked) {
                if (oldCount > 0) oldCount - 1 else oldCount
            } else {
                oldCount + 1
            }
        }
        post = post.copy(likesCount = calkNewLikeCount(post.likesCount, post.liked))
        post = post.copy(liked = !post.liked)
        data.value = post
    }

    override fun share() {
        post = post.copy(sharedCount = post.sharedCount + 1)
        data.value = post
    }
}