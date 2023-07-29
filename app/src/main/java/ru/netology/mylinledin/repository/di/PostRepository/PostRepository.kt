package ru.netology.mylinledin.repository.di.PostRepository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.mylinledin.dto.posts.Post
import ru.netology.mylinledin.model.AuthModel
import java.io.File

interface PostRepository {
    val data: Flow<PagingData<Post>>
    suspend fun getAll()
    suspend fun likeById(post: Post)
    suspend fun save(post: Post)
    suspend fun removeById(id: Int)
    fun getNewer(id:Long): Flow<Int>
    suspend fun saveWithAttachment(file: File, post:Post)
    suspend fun getToken(login:String?, password:String?): AuthModel
    suspend fun newUser (login:String, password:String, name: String): AuthModel


}