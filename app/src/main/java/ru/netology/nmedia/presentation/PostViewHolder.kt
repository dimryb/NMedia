package ru.netology.nmedia.presentation

import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.domain.Post

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
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
            likesButton.text = post.likesCount.toString()
            likesButton.isChecked = post.likedByMe
            shareButton.text = formatter.counterCompression(post.sharedCount)
            viewsButton.text = formatter.counterCompression(post.viewCount)

        }
    }


    private fun setupClickListeners(cardPostBinding: CardPostBinding, post: Post) {
        with(cardPostBinding) {
            likesButton.setOnClickListener { onInteractionListener.onLike(post) }
            shareButton.setOnClickListener { onInteractionListener.onShare(post) }

            menuButton.setOnClickListener { setupPopupMenu(it, post) }
        }
    }

    private fun setupPopupMenu(view: View, post: Post){
        PopupMenu(view.context, view).apply {
            inflate(R.menu.options_post)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.remove -> {
                        onInteractionListener.onRemove(post)
                        true
                    }
                    R.id.edit -> {
                        onInteractionListener.onEdit(post)
                        true
                    }
                    else -> false
                }
            }
        }.show()
    }
}


