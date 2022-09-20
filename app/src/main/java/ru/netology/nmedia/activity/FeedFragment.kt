package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
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
            viewModel.like(post)
        }

        override fun onShare(post: Post) {
            viewModel.share(post.id)
        }

        override fun onEdit(post: Post) {
            viewModel.edit(post)
        }

        override fun onRemove(post: Post) {
            viewModel.removeById(post.id)
        }

        override fun onMedia(post: Post) {
            //startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(post.video)))
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
        setupListeners()
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun observeViewModel() {
        binding.postsList.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            binding.progress.isVisible = if (state.swipeRefresh) false else state.loading
            binding.errorGroup.isVisible = state.error
            binding.emptyText.isVisible = state.empty
        }
        viewModel.edited.observe(viewLifecycleOwner) { edited ->
            if (edited.id == 0L) {
                return@observe
            }
            launchEditPost()
        }
    }

    private fun setupListeners() {
        binding.createButton.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }
        binding.retryButton.setOnClickListener {
            viewModel.loadPosts(swipeRefresh = false)
        }
        binding.swiperefresh.setOnRefreshListener {
            viewModel.loadPosts(swipeRefresh = true)
            binding.swiperefresh.isRefreshing = false
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