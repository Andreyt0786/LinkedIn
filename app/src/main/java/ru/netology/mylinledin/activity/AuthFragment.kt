package ru.netology.mylinledin.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.mylinledin.R
import ru.netology.mylinledin.adapter.OnInteractionListener
import ru.netology.mylinledin.adapter.PostAdapter
import ru.netology.mylinledin.auth.AppAuth
import ru.netology.mylinledin.databinding.FragmentAuthBinding
import ru.netology.mylinledin.dto.posts.Post
import ru.netology.mylinledin.viewModel.AuthViewModel
import ru.netology.mylinledin.viewModel.IdenticViewModel
import ru.netology.mylinledin.viewModel.PostViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AuthFragment() : Fragment() {

    @Inject
    lateinit var appAuth: AppAuth
    private val identicViewModel: IdenticViewModel by viewModels()
    private val viewModel: PostViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAuthBinding.inflate(
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

        if (!authViewModel.isAuthorized) {
            binding.enter.setOnClickListener {
                identicViewModel.getIdToken(
                    binding.login.text.toString().trim(),
                    binding.pass.text.toString().trim()
                )
            }

            binding.authButtom.setOnClickListener{
                findNavController().navigate(R.id.action_authFragment_to_newUserFragment)
            }

            identicViewModel.tokenServer.observe(viewLifecycleOwner) { state ->
                binding.authGroup.isVisible = state.complete
                binding.newPostGroup.isVisible = state.firstView
                binding.apiErrorGroup.isVisible = state.errorApi
                binding.errorException.isVisible = state.error
                binding.signOut.isVisible = state.signOut
            }

            binding.complete.setOnClickListener {
                //findNavController().navigate(R.id.action_authFragment_to_feedFragment)
                findNavController().navigateUp()
                adapter.refresh()
            }


            binding.errorButtom.setOnClickListener {
                binding.authGroup.isVisible = false
                binding.newPostGroup.isVisible = true
                binding.apiErrorGroup.isVisible = false
                binding.errorException.isVisible = false
                binding.signOut.isVisible = false
            }

            binding.apiErrorButtom.setOnClickListener {
                binding.authGroup.isVisible = false
                binding.newPostGroup.isVisible = true
                binding.apiErrorGroup.isVisible = false
                binding.errorException.isVisible = false
                binding.signOut.isVisible = false
            }
        } else {

            binding.authGroup.isVisible = false
            binding.newPostGroup.isVisible = false
            binding.apiErrorGroup.isVisible = false
            binding.errorException.isVisible = false
            binding.signOut.isVisible = true

            binding.No.setOnClickListener {
                //findNavController().navigate(R.id.action_authFragment_to_feedFragment)
                findNavController().navigateUp()
            }

            binding.Yes.setOnClickListener {
                appAuth.removeUser()
              //  findNavController().navigate(R.id.action_authFragment_to_feedFragment)
                findNavController().navigateUp()
                adapter.refresh()
            }
        }

        return binding.root
    }
}