package ru.netology.nmedia.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.domain.Post

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var post = Post(
        author = "Нетология. Университет интернет-профессий",
        authorAvatar = "",
        published = "24 июня в 21:11",
        content = """Привет, это новая нетология! когда-то Нетология начиналась с интенсивов по 
                |онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и 
                |управлению. Мы растем сами и помогаем расти студентам: от новичков до уверенный 
                |профессионалов. Но самое важное остается с нами: мы верим, что в каждом уже есть 
                |сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия 
                |- помочь встать на путь роста и начать цепочку перемен""".trimMargin(),
        likesCount = 10,
        sharedCount = 1099,
        viewCount = 5,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setContent(post)
        setupClickListeners()
    }

    private fun setContent(post: Post) {
        with(binding) {
            authorTextView.text = post.author
            publishedTextView.text = post.published
            postTextView.text = post.content
            shareTextView.text = counterCompression(post.sharedCount)
            viewsTextView.text = post.viewCount.toString()
        }
        setLikedResource(post.liked)
        setLikedCounter(post.likesCount, post.liked)
    }

    private fun setLikedResource(isLiked: Boolean) {
        binding.likesImageView.setImageResource(
            if (isLiked) {
                R.drawable.ic_liked
            } else {
                R.drawable.ic_like
            }
        )
    }

    private fun setLikedCounter(counter: Int, isLiked: Boolean) {
        val newCount = counter + if (isLiked) 1 else 0
        binding.likesTextView.text = newCount.toString()
    }

    private fun setupClickListeners() {
        with(binding) {
            likesImageView.setOnClickListener {
                post = post.copy(liked = !post.liked)
                setLikedResource(post.liked)
                setLikedCounter(post.likesCount, post.liked)
            }
            shareImageView.setOnClickListener {
                post = post.copy(sharedCount = post.sharedCount + 1)
                shareTextView.text = counterCompression(post.sharedCount)
            }
        }
    }

    private fun counterCompressionThousands(counter: Int): String{
        val thousands = counter/1000
        val hundreds = (counter - thousands * 1000)/100
        return if (hundreds == 0){
            "${thousands}K"
        }else{
            "${thousands}.${hundreds}K"
        }
    }

    private fun counterCompression(counter: Int): String {
        return when {
            counter < 1000 -> counter.toString()
            counter in 1000..9999 -> counterCompressionThousands(counter)
            else -> "0"
        }
    }
}