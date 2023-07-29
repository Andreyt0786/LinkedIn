package ru.netology.mylinledin.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.BuildConfig
import ru.netology.mylinledin.R
import ru.netology.mylinledin.databinding.CardPostBinding
import ru.netology.mylinledin.dto.posts.AttachmentType
import ru.netology.mylinledin.dto.posts.Post
import ru.netology.mylinledin.mediaPlayer.MediaLifecycleObserver


interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShare(post: Post) {}
    fun previewPhoto(post: Post) {}
    fun playVideo(post: Post) {}
    fun playMusic(post: Post) {}
}


class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}


class PostAdapter(
    private val onInteractionListener: OnInteractionListener,
) : PagingDataAdapter<Post, PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)?:return
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(post: Post) {
        val url = "${"https://netomedia.ru"}/avatars/${post.authorAvatar}"
        Glide.with(binding.avatar)
            .load(url)
            .timeout(10000)
            .circleCrop()
            .into(binding.avatar)

        if (post.attachment?.type == AttachmentType.IMAGE) {
            val urlImage = "${"https://netomedia.ru"}/media/${post.attachment?.url}"

            if (urlImage == null && post.attachment?.type == AttachmentType.IMAGE) {
                binding.imageHolder.isVisible = false
            } else {
                binding.imageHolder.isVisible = true
            }

            Glide.with(binding.imageHolder)
                .load(urlImage)
                .timeout(10000)
                .circleCrop()
                .into(binding.imageHolder)
        }


        if (post.attachment?.type == AttachmentType.VIDEO) {
            val urlVideo = "${"https://netomedia.ru"}/media/${post.attachment?.url}"

            if (urlVideo != null && post.attachment?.type == AttachmentType.VIDEO) {
                binding.video.isVisible = true
                binding.playButtom.isVisible = true
                binding.fullScreen.isVisible = true
            }

            binding.playButtom.setOnClickListener {
                binding.video.apply {
                    // Удален MediaController

                    setVideoURI(
                        Uri.parse(urlVideo)
                    )
                    setOnPreparedListener {
                        start()
                    }
                    setOnCompletionListener {
                        stopPlayback()
                    }
                }

                if (post.attachment?.type == AttachmentType.AUDIO) {
                    val urlAudio = "${"https://netomedia.ru"}/media/${post.attachment?.url}"

                    if (urlAudio != null && post.attachment?.type == AttachmentType.AUDIO) {
                        binding.play.isVisible = true
                    }

                    binding.apply {
                        author.text = post.author
                        published.text = post.published
                        content.text = post.content
                        // в адаптере
                        like.isChecked = post.likedByMe
                        menu.isVisible = post.ownedByMe

                        menu.setOnClickListener {
                            PopupMenu(it.context, it).apply {
                                inflate(R.menu.options_post)
                                setOnMenuItemClickListener { item ->
                                    when (item.itemId) {
                                        R.id.remove -> {
                                            onInteractionListener.onRemove(post)
                                            true
                                        }

                                        R.id.edit -> {
                                            onInteractionListener.onEdit(post)
                                            true
                                        }

                                        else -> false
                                    }
                                }
                            }.show()
                        }

                        imageHolder.setOnClickListener {
                            onInteractionListener.previewPhoto(post)
                        }

                        like.setOnClickListener {
                            onInteractionListener.onLike(post)
                        }

                        share.setOnClickListener {
                            onInteractionListener.onShare(post)
                        }

                        fullScreen.setOnClickListener {
                            onInteractionListener.playVideo(post)
                        }

                        play.setOnClickListener {
                            onInteractionListener.playMusic(post)
                        }
                    }
                }
            }
        }
    }
}


