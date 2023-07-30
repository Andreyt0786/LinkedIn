package ru.netology.mylinledin.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.mylinledin.R
import ru.netology.mylinledin.activity.MediaFragment.Companion.textArg
import ru.netology.mylinledin.adapter.OnInteractionListener
import ru.netology.mylinledin.adapter.PostAdapter
import ru.netology.mylinledin.adapter.PostLoadingStateAdapter
import ru.netology.mylinledin.auth.AppAuth
import ru.netology.mylinledin.databinding.FragmentFeedBinding
import ru.netology.mylinledin.dto.posts.Post
import ru.netology.mylinledin.viewModel.AuthViewModel
import ru.netology.mylinledin.viewModel.IdenticViewModel
import ru.netology.mylinledin.viewModel.PostViewModel
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment : Fragment() {


    private val viewModel: PostViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)


        val adapter = PostAdapter(object : OnInteractionListener {

            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }

            override fun onLike(post: Post) {
                if (authViewModel.isAuthorized) {
                    viewModel.likeById(post)
                } else {
                    findNavController().navigate(R.id.action_feedFragment_to_authFragment)
                }
            }

            override fun previewPhoto(post: Post) {
                findNavController().navigate(R.id.action_feedFragment_to_photoFragment,
                    Bundle().apply { textArg = post.attachment?.url })
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun playVideo(post: Post) {
                findNavController().navigate(R.id.action_feedFragment_to_mediaFragment,
                    Bundle().apply { textArg = post.attachment?.url })
            }

            override fun playMusic(post: Post) {
                findNavController().navigate(R.id.action_feedFragment_to_musicFragment,
                    Bundle().apply { textArg = post.attachment?.url })
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }
        })

        binding.list.adapter = adapter
        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.refreshView.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadPosts() }
                    .show()
            }
        }

        var currentMenuProvider: MenuProvider? = null

        authViewModel.authLiveData.observe(viewLifecycleOwner) { //authModel -> почему то подсвечивает

            currentMenuProvider?.let(requireActivity()::removeMenuProvider)
            requireActivity().addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.auth_menu, menu)
                    menu.setGroupVisible(R.id.authorized, authViewModel.isAuthorized)
                    menu.setGroupVisible(R.id.unAuthorized, !authViewModel.isAuthorized)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.singIn -> {
                            findNavController().navigate(R.id.action_feedFragment_to_authFragment)
                            true
                        }

                        R.id.singUp -> {
                            findNavController().navigate(R.id.action_feedFragment_to_newUserFragment)
                            true
                        }

                        R.id.singOut -> {
                            findNavController().navigate(R.id.action_feedFragment_to_authFragment)
                            true
                        }

                        else -> false
                    }
                }
            }.also { currentMenuProvider = it }, viewLifecycleOwner)
        }

        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest {
                adapter.submitData(it)
            }
        }
/*
        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PostLoadingStateAdapter {
                adapter.retry()
            },
            footer = PostLoadingStateAdapter {
                adapter.retry()
            }
        )*/
        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { state ->
                binding.refreshView.isRefreshing =
                    state.refresh is LoadState.Loading
            }
        }

        binding.refreshView.setOnRefreshListener {
            adapter.refresh()
        }

        binding.fab.setOnClickListener {
          //  if (authViewModel.isAuthorized) {
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
          //  } else {
          //      findNavController().navigate(R.id.action_feedFragment_to_authFragment)
           // }

        }


        return binding.root

    }
}