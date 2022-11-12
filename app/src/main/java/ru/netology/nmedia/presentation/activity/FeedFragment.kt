package ru.netology.nmedia.presentation.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.domain.dto.Post
import ru.netology.nmedia.presentation.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.presentation.adapter.PostAdapter
import ru.netology.nmedia.presentation.model.FeedModelState
import ru.netology.nmedia.presentation.viewholder.OnInteractionListener
import ru.netology.nmedia.presentation.viewmodel.AuthViewModel
import ru.netology.nmedia.presentation.viewmodel.PostViewModel

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment,
    )

    private var _binding: FragmentFeedBinding? = null
    private val binding: FragmentFeedBinding
        get() = _binding ?: throw RuntimeException("FragmentFeedBinding == null!")

    private val authViewModel: AuthViewModel by activityViewModels()

    private val adapter = PostAdapter(object : OnInteractionListener {
        override fun onLike(post: Post) {
            if (authViewModel.authorized) {
                viewModel.like(post)
            } else {
                authViewModel.signIn()
            }
        }

        override fun onShare(post: Post) {
//            viewModel.share(post.id)
        }

        override fun onEdit(post: Post) {
            viewModel.edit(post)
        }

        override fun onRemove(post: Post) {
            viewModel.removeById(post.id)
        }

        override fun onMedia(post: Post) {
            //startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(post.video)))
            findNavController().navigate(
                FeedFragmentDirections.actionFeedFragmentToMediaFragment(post)
            )
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
        viewModel.refresh()

        binding.postsList.adapter = adapter
        viewModel.dataVisible.observe(viewLifecycleOwner) { state ->
            val newPost = state.posts.size > adapter.currentList.size
            adapter.submitList(state.posts) {
                if (newPost) {
                    binding.postsList.scrollToPosition(0)
                }
            }
            binding.emptyText.isVisible = state.empty
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state is FeedModelState.Loading
            if (state is FeedModelState.Error) {
                Snackbar.make(binding.root, "Error", Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) {
                        viewModel.refresh()
                    }.show()
            }
            binding.swipeRefresh.isRefreshing = state is FeedModelState.Refresh
        }

        viewModel.edited.observe(viewLifecycleOwner) { edited ->
            if (edited.id == 0L) {
                return@observe
            }
            launchEditPost()
        }

        viewModel.newerCount.observe(viewLifecycleOwner) {
            //println("Newer count: $it")
        }

        viewModel.invisibleCount.observe(viewLifecycleOwner) {
            binding.newPostsButton.visibility = if (it > 0) View.VISIBLE else View.INVISIBLE
            println("Invisible count: $it")
        }
    }

    private fun setupListeners() {
        binding.createButton.setOnClickListener {
            if (authViewModel.authorized) {
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            } else {
                authViewModel.signIn()
            }
        }
        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
            binding.swipeRefresh.isRefreshing = false
        }
        binding.newPostsButton.setOnClickListener {
            viewModel.showNewPosts()
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