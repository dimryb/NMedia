package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.domain.Post
import ru.netology.nmedia.presentation.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var newPostLauncher: ActivityResultLauncher<Unit>
    private lateinit var editPostLauncher: ActivityResultLauncher<String>
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
    })

    private fun editCallback(text: String?){
        text ?: return
        viewModel.editContent(text)
        viewModel.save()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        newPostLauncher = registerForActivityResult(NewPostActivityContract(), ::editCallback)
        editPostLauncher = registerForActivityResult(EditPostActivityContract(), ::editCallback)

        setContentView(binding.root)
        observeViewModel()
        setupClickListeners()
    }

    private fun observeViewModel() {
        binding.postsList.adapter = adapter
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }
        viewModel.edited.observe(this) { edited ->
            if (edited.id == 0L){
                return@observe
            }
            editPostLauncher.launch(edited.content)
        }
    }

    private fun setupClickListeners() {
        binding.createButton.setOnClickListener{
            newPostLauncher.launch()
        }
    }
}