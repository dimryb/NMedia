package ru.netology.nmedia.presentation.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentDetailsBinding
import ru.netology.nmedia.domain.Post
import ru.netology.nmedia.presentation.util.CounterFormatter
import ru.netology.nmedia.presentation.view.loadAuthorAvatar
import ru.netology.nmedia.presentation.view.loadImageMedia
import ru.netology.nmedia.presentation.viewmodel.PostViewModel

class DetailsFragment : Fragment() {
    private val args by navArgs<DetailsFragmentArgs>()

    private val formatter: CounterFormatter by lazy {
        CounterFormatter()
    }

    private var _binding: FragmentDetailsBinding? = null
    private val binding: FragmentDetailsBinding
        get() = _binding ?: throw RuntimeException("FragmentDetailsBinding == null")

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners(args.post)
        setContent(args.post)
        observeViewModel(args.post)
    }

    private fun setContent(post: Post) {
        with(binding) {
            authorTextView.text = post.author
            publishedTextView.text = post.published
            postTextView.text = post.content
            likesButton.text = post.likes.toString()
            likesButton.isChecked = post.likedByMe
            shareButton.text = formatter.counterCompression(post.sharedCount)
            viewsButton.text = formatter.counterCompression(post.viewCount)

            if (post.attachment == null) {
                mediaImageView.setImageResource(0)
                mediaTextView.text = null
                media.visibility = View.GONE
            } else {
                when (post.attachment.type) {
                    "IMAGE" -> {
                        mediaTextView.text = null
                        mediaTextView.visibility = View.GONE
                        media.visibility = View.VISIBLE
                        mediaImageView.loadImageMedia(post.attachment.url)
                    }
                    else -> {
                        mediaImageView.setImageResource(R.mipmap.media)
                        mediaTextView.text = mediaTextView.context.getString(R.string.media_image)
                        media.visibility = View.VISIBLE
                    }
                }
            }

            avatarImageView.loadAuthorAvatar(post.authorAvatar)
        }
    }

    private fun observeViewModel(post: Post) {
        viewModel.data.observe(viewLifecycleOwner) {
            viewModel.data.value?.posts?.find { it.id == post.id }?.let {
                setContent(it)
            }
        }
    }

    private fun setupClickListeners(post: Post) {
        with(binding) {
            likesButton.setOnClickListener {
                viewModel.like(post)
            }
            shareButton.setOnClickListener {
                viewModel.share(post.id)
            }
            media.setOnClickListener {
                //startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(post.video)))
            }
            menuButton.setOnClickListener { setupPopupMenu(it, post) }
        }
    }

    private fun setupPopupMenu(view: View, post: Post) {
        PopupMenu(view.context, view).apply {
            inflate(R.menu.options_post)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.remove -> {
                        viewModel.removeById(post.id)
                        findNavController().navigateUp()
                        true
                    }
                    R.id.edit -> {
                        viewModel.edit(post)
                        findNavController().navigateUp()
                        true
                    }
                    else -> false
                }
            }
        }.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}