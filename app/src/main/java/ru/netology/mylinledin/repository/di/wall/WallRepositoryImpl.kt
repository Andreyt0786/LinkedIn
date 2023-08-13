package ru.netology.mylinledin.repository.di.wall

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.mylinledin.api.post.ApiPostService
import ru.netology.mylinledin.dao.post.PostDao
import ru.netology.mylinledin.dao.wall.WallDao
import ru.netology.mylinledin.dto.posts.Attachment
import ru.netology.mylinledin.dto.posts.AttachmentType
import ru.netology.mylinledin.dto.posts.Media
import ru.netology.mylinledin.dto.posts.Post
import ru.netology.mylinledin.entity.PostEntity
import ru.netology.mylinledin.entity.wallEntity.WallEntity
import ru.netology.mylinledin.entity.wallEntity.toWallEntity
import ru.netology.mylinledin.error.ApiError
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WallRepositoryImpl @Inject constructor(

    private val wallDao: WallDao,
    private val apiPostService: ApiPostService,
    private val postDao: PostDao,
) : WallRepository {

    override val dataUser =
        wallDao.getAllWall().map { it.map(WallEntity::toDto) }.flowOn(Dispatchers.Default)


    override suspend fun getAllForWall(id: Int) {
        wallDao.clearWall()
        val posts = apiPostService.getForWall(id)
        if (!posts.isSuccessful) {
            throw ApiError(posts.code(), posts.message())
        }
        val body = posts.body() ?: throw ApiError(posts.code(), posts.message())
        wallDao.insertWall(body.toWallEntity())
    }

    override suspend fun saveWithAttachment(file: File, post: Post) {
        val media = upload(file)
        val posts = apiPostService.save(
            post.copy(
                attachment = Attachment(
                    url = media.url,
                    type = AttachmentType.IMAGE
                )
            )
        )
        if (!posts.isSuccessful) {
            throw ApiError(posts.code(), posts.message())
        }
        val body = posts.body() ?: throw ApiError(posts.code(), posts.message())
        wallDao.insertWall(WallEntity.fromDto(body))

    }

    private suspend fun upload(file: File): Media {
        return apiPostService.upload(
            MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())
        )
            .let { requireNotNull(it.body()) }
    }


    override suspend fun likeById(post: Post) {
        if (!post.likedByMe) {
            val posts = apiPostService.likeById(post.id)
            if (!posts.isSuccessful) {
                throw ApiError(posts.code(), posts.message())
            }
            val body = posts.body() ?: throw ApiError(posts.code(), posts.message())
            wallDao.insertWall(WallEntity.fromDto(body))
            postDao.insert(PostEntity.fromDto(body))
        } else {
            val posts = apiPostService.dislikeById(post.id)
            if (!posts.isSuccessful) {
                throw ApiError(posts.code(), posts.message())
            }
            val body = posts.body() ?: throw ApiError(posts.code(), posts.message())
            wallDao.insertWall(WallEntity.fromDto(body))
            postDao.insert(PostEntity.fromDto(body))
        }
    }

    override suspend fun save(post: Post) {

        val posts = apiPostService.save(post)
        if (!posts.isSuccessful) {
            throw ApiError(posts.code(), posts.message())
        }
        val body = posts.body() ?: throw ApiError(posts.code(), posts.message())
        wallDao.insertWall(WallEntity.fromDto(body))
        postDao.insert(PostEntity.fromDto(body))

    }

    override suspend fun removeById(id: Int) {
        val posts = apiPostService.removeById(id)
        if (!posts.isSuccessful) {
            throw ApiError(posts.code(), posts.message())
        } else {
            wallDao.removeByIdWall(id)
            postDao.removeById(id)
        }
    }


}

