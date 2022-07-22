package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewPostBinding

class NewPostFragment : Fragment() {

    private var _binding: FragmentNewPostBinding? = null
    private val binding: FragmentNewPostBinding
        get() = _binding ?: throw RuntimeException("FragmentNewPostBinding == null")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )
        //binding.contentEditText.setText(intent.getStringExtra(Intent.EXTRA_TEXT))

        setupClickListeners()

        return binding.root
    }

    private fun setupClickListeners() {

        binding.saveButton.setOnClickListener {
            val intent = Intent()
            if (binding.contentEditText.text.isNullOrBlank()) {
                Toast.makeText(
                    it.context,
                    getString(R.string.empty_post_error),
                    Toast.LENGTH_SHORT
                ).show()
                activity?.setResult(Activity.RESULT_CANCELED, intent)
            } else {
                intent.putExtra(Intent.EXTRA_TEXT, binding.contentEditText.text.toString())
                activity?.setResult(Activity.RESULT_OK, intent)
            }
            findNavController().navigateUp()
        }
    }

    companion object {
        private const val TEXT_KEY = "TEXT_KEY"
        var Bundle.textArg: String?
            set(value) = putString(TEXT_KEY, value)
            get() = getString(TEXT_KEY)
    }
}