package ru.netology.mylinledin.entity.event

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.mylinledin.dto.event.Attach

import ru.netology.mylinledin.dto.event.Event


@Entity
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val authorId: Int,
    val author: String,
    val authorAvatar: String?,
    val authorJob: String?,
    val content: String,
    val datetime: String,
    val published: String,
    //val type: String,
    val link: String?,
//  val coords: Coordinates?,
//val likeOwnerIds:List<Int>, узнать и добавить
// val mentionIds:List<Int>,
    val likedByMe: Boolean,
    val participantedByMe: Boolean,
    @Embedded
    val attachment: Attach?,
    val ownedByMe: Boolean,
) {

    fun toDtoEvent() = Event(
        id = id,
        authorId = authorId,
        author = author,
        authorAvatar = authorAvatar,
        authorJob = authorJob,
        content = content,
        datetime = datetime,
        published = published,
        //type = type,
        link = link,
        //  val coords: Coordinates?,
//val likeOwnerIds:List<Int>, узнать и добавить
// val mentionIds:List<Int>,
        likedByMe = likedByMe,
        participantedByMe = participantedByMe,
       attachment = attachment,
        ownedByMe = ownedByMe,
    )

    companion object {
        fun fromDtoEvent(dto: Event) =
            EventEntity(
                id = dto.id,
                authorId = dto.authorId,
                author = dto.author,
                authorAvatar = dto.authorAvatar,
                authorJob = dto.authorJob,
                content = dto.content,
                datetime = dto.datetime,
                published = dto.published,
                //type = dto.type,
                link = dto.link,
                //  val coords: Coordinates?,
                //val likeOwnerIds:List<Int>, узнать и добавить
                // val mentionIds:List<Int>,
                likedByMe = dto.likedByMe,
                participantedByMe = dto.participantedByMe,
                attachment = dto.attachment,
                ownedByMe = dto.ownedByMe,
            )
    }

}

fun List<EventEntity>.toDtoEvent(): List<Event> = map(EventEntity::toDtoEvent)
fun List<Event>.toEntityEvents(): List<EventEntity> = map(EventEntity::fromDtoEvent)
