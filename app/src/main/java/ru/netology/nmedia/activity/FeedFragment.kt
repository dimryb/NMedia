package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.launch
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.domain.Post
import ru.netology.nmedia.presentation.*

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding: FragmentFeedBinding
        get() = _binding ?: throw RuntimeException("FragmentFeedBinding == null!")

    //private lateinit var newPostLauncher: ActivityResultLauncher<Unit>
    //private lateinit var editPostLauncher: ActivityResultLauncher<String>
    private val viewModel: PostViewModel by viewModels()
    private val adapter = PostAdapter(object : OnInteractionListener {
        override fun onLike(post: Post) {
            viewModel.like(post.id)
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
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(post.video)))
        }
    })

    private fun editCallback(text: String?) {
        text ?: return
        viewModel.editContent(text)
        viewModel.save()
    }

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //newPostLauncher = registerForActivityResult(NewPostActivityContract(), ::editCallback)
        //editPostLauncher = registerForActivityResult(EditPostActivityContract(), ::editCallback)
        observeViewModel()
        setupClickListeners()
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
            //editPostLauncher.launch(edited.content)
        }
    }

    private fun setupClickListeners() {
        binding.createButton.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }
    }
}