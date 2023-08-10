package ru.netology.mylinledin.activity.event

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.mylinledin.R
import ru.netology.mylinledin.databinding.FragmentNewEventBinding
import ru.netology.mylinledin.databinding.FragmentNewJobBinding
import ru.netology.mylinledin.databinding.FragmentNewPostBinding
import ru.netology.mylinledin.model.MediaModel
import ru.netology.mylinledin.util.AndroidUtils
import ru.netology.mylinledin.util.StringArg
import ru.netology.mylinledin.viewModel.EventViewModel
import ru.netology.mylinledin.viewModel.JobViewModel

@AndroidEntryPoint
class NewJobFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }


    private val viewModel: JobViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewJobBinding.inflate(
            inflater,
            container,
            false
        )



        binding.nameJob.requestFocus()

        activity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.new_post_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.save -> {
                        if (!binding.nameJob.text.toString().isNullOrEmpty() ||
                            !binding.positionJob.text.toString().isNullOrEmpty() ||
                            !binding.startJob.text.toString().isNullOrEmpty()
                        ) {
                            viewModel.changeJob(
                                binding.nameJob.text.toString(),
                                binding.positionJob.text.toString(),
                                binding.startJob.text.toString()
                            )
                            viewModel.changeFinish(binding.finishJob.text.toString())
                            viewModel.changeLink(binding.linkJob.text.toString())
                            viewModel.save()
                            AndroidUtils.hideKeyboard(requireView())
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Необходимо заполнить поля название работы, позицию и начало работы",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                        true
                    }

                    else -> false

                }
            }

        }, viewLifecycleOwner)



     /*   binding.remove.setOnClickListener {
            viewModel.changeMediaEvents(null)
        }*/

        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.loadMyJobs()
            findNavController().navigateUp()
        }
        return binding.root
    }
}