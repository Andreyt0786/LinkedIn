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
import ru.netology.mylinledin.databinding.FragmentPhotoBinding
import ru.netology.mylinledin.util.StringArg

class PhotoFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPhotoBinding.inflate(
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

        val urlImage = arguments?.textArg
        Glide.with(binding.photoPreview)
            .load(urlImage)
            .timeout(10000)
            .circleCrop()
            .into(binding.photoPreview)

        return binding.root
    }
}