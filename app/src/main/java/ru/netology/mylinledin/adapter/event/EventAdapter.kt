package ru.netology.mylinledin.adapter.event

import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.mylinledin.R
import ru.netology.mylinledin.databinding.CardEventBinding
import ru.netology.mylinledin.dto.event.AttachmentType
import ru.netology.mylinledin.dto.event.Event
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


interface OnInteractionListenerEvent {
    fun onLike(event: Event) {}
    fun onEdit(event: Event) {}
    fun onRemove(event: Event) {}
    fun onShare(event: Event) {}
    fun previewPhoto(event: Event) {}
    fun playVideo(event: Event) {}
    fun playMusic(event: Event) {}
}


class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
    override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem == newItem
    }
}


class EventAdapter(
    private val onInteractionListener: OnInteractionListenerEvent,
) : PagingDataAdapter<Event, EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = CardEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding, onInteractionListener)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position) ?: return
        holder.bind(event)
    }
}

class EventViewHolder(
    private val binding: CardEventBinding,
    private val onInteractionListener: OnInteractionListenerEvent,
) : RecyclerView.ViewHolder(binding.root) {


    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(event: Event) {
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss")

        val publishedEvent = OffsetDateTime.parse(event.published, DateTimeFormatter.ISO_DATE_TIME)
        val formatPublished = publishedEvent.format(formatter)

        val dateTimeEvent = OffsetDateTime.parse(event.datetime, DateTimeFormatter.ISO_DATE_TIME)
        val formatDateTime = dateTimeEvent.format(formatter)

        binding.apply {
            author.text = event.author
            published.text = formatPublished
            content.text = event.content
            // в адаптере
            like.isChecked = event.likedByMe
            menu.isVisible = event.ownedByMe
            WEB.text = event.type
            authorWork.text = event.authorJob
            authorLink.text = event.link
            dateTime.text = formatDateTime

            if (event.authorJob.isNullOrEmpty()) {
                binding.authorWork.isVisible = false
                binding.work.isVisible = false
            }

            if (event.link.isNullOrEmpty()) {
                binding.link.isVisible = false
                binding.authorLink.isVisible = false
            }

            val urlNoAva = "https://znaet.petrovich.ru/assets/image/no-avatar.png"
            if (event.authorAvatar.isNullOrEmpty()) {
                Glide.with(binding.avatar)
                    .load(urlNoAva)
                    .timeout(10000)
                    .circleCrop()
                    .into(binding.avatar)

            } else {
                val url = event.authorAvatar
                Glide.with(binding.avatar)
                    .load(url)
                    .timeout(10000)
                    .circleCrop()
                    .into(binding.avatar)
            }


            if (event.attachment?.type == AttachmentType.IMAGE) {
                val urlImage = event.attachment.url
                binding.imageHolder.isVisible = true

                Glide.with(binding.imageHolder)
                    .load(urlImage)
                    .timeout(10000)
                    .circleCrop()
                    .into(binding.imageHolder)
            } else {
                binding.imageHolder.isVisible = false
            }

            if (event.attachment?.type == AttachmentType.VIDEO) {
                binding.video.isVisible = true
                binding.playButtom.isVisible = true

                binding.playButtom.setOnClickListener {
                    binding.video.apply {
                        // Удален MediaController

                        setVideoURI(
                            Uri.parse(event.attachment.url)
                        )
                        setOnPreparedListener {
                            start()
                        }
                        setOnCompletionListener {
                            stopPlayback()
                        }
                    }
                }
            } else {
                binding.video.isVisible = false
                binding.playButtom.isVisible = false
            }

            binding.play.isVisible = event.attachment?.type == AttachmentType.AUDIO



            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(event)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.onEdit(event)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }

            imageHolder.setOnClickListener {
                onInteractionListener.previewPhoto(event)
            }

            like.setOnClickListener {
                onInteractionListener.onLike(event)
            }

            share.setOnClickListener {
                onInteractionListener.onShare(event)
            }

            video.setOnClickListener {
                onInteractionListener.playVideo(event)
            }

            play.setOnClickListener {
                onInteractionListener.playMusic(event)
            }
        }
    }
}




