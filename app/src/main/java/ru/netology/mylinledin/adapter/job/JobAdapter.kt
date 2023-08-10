package ru.netology.mylinledin.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.mylinledin.R
import ru.netology.mylinledin.databinding.CardJobBinding
import ru.netology.mylinledin.dto.Job.Job
import ru.netology.mylinledin.dto.posts.AttachmentType


interface OnteractionListener {

    fun onRemove(job: Job) {}
}


class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
    override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem == newItem
    }
}


class JobAdapter(
    private val onInteractionListener: OnteractionListener,
) : ListAdapter<Job, JobViewHolder>(JobDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = CardJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = getItem(position) ?: return
        holder.bind(job)
    }
}

class JobViewHolder(
    private val binding: CardJobBinding,
    private val onInteractionListener: OnteractionListener,
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(job: Job) {


        binding.apply {
            jobName.text = job.name
            positionName.text = job.position
            startDate.text = job.start

            if (job.finish.isNullOrEmpty()) {
                binding.end.isVisible = false
                binding.endDate.isVisible = false
            } else {
                binding.end.isVisible = true
                binding.endDate.isVisible = true
                endDate.text = job.finish
            }

            if (job.link.isNullOrEmpty()) {
                binding.link.isVisible = false
                binding.authorLink.isVisible = false
            } else {
                binding.link.isVisible = true
                binding.authorLink.isVisible = true
                authorLink.text = job.link
            }

            menu.isVisible = job.ownedByMe

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.delete_menu)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.delete -> {
                                onInteractionListener.onRemove(job)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }

        }
    }
}




