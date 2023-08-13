package ru.netology.mylinledin.activity

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.mylinledin.R
import ru.netology.mylinledin.databinding.FragmentMediaBinding
import ru.netology.mylinledin.util.StringArg

@AndroidEntryPoint
class MediaFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMediaBinding.inflate(
            inflater,
            container,
            false
        )


        activity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.new_post_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.save -> {
                        findNavController().navigateUp()
                        true
                    }

                    else -> false

                }
            }

        }, viewLifecycleOwner)


        binding.video.setOnClickListener {
            binding.video.apply {
                setMediaController(MediaController(context))
                setVideoURI(
                    Uri.parse(arguments?.textArg)
                )
                setOnPreparedListener {
                    start()
                }
                setOnCompletionListener {
                    stopPlayback()
                }
            }
        }
        return binding.root
    }
}