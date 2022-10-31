package ru.netology.nmedia.presentation.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.netology.nmedia.databinding.FragmentSignInBinding
import ru.netology.nmedia.presentation.viewmodel.PostViewModel

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding: FragmentSignInBinding
        get() = _binding ?: throw RuntimeException("FragmentMediaBinding == null")

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        setupClickListeners()
        return binding.root
    }

    private fun setupClickListeners() {
        binding.signInButton.setOnClickListener {
            println("Sign In: Login: ${binding.fieldLogin.text}, Password: ${binding.fieldPassword.text} ")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}