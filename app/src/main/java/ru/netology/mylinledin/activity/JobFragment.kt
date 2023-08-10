package ru.netology.mylinledin.activity.job

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
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.mylinledin.R
import ru.netology.mylinledin.activity.MediaFragment.Companion.textArg
import ru.netology.mylinledin.activity.PhotoFragment.Companion.textArg
import ru.netology.mylinledin.activity.wall.WallFragment.Companion.textArg
import ru.netology.mylinledin.adapter.JobAdapter
import ru.netology.mylinledin.adapter.OnInteractionListener
import ru.netology.mylinledin.adapter.OnteractionListener
import ru.netology.mylinledin.adapter.wall.InteractionListener
import ru.netology.mylinledin.adapter.wall.WallAdapter
import ru.netology.mylinledin.auth.AppAuth
import ru.netology.mylinledin.databinding.FragmentJobBinding
import ru.netology.mylinledin.databinding.FragmentWallBinding
import ru.netology.mylinledin.dto.Job.Job
import ru.netology.mylinledin.dto.posts.Post
import ru.netology.mylinledin.util.StringArg
import ru.netology.mylinledin.viewModel.AuthViewModel
import ru.netology.mylinledin.viewModel.IdenticViewModel
import ru.netology.mylinledin.viewModel.JobViewModel
import ru.netology.mylinledin.viewModel.WallViewModel
import javax.inject.Inject

@AndroidEntryPoint
class JobFragment : Fragment() {

    @Inject
    lateinit var appAuth: AppAuth

    private val viewModel: JobViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by viewModels()

    companion object {
        var Bundle.textArg: String? by StringArg
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentJobBinding.inflate(inflater, container, false)

        val adapter = JobAdapter(object : OnteractionListener {

            override fun onRemove(job: Job) {
                viewModel.removeById(job.id)
            }
        })


        binding.list.adapter = adapter
        if (arguments?.textArg.isNullOrEmpty() ||
            appAuth.authStateFlow.value.id == arguments?.textArg!!.toInt()
        ) {
            viewModel.loadMyJobs()
        } else {
            viewModel.loadUserJobs(arguments?.textArg!!.toInt())
        }
        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.refreshView.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) {
                        if (arguments?.textArg.isNullOrEmpty()
                            || appAuth.authStateFlow.value.id == arguments?.textArg!!.toInt()
                        ) {
                            viewModel.loadMyJobs()
                        } else {
                            viewModel.loadUserJobs(arguments?.textArg!!.toInt())
                        }
                    }
                    .show()
            }
        }
        if (arguments?.textArg.isNullOrEmpty() ||
            appAuth.authStateFlow.value.id == arguments?.textArg!!.toInt()
        ) {
            viewModel.dataMyJob.observe(viewLifecycleOwner)
            { state ->
                adapter.submitList(state.jobs) {
                    binding.list.smoothScrollToPosition(0)
                    binding.emptyText.isVisible = state.empty
                }
            }
        } else {
            viewModel.dataUserJob.observe(viewLifecycleOwner)
            { state ->
                adapter.submitList(state.jobs) {
                    binding.list.smoothScrollToPosition(0)
                    binding.emptyText.isVisible = state.empty
                }
            }
        }

        binding.fab.isVisible = arguments?.textArg.isNullOrEmpty()
                || appAuth.authStateFlow.value.id == arguments?.textArg!!.toInt()

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_jobFragment_to_newJobFragment)
        }
        return binding.root
    }
}