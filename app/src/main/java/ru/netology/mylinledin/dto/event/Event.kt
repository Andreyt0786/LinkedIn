package ru.netology.mylinledin.dto.event

import ru.netology.mylinledin.dto.posts.Attachment

data class Event(
    val id: Int,
    val authorId: Int,
    val author: String,
    val authorAvatar: String?,
    val authorJob: String?,
    val content: String,
    val datetime: String,
    val published: String,
    val type: String,
    val link: String?,
//  val coords: Coordinates?,
//val likeOwnerIds:List<Int>, узнать и добавить
// val mentionIds:List<Int>,
    val likedByMe: Boolean,
    val participantedByMe: Boolean,
    val attachment: Attach?,
    val ownedByMe: Boolean,
)

data class Attach(
    val url: String,
    val type: AttachmentType,
)

enum class AttachmentType {
    IMAGE, VIDEO, AUDIO,
}

