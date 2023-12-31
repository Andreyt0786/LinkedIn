package ru.netology.mylinledin.repository.di.wall

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.mylinledin.dto.event.Event
import ru.netology.mylinledin.dto.posts.Post
import java.io.File

interface WallRepository {
    val dataUser: Flow<List<Post>>
    suspend fun getAllForWall(id: Int)

    suspend fun likeById(post: Post)
    suspend fun save(post: Post)
    suspend fun removeById(id: Int)

    suspend fun saveWithAttachment(file: File, post: Post)
}