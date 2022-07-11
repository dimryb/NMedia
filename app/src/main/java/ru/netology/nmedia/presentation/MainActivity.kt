package ru.netology.nmedia.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.domain.Post
import ru.netology.nmedia.util.AndroidUtils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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
            binding.content.setText(edited.content)
            binding.content.requestFocus()
        }
    }

    private fun setupClickListeners() {
        binding.save.setOnClickListener {
            if (binding.content.text.isNullOrBlank()) {
                Toast.makeText(it.context, getString(R.string.empty_post_error), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val text = binding.content.text.toString()

            viewModel.editContent(text)
            viewModel.save()

            binding.content.clearFocus()
            AndroidUtils.hideKeyboard(binding.content)
            binding.content.setText("")
        }
    }
}