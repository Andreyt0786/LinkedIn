package ru.netology.mylinledin.dto.posts

data class Post(
    val id:Int,
    val authorId:Int,
    val author: String,
    val authorAvatar: String?,
    val authorJob:String?,
    val content: String,
    val published: String,
  //  val coords: Coordinates?,
    val link: String?,
    //val likeOwnerIds:List<Int>,
   // val mentionIds:List<Int>,
    val mentiondMe:Boolean,
    val likedByMe:Boolean,
    val attachment:Attachment?,
    val ownedByMe:Boolean,
)

data class Attachment(
    val url:String,
    val type: AttachmentType,
)

enum class AttachmentType{
    IMAGE, VIDEO, AUDIO,
}

data class Coordinates(
    val lat: String,
    val long: String,
)


