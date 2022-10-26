package ru.netology.nmedia.presentation.activity

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.presentation.viewmodel.PostViewModel
import ru.netology.nmedia.util.AndroidUtils

class NewPostFragment : Fragment() {

    private var _binding: FragmentNewPostBinding? = null
    private val binding: FragmentNewPostBinding
        get() = _binding ?: throw RuntimeException("FragmentNewPostBinding == null")

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.create_posts_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId){
                    R.id.save -> {
                        viewModel.editContent(binding.contentEditText.text.toString())
                        viewModel.save()
                        AndroidUtils.hideKeyboard(requireView())
                        true
                    }
                    else -> false
                }

        }, viewLifecycleOwner)

        _binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )
        arguments?.textArg?.let(binding.contentEditText::setText)
        setupClickListeners()

        viewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupClickListeners() {


    }

    companion object {
        private const val TEXT_KEY = "TEXT_KEY"
        var Bundle.textArg: String?
            set(value) = putString(TEXT_KEY, value)
            get() = getString(TEXT_KEY)
    }
}