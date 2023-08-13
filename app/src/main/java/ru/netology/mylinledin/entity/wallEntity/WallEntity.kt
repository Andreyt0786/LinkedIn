package ru.netology.mylinledin.entity.wallEntity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.mylinledin.dto.posts.Attachment
import ru.netology.mylinledin.dto.posts.Post

@Entity
data class WallEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val authorId: Int,
    val author: String,
    val authorAvatar: String?,
    val authorJob: String?,
    val content: String,
    val published: String,
    val link: String?,
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
        link = link,
        mentiondMe = mentiondMe,
        likedByMe = likedByMe,
        attachment = attachment,
        ownedByMe = ownedByMe
    )

    companion object {
        fun fromDto(dto: Post) =
            WallEntity(
                id = dto.id,
                authorId = dto.authorId,
                author = dto.author,
                authorAvatar = dto.authorAvatar,
                authorJob = dto.authorJob,
                content = dto.content,
                published = dto.published,
                link = dto.link,
                mentiondMe = dto.mentiondMe,
                likedByMe = dto.likedByMe,
                attachment = dto.attachment,
                ownedByMe = dto.ownedByMe
            )
    }

}

fun List<WallEntity>.toDto(): List<Post> = map(WallEntity::toDto)
fun List<Post>.toWallEntity(): List<WallEntity> = map(WallEntity::fromDto)
