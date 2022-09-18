package ru.netology.nmedia.presentation

import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
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
            likesButton.text = post.likes.toString()
            likesButton.isChecked = post.likedByMe
            shareButton.text = formatter.counterCompression(post.sharedCount)
            viewsButton.text = formatter.counterCompression(post.viewCount)

            if (post.video.isNullOrBlank()) {
                mediaImageView.setImageResource(0)
                mediaTextView.text = null
                media.visibility = View.GONE
            } else {
                mediaImageView.setImageResource(R.mipmap.media)
                mediaTextView.text = mediaTextView.context.getString(R.string.media_image)
                media.visibility = View.VISIBLE
            }

            setAuthorAvatar (this, post.authorAvatar)
        }
    }

    private fun setAuthorAvatar (cardPostBinding: CardPostBinding, authorAvatar: String){
        val baseUrl = "http://10.0.2.2:9999"
        val url = "${baseUrl}/avatars/${authorAvatar}"
//        Picasso.get()
//            .load(url)
//            .error(R.drawable.ic_error_100dp)
//            .into(cardPostBinding.avatarImageView);
        Glide.with(cardPostBinding.avatarImageView)
        .load(url)
        .placeholder(R.drawable.ic_loading_100dp)
        .error(R.drawable.ic_error_100dp)
        .timeout(10_000)
        .into(cardPostBinding.avatarImageView)
    }


    private fun setupClickListeners(cardPostBinding: CardPostBinding, post: Post) {
        with(cardPostBinding) {
            likesButton.setOnClickListener { onInteractionListener.onLike(post) }
            shareButton.setOnClickListener { onInteractionListener.onShare(post) }
            media.setOnClickListener { onInteractionListener.onMedia(post) }

            menuButton.setOnClickListener { setupPopupMenu(it, post) }
            postLayout.setOnClickListener { onInteractionListener.onDetails(post) }
        }
    }

    private fun setupPopupMenu(view: View, post: Post) {
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


