package ru.netology.mylinledin.activity.event

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
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
import ru.netology.mylinledin.databinding.FragmentNewPostBinding
import ru.netology.mylinledin.model.MediaModel
import ru.netology.mylinledin.util.AndroidUtils
import ru.netology.mylinledin.util.StringArg
import ru.netology.mylinledin.viewModel.EventViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class NewEventFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }


    private val viewModel: EventViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewEventBinding.inflate(
            inflater,
            container,
            false
        )

        val launch =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val data = result.data

                when (result.resultCode) {
                    Activity.RESULT_OK -> {
                        //Image Uri will not be null for RESULT_OK
                        val uri = data?.data!!
                        val file = uri.toFile()

                        viewModel.changeMediaEvents(MediaModel(uri, file))
                    }

                    ImagePicker.RESULT_ERROR -> {
                        Toast.makeText(
                            requireContext(),
                            ImagePicker.getError(data),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {
                        Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }


        arguments?.textArg
            ?.let(binding.editText::setText)


        binding.editDate.text = SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis())

        var cal = Calendar.getInstance()

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "yyyy-MM-dd" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US).toString()
                binding.editDate.text = sdf.format(cal.time)

            }


        binding.pickDate.setOnClickListener {
            DatePickerDialog(
                requireContext(), dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.editTime.text = SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis())
        binding.pickTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)

                val myFormat = "HH:mm:ss"
                binding.editTime.text = SimpleDateFormat(myFormat).format(cal.time).toString()
            }
            TimePickerDialog(
                requireContext(),
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }






        binding.editText.requestFocus()

        activity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.new_post_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.save -> {
                       if(!binding.edit.text.toString().isEmpty() ||
                           !binding.editDate.text.toString().isEmpty()||
                           !binding.editTime.text.toString().isEmpty()) {
                           val time = binding.editDate.text.toString() +"T" +binding.editTime.text.toString()+"Z"
                           viewModel.changeContentEvent(binding.edit.text.toString(), time)
                           viewModel.changeLink(binding.authorLink.text.toString())
                           viewModel.saveEvents()
                           AndroidUtils.hideKeyboard(requireView())
                       } else{
                           Toast.makeText(
                               requireContext(),
                               "Необходимо заполнить поля: Событие и время события",
                               Toast.LENGTH_SHORT
                           )
                       }
                        true
                    }

                    else -> false

                }
            }

        }, viewLifecycleOwner)

        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .galleryOnly()
                .createIntent { launch.launch(it) }
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .cameraOnly()
                .createIntent { launch.launch(it) }
        }



        viewModel.mediaState.observe(viewLifecycleOwner) { mediaState ->
            if (mediaState == null) {
                binding.imagePreviewContainer.isVisible = false
                return@observe
            }
            binding.imagePreviewContainer.isVisible = true
            binding.photoPreview.setImageURI(mediaState.uri)
        }

        binding.remove.setOnClickListener {
            viewModel.changeMediaEvents(null)
        }

        viewModel.eventCreated.observe(viewLifecycleOwner) {
            viewModel.loadEvents()
            findNavController().navigateUp()
        }
        return binding.root
    }
}