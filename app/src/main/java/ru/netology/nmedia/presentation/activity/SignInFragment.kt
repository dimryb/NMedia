package ru.netology.nmedia.presentation.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import ru.netology.nmedia.databinding.FragmentSignInBinding
import ru.netology.nmedia.domain.dto.Token
import ru.netology.nmedia.presentation.viewmodel.AuthViewModel
import ru.netology.nmedia.presentation.viewmodel.PostViewModel

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding: FragmentSignInBinding
        get() = _binding ?: throw RuntimeException("FragmentMediaBinding == null")

//    private val viewModel: PostViewModel by viewModels(
//        ownerProducer = ::requireParentFragment
//    )

    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        setupClickListeners()
        return binding.root
    }

    private fun setupClickListeners() {
        with(binding){
            signInButton.setOnClickListener {
                viewModel.updateUser(
                    fieldLogin.text.toString(),
                    fieldPassword.text.toString()
                )
            }
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.token.observe(viewLifecycleOwner){ token ->
            println("Token ${token.id} ${token.token}")

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}