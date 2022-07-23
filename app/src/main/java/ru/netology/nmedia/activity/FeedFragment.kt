package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.domain.Post
import ru.netology.nmedia.presentation.OnInteractionListener
import ru.netology.nmedia.presentation.PostAdapter
import ru.netology.nmedia.presentation.PostViewModel

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding: FragmentFeedBinding
        get() = _binding ?: throw RuntimeException("FragmentFeedBinding == null!")

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )
    private val adapter = PostAdapter(object : OnInteractionListener {
        override fun onLike(post: Post) {
            viewModel.like(post.id)
        }

        override fun onShare(post: Post) {
            viewModel.share(post.id)
        }

        override fun onEdit(post: Post) {
            viewModel.edit(post)
            launchEditPost()
        }

        override fun onRemove(post: Post) {
            viewModel.removeById(post.id)
        }

        override fun onMedia(post: Post) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(post.video)))
        }

        override fun onDetails(post: Post) {
            findNavController().navigate(
                FeedFragmentDirections.actionFeedFragmentToDetailsFragment(post)
            )
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )
        observeViewModel()
        setupClickListeners()
        return binding.root
    }

    private fun observeViewModel() {
        binding.postsList.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
        }
        viewModel.edited.observe(viewLifecycleOwner) { edited ->
            if (edited.id == 0L) {
                return@observe
            }
        }
    }

    private fun setupClickListeners() {
        binding.createButton.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }
    }

    private fun launchEditPost() {
        findNavController()
            .navigate(
                R.id.action_feedFragment_to_newPostFragment,
                Bundle().apply {
                    viewModel.edited.value?.content.let {
                        textArg = it
                    }
                }
            )
    }
}