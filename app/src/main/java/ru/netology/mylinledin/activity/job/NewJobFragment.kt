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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

        binding.startJob.text = SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis())
        binding.pickTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)

                val myFormat = "HH:mm:ss"
                binding.startJob.text = SimpleDateFormat(myFormat).format(cal.time).toString()
            }
            TimePickerDialog(
                requireContext(),
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }

        binding.editFinishDate.text = SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis())

        binding.pickFinishDate.setOnClickListener {
            val dateSetListener =
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val myFormat = "yyyy-MM-dd" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US).toString()
                    binding.editFinishDate.text = sdf.format(cal.time)

                }
            DatePickerDialog(
                requireContext(), dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.finishTimeJob.text = SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis())
        binding.pickfinishTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)

                val myFormat = "HH:mm:ss"
                binding.finishTimeJob.text = SimpleDateFormat(myFormat).format(cal.time).toString()
            }
            TimePickerDialog(
                requireContext(),
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }




        binding.nameJob.requestFocus()

        activity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.new_post_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.save -> {
                        val timeFinish = binding.editFinishDate.text.toString() +"T" +binding.finishTimeJob.text.toString()+"Z"
                        val time = binding.editDate.text.toString() +"T" +binding.startJob.text.toString()+"Z"
                        if (!binding.nameJob.text.toString().isNullOrEmpty() ||
                            !binding.positionJob.text.toString().isNullOrEmpty() ||
                            !time.isNullOrEmpty()
                        ) {
                            viewModel.changeJob(
                                binding.nameJob.text.toString(),
                                binding.positionJob.text.toString(),
                              time
                            )
                            viewModel.changeFinish(timeFinish)
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