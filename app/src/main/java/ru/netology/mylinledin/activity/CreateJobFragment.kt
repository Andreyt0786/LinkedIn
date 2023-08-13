package ru.netology.mylinledin.activity

import android.annotation.SuppressLint
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
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.mylinledin.R
import ru.netology.mylinledin.databinding.FragmentNewJobBinding
import ru.netology.mylinledin.util.AndroidUtils
import ru.netology.mylinledin.util.StringArg
import ru.netology.mylinledin.viewModel.JobViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class CreateJobFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }


    private val viewModel: JobViewModel by activityViewModels()
    @SuppressLint("SimpleDateFormat")
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

        binding.pickDate.setOnClickListener {
            val calendar = Calendar.getInstance()

            val dateSetListener =
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, monthOfYear)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val myFormat = "yyyy-MM-dd" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US).toString()
                    binding.editDate.text = sdf.format(calendar.time)

                }

            DatePickerDialog(
                requireContext(), dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.startJob.text = SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis())
        binding.pickTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)

                val myFormat = "HH:mm:ss"
                binding.startJob.text = SimpleDateFormat(myFormat).format(calendar.time).toString()
            }
            TimePickerDialog(
                requireContext(),
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        binding.editFinishDate.text =
            SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis())

        binding.pickFinishDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val dateSetListener =
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, monthOfYear)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val myFormat = "yyyy-MM-dd" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US).toString()
                    binding.editFinishDate.text = sdf.format(calendar.time)

                }
            DatePickerDialog(
                requireContext(), dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.finishTimeJob.text = SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis())
        binding.pickfinishTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)

                val myFormat = "HH:mm:ss"
                binding.finishTimeJob.text = SimpleDateFormat(myFormat).format(calendar.time).toString()
            }
            TimePickerDialog(
                requireContext(),
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
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
                        if (binding.nameJob.text.toString().isNotEmpty() ||
                            binding.positionJob.text.toString().isNotEmpty()
                        ) {
                            val timeFinish =
                                binding.editFinishDate.text.toString() + "T" + binding.finishTimeJob.text.toString() + "Z"
                            val time =
                                binding.editDate.text.toString() + "T" + binding.startJob.text.toString() + "Z"

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
                                R.string.New_Job_error,
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


        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.loadMyJobs()
            findNavController().navigateUp()
        }
        return binding.root
    }

}