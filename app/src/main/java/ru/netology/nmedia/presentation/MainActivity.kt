package ru.netology.nmedia.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.domain.Post

class MainActivity : AppCompatActivity() {

    private val model: CounterFormatter by lazy {
        CounterFormatter()
    }
    private lateinit var binding: ActivityMainBinding
    private val viewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeViewModel()
        setupClickListeners()
    }

    private fun observeViewModel() {
        viewModel.data.observe(this) { post ->
            setContent(post)
        }
    }

    private fun setContent(post: Post) {
        with(binding) {
            authorTextView.text = post.author
            publishedTextView.text = post.published
            postTextView.text = post.content
            likesTextView.text = post.likesCount.toString()
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
            likesImageView.setOnClickListener { viewModel.like() }
            shareImageView.setOnClickListener { viewModel.share() }
        }
    }


}