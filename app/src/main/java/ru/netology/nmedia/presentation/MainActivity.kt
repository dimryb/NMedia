package ru.netology.nmedia.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.domain.Post

class MainActivity : AppCompatActivity() {

    private val model: Model by lazy {
        Model()
    }
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
            likesTextView.text = model.calcLickedCounter(post.likesCount, post.liked)
            shareTextView.text = model.counterCompression(post.sharedCount)
            viewsTextView.text = model.counterCompression(post.viewCount)
        }
        setLikedResource(post.liked)
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

    private fun setupClickListeners() {
        with(binding) {
            likesImageView.setOnClickListener {
                post = post.copy(liked = !post.liked)
                setLikedResource(post.liked)
                binding.likesTextView.text = model.calcLickedCounter(post.likesCount, post.liked)
            }
            shareImageView.setOnClickListener {
                post = post.copy(sharedCount = post.sharedCount + 1)
                shareTextView.text = model.counterCompression(post.sharedCount)
            }
        }
    }


}