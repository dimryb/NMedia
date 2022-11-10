package ru.netology.nmedia.presentation.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.databinding.FragmentMediaBinding
import ru.netology.nmedia.domain.dto.Post
import ru.netology.nmedia.presentation.view.loadImageMedia
import ru.netology.nmedia.presentation.viewmodel.PostViewModel

@AndroidEntryPoint
class MediaFragment : Fragment() {

    private val args by navArgs<MediaFragmentArgs>()

    private var _binding: FragmentMediaBinding? = null
    private val binding: FragmentMediaBinding
        get() = _binding ?: throw RuntimeException("FragmentMediaBinding == null")

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setContent(args.post)
    }

    private fun setContent(post: Post) {
        with(binding) {
            post.attachment?.let { attachment ->
                mediaImageView.loadImageMedia(attachment.url)
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}