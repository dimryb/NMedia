package ru.netology.nmedia.presentation.viewholder

import android.view.View
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.domain.dto.Post
import ru.netology.nmedia.domain.enumeration.AttachmentType
import ru.netology.nmedia.presentation.util.CounterFormatter
import ru.netology.nmedia.presentation.view.loadAuthorAvatar
import ru.netology.nmedia.presentation.view.loadImageMedia
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.TimeUtils

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

    private fun setLocalButton(cardPostBinding: CardPostBinding, post: Post){
        with(cardPostBinding) {
            localButton.visibility = if (post.ownerByMe) View.VISIBLE else View.INVISIBLE
            localButton.setIconResource(
                if (post.isLocal) R.drawable.ic_local else R.drawable.ic_not_local
            )
        }
    }

    private fun setContent(cardPostBinding: CardPostBinding, post: Post) {
        with(cardPostBinding) {
            authorTextView.text = post.author
            val dateTime = TimeUtils.dateTimeFromEpochSecond(post.published)
            publishedTextView.text = TimeUtils.dataFormatted(dateTime)
            postTextView.text = post.content
            likesButton.text = post.likes.toString()
            likesButton.isChecked = post.likedByMe
            shareButton.text = formatter.counterCompression(post.sharedCount)
            viewsButton.text = formatter.counterCompression(post.viewCount)
            setLocalButton(cardPostBinding, post)

            if (post.attachment == null) {
                mediaImageView.setImageResource(0)
                mediaTextView.text = null
                media.visibility = View.GONE
            } else {
                when (post.attachment.type) {
                    AttachmentType.IMAGE -> {
                        mediaTextView.text = null
                        mediaTextView.visibility = View.GONE
                        media.visibility = View.VISIBLE

                        mediaImageView.loadImageMedia(post.attachment.url)
                    }
                }
            }

            avatarImageView.loadAuthorAvatar(post.authorAvatar)
        }
    }

    private fun setupClickListeners(cardPostBinding: CardPostBinding, post: Post) {
        with(cardPostBinding) {
            likesButton.setOnClickListener { onInteractionListener.onLike(post) }
            shareButton.setOnClickListener { onInteractionListener.onShare(post) }
            media.setOnClickListener { onInteractionListener.onMedia(post) }

            menuButton.isVisible = post.ownerByMe
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


