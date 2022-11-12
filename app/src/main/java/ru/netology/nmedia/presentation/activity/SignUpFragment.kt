package ru.netology.nmedia.presentation.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSignUpBinding
import ru.netology.nmedia.di.DependencyContainer
import ru.netology.nmedia.presentation.viewmodel.AuthViewModel
import ru.netology.nmedia.presentation.viewmodel.ViewModelFactory

class SignUpFragment : Fragment() {

    private val dependencyContainer = DependencyContainer.getInstance()

    private var _binding: FragmentSignUpBinding? = null
    private val binding: FragmentSignUpBinding
        get() = _binding ?: throw RuntimeException("SignUpFragment == null")

    private val viewModel: AuthViewModel by activityViewModels(
        factoryProducer = {
            ViewModelFactory(
                dependencyContainer.postRepository,
                dependencyContainer.authRepository,
                dependencyContainer.appAuth
            )
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        setupClickListeners()
        return binding.root
    }

    private fun setupClickListeners() {
        with(binding) {
            signInButton.setOnClickListener {
                viewModel.registerUser(
                    fieldLogin.text.toString(),
                    fieldPassword.text.toString(),
                    fieldRepeatPassword.text.toString(),
                    fieldName.text.toString()
                )
            }
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.registerError.observe(viewLifecycleOwner) {
            Toast.makeText(context, R.string.register_error, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}