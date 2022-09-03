package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentDetailsBinding
import ru.netology.nmedia.domain.Post
import ru.netology.nmedia.presentation.CounterFormatter
import ru.netology.nmedia.presentation.PostViewModel

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
            likesButton.text = post.likesCount.toString()
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
        }
    }

    private fun observeViewModel(post: Post) {
        viewModel.data.observe(viewLifecycleOwner){
//            viewModel.data.value?.find { it.id == post.id }?.let {
//                setContent(it)
//            }
        }
    }

    private fun setupClickListeners(post: Post) {
        with(binding) {
            likesButton.setOnClickListener {
                viewModel.like(post.id)
            }
            shareButton.setOnClickListener {
                viewModel.share(post.id)
            }
            media.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(post.video)))
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