package ru.netology.mylinledin.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import ru.netology.mylinledin.R
import ru.netology.mylinledin.activity.PhotoFragment.Companion.textArg
import ru.netology.mylinledin.databinding.FragmentMusicBinding
import ru.netology.mylinledin.databinding.FragmentPhotoBinding
import ru.netology.mylinledin.mediaPlayer.MediaLifecycleObserver
import ru.netology.mylinledin.util.StringArg

class MusicFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }
    private val mediaObserver = MediaLifecycleObserver()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMusicBinding.inflate(
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

        lifecycle.addObserver(mediaObserver)

        val urlImage = "${"https://netomedia.ru"}/media/${arguments?.textArg}"
        binding.play.setOnClickListener {
            mediaObserver.apply {
                player?.setDataSource(
                    urlImage
                )
            }.play()
        }

        return binding.root
    }
}