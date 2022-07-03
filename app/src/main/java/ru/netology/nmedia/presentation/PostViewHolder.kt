package ru.netology.nmedia.presentation

import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.domain.Post

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onLikeListener: OnLikeListener,
    private val onShareListener: OnShareListener
) : RecyclerView.ViewHolder(binding.root) {

    private val formatter: CounterFormatter by lazy {
        CounterFormatter()
    }

    fun bind(post: Post) {
        binding.apply {
            setContent(this, post)
            setupClickListeners(this, post)
        }
    }

    private fun setContent(cardPostBinding: CardPostBinding, post: Post) {
        with(cardPostBinding) {
            authorTextView.text = post.author
            publishedTextView.text = post.published
            postTextView.text = post.content
            likesTextView.text = post.likesCount.toString()
            shareTextView.text = formatter.counterCompression(post.sharedCount)
            viewsTextView.text = formatter.counterCompression(post.viewCount)
        }
        setLikedResource(cardPostBinding, post.likedByMe)
    }

    private fun setLikedResource(cardPostBinding: CardPostBinding, isLiked: Boolean) {
        cardPostBinding.likesImageView.setImageResource(
            if (isLiked) {
                R.drawable.ic_liked
            } else {
                R.drawable.ic_like
            }
        )
    }

    private fun setupClickListeners(cardPostBinding: CardPostBinding, post: Post) {
        with(cardPostBinding) {
            likesImageView.setOnClickListener { onLikeListener(post) }
            shareImageView.setOnClickListener { onShareListener(post) }
        }
    }
}


