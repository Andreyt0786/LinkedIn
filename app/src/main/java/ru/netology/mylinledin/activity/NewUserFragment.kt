package ru.netology.mylinledin.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.mylinledin.adapter.OnInteractionListener
import ru.netology.mylinledin.adapter.PostAdapter
import ru.netology.mylinledin.databinding.FragmentNewUserBinding
import ru.netology.mylinledin.dto.posts.Post
import ru.netology.mylinledin.viewModel.IdenticViewModel

@AndroidEntryPoint
class NewUserFragment() : Fragment() {

    private val identicViewModel: IdenticViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewUserBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = PostAdapter(object : OnInteractionListener {

            override fun previewPhoto(post: Post) {

            }

            override fun onEdit(post: Post) {

            }

            override fun onLike(post: Post) {

            }

            override fun onRemove(post: Post) {

            }

            override fun onShare(post: Post) {

            }
        })


        binding.enter.setOnClickListener {
            if (binding.pass1.text.toString().trim() == binding.pass2.text.toString().trim()) {
                identicViewModel.getNewUser(
                    binding.login.text.toString().trim(),
                    binding.pass1.text.toString().trim(),
                    binding.name.text.toString().trim(),
                )
            } else {
                binding.authGroup.isVisible = false
                binding.newPostGroup.isVisible = false
                binding.apiErrorGroup.isVisible = false
                binding.errorException.isVisible = false
                binding.errorPassword.isVisible = true
            }
        }


        identicViewModel.newTokenServer.observe(viewLifecycleOwner) { state ->
            binding.authGroup.isVisible = state.complete
            binding.newPostGroup.isVisible = state.firstView
            binding.apiErrorGroup.isVisible = state.errorApi
            binding.errorException.isVisible = state.error
            binding.errorPassword.isVisible = state.errorPass
        }

        binding.complete.setOnClickListener {
            findNavController().navigateUp()
            adapter.refresh()
        }


        binding.errorButtom.setOnClickListener {
            binding.authGroup.isVisible = false
            binding.newPostGroup.isVisible = true
            binding.apiErrorGroup.isVisible = false
            binding.errorException.isVisible = false
            binding.errorPassword.isVisible = false
        }

        binding.apiErrorButtom.setOnClickListener {
            binding.authGroup.isVisible = false
            binding.newPostGroup.isVisible = true
            binding.apiErrorGroup.isVisible = false
            binding.errorException.isVisible = false
            binding.errorPassword.isVisible = false
        }

        binding.errorButtomPass.setOnClickListener {
            binding.authGroup.isVisible = false
            binding.newPostGroup.isVisible = true
            binding.apiErrorGroup.isVisible = false
            binding.errorException.isVisible = false
            binding.errorPassword.isVisible = false
        }
        return binding.root
    }
}