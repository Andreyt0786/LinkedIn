package ru.netology.mylinledin.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.mylinledin.dto.posts.Attachment
import ru.netology.mylinledin.dto.posts.Coordinates
import ru.netology.mylinledin.dto.posts.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val authorId: Int,
    val author: String,
    val authorAvatar: String?,
    val authorJob: String?,
    val content: String,
    val published: String,
    //@Embedded
   // val coords: Coordinates?,
    val link: String?,
    //val likeOwnerIds: List<Int>,
   // val mentionIds: List<Int>,
    val mentiondMe: Boolean,
    val likedByMe: Boolean,
    @Embedded
    val attachment: Attachment?,
    val ownedByMe: Boolean,
) {

    fun toDto() = Post(
        id = id,
        authorId = authorId,
        author = author,
        authorAvatar = authorAvatar,
        authorJob = authorJob,
        content = content,
        published = published,
       // coords = coords,
        link = link,
      //  likeOwnerIds = likeOwnerIds,
       // mentionIds = mentionIds,
        mentiondMe = mentiondMe,
        likedByMe = likedByMe,
        attachment = attachment,
        ownedByMe = ownedByMe
    )

    companion object{
        fun fromDto(dto:Post) =
            PostEntity(
                id = dto.id,
                authorId = dto.authorId,
                author = dto.author,
                authorAvatar = dto.authorAvatar,
                authorJob = dto.authorJob,
                content = dto.content,
                published = dto.published,
                //coords = dto.coords,
                link = dto.link,
               // likeOwnerIds = dto.likeOwnerIds,
               // mentionIds = dto.mentionIds,
                mentiondMe = dto.mentiondMe,
                likedByMe = dto.likedByMe,
                attachment = dto.attachment,
                ownedByMe = dto.ownedByMe
            )
    }

}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)
