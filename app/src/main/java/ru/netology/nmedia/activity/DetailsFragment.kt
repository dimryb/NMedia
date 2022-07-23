package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentDetailsBinding
import ru.netology.nmedia.domain.Post
import ru.netology.nmedia.presentation.CounterFormatter

class DetailsFragment : Fragment() {
    private val args by navArgs<DetailsFragmentArgs>()

    private val formatter: CounterFormatter by lazy {
        CounterFormatter()
    }

    private var _binding: FragmentDetailsBinding? = null
    private val binding: FragmentDetailsBinding
        get() = _binding ?: throw RuntimeException("FragmentDetailsBinding == null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setContent(args.post)
    }

    private fun setupClickListeners() {

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
}