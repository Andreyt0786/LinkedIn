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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.mylinledin.R
import ru.netology.mylinledin.databinding.FragmentNewPostSendBinding
import ru.netology.mylinledin.util.AndroidUtils
import ru.netology.mylinledin.util.StringArg
import ru.netology.mylinledin.viewModel.PostViewModel


@AndroidEntryPoint
class NewSendPostFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostSendBinding.inflate(
            inflater,
            container,
            false
        )


        val text = activity?.intent?.getStringExtra(Intent.EXTRA_TEXT)

        activity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.new_post_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.save -> {
                        if (text != null) {
                            viewModel.changeContent(binding.edit.text.toString())
                            viewModel.save()
                            AndroidUtils.hideKeyboard(requireView())
                        } else {
                            findNavController().navigateUp()
                        }
                        true
                    }

                    else -> false

                }
            }
        }, viewLifecycleOwner)


        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.loadPosts()
            findNavController().navigateUp()
        }
        return binding.root
    }
}