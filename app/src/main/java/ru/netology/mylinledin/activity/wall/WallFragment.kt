package ru.netology.mylinledin.activity.wall

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
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.mylinledin.R
import ru.netology.mylinledin.activity.MediaFragment.Companion.textArg
import ru.netology.mylinledin.activity.PhotoFragment.Companion.textArg
import ru.netology.mylinledin.adapter.OnInteractionListener
import ru.netology.mylinledin.adapter.wall.InteractionListener
import ru.netology.mylinledin.adapter.wall.WallAdapter
import ru.netology.mylinledin.auth.AppAuth
import ru.netology.mylinledin.databinding.FragmentWallBinding
import ru.netology.mylinledin.dto.posts.Post
import ru.netology.mylinledin.util.StringArg
import ru.netology.mylinledin.viewModel.AuthViewModel
import ru.netology.mylinledin.viewModel.IdenticViewModel
import ru.netology.mylinledin.viewModel.WallViewModel
import javax.inject.Inject

@AndroidEntryPoint
class WallFragment : Fragment() {

    @Inject
    lateinit var appAuth: AppAuth

    private val viewModel: WallViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val identicViewModel: IdenticViewModel by viewModels()

    companion object {
        var Bundle.textArg: String? by StringArg
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentWallBinding.inflate(inflater, container, false)

        var count = 0
        val adapter = WallAdapter(object : InteractionListener {

            override fun previewPhoto(post: Post) {
              /*  findNavController().navigate(R.id.action_feedFragment_to_photoFragment,
                    Bundle().apply { textArg = post.attachment?.url })*/
            }

            override fun onEdit(post: Post) {

            }

            override fun onLike(post: Post) {
              /*  if (authViewModel.isAuthorized) {
                    viewModel.likeById(post)
                } else {
                    findNavController().navigate(R.id.action_feedFragment_to_authFragment)
                }*/
            }

            override fun onRemove(post: Post) {

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
        viewModel.loadPosts(arguments?.textArg!!.toInt())
        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.refreshView.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadPosts(arguments?.textArg!!.toInt()) }
                    .show()
            }
        }

        viewModel.data.observe(viewLifecycleOwner)
        { state ->
            adapter.submitList(state.posts) {
                binding.list.smoothScrollToPosition(0)
            }
            binding.emptyText.isVisible = state.empty

        }
        //  var currentMenuProvider: MenuProvider? = null

        /*   authViewModel.authLiveData.observe(viewLifecycleOwner) { //authModel -> почему то подсвечивает

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
                               appAuth.setUser(AuthModel(5, "x-token"))
                               true
                           }

                           R.id.singOut -> {
                               findNavController().navigate(R.id.action_feedFragment_to_signOutFragment)
                               true
                           }

                           else -> false
                       }
                   }
               }.also { currentMenuProvider = it }, viewLifecycleOwner)
           }
   */


        /*viewModel.newer.observe(viewLifecycleOwner)
        {
            count = it
            if (count == 0) {
                binding.newPostGroup.isVisible = false
            } else if (count > 0) {
                binding.newPostGroup.isVisible = true
            }
        }*/

        /* binding.getNewPost.setOnClickListener {
             viewModel.updateFromDao()
             binding.newPostGroup.isVisible = false
             count = 0
         }

         binding.refreshView.setOnRefreshListener {
             viewModel.refreshPosts()
         }*/

        /*  binding.fab.setOnClickListener {
              if (authViewModel.isAuthorized) {
                  findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
              } else {
                  findNavController().navigate(R.id.action_feedFragment_to_authFragment)
              }*/



        return binding.root
    }
}