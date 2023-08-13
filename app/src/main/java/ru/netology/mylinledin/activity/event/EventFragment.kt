package ru.netology.mylinledin.activity.event

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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.mylinledin.R
import ru.netology.mylinledin.activity.PhotoFragment.Companion.textArg
import ru.netology.mylinledin.adapter.event.EventAdapter
import ru.netology.mylinledin.adapter.event.OnInteractionListenerEvent
import ru.netology.mylinledin.databinding.FragmentFeedEventBinding
import ru.netology.mylinledin.dto.event.Event
import ru.netology.mylinledin.viewModel.AuthViewModel
import ru.netology.mylinledin.viewModel.EventViewModel

@AndroidEntryPoint
class EventFragment : Fragment() {


    private val viewModel: EventViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedEventBinding.inflate(inflater, container, false)


        val adapter = EventAdapter(object : OnInteractionListenerEvent {

            override fun onEdit(event: Event) {
                viewModel.editEvent(event)
            }

            override fun onLike(event: Event) {
                if (authViewModel.isAuthorized) {
                    viewModel.likeByIdEvent(event)
                } else {
                    findNavController().navigate(
                        R.id.action_bottomNavigationFragment_to_authFragment
                    )
                }
            }

            override fun previewPhoto(event: Event) {
                findNavController().navigate(
                    R.id.action_bottomNavigationFragment_to_photoFragment,
                    Bundle().apply { textArg = event.attachment?.url })
            }

            override fun onRemove(event: Event) {
                viewModel.removeByIdEvent(event.id)
            }

            override fun playVideo(event: Event) {
                findNavController().navigate(
                    R.id.action_bottomNavigationFragment_to_mediaFragment,
                    Bundle().apply { textArg = event.attachment?.url })
            }

            override fun playMusic(event: Event) {
                findNavController().navigate(R.id.action_bottomNavigationFragment_to_musicFragment,
                    Bundle().apply { textArg = event.attachment?.url })
            }

            override fun onShare(event: Event) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, event.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }
        })

        viewModel.edited.observe(viewLifecycleOwner) { event ->
            if (event.id == 0) {
                return@observe
            }
            findNavController().navigate(R.id.action_bottomNavigationFragment_to_newEventFragment,
                Bundle().apply { textArg = event.content })

        }

        binding.list.adapter = adapter
        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.refreshView.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadEvents() }
                    .show()
            }
        }

        var currentMenuProvider: MenuProvider? = null

        authViewModel.authLiveData.observe(viewLifecycleOwner) {

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
                            findNavController().navigate(R.id.action_bottomNavigationFragment_to_authFragment)
                            true
                        }

                        R.id.singUp -> {
                            findNavController().navigate(R.id.action_bottomNavigationFragment_to_newUserFragment)
                            true
                        }

                        R.id.singOut -> {
                            findNavController().navigate(R.id.action_bottomNavigationFragment_to_authFragment)
                            true
                        }

                        else -> false
                    }
                }
            }.also { currentMenuProvider = it }, viewLifecycleOwner)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.data.collectLatest {
                    adapter.submitData(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collectLatest { state ->
                    binding.refreshView.isRefreshing =
                        state.refresh is LoadState.Loading ||
                                state.append is LoadState.Loading
                }
            }
        }

        binding.refreshView.setOnRefreshListener {
            adapter.refresh()
        }

        binding.fab.setOnClickListener {
            if (authViewModel.isAuthorized) {
                findNavController().navigate(R.id.action_bottomNavigationFragment_to_newEventFragment)
            } else {
                findNavController().navigate(R.id.action_bottomNavigationFragment_to_authFragment)
            }

        }


        return binding.root

    }
}

