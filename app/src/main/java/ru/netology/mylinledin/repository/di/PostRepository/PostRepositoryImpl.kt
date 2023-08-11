package ru.netology.mylinledin.repository.di.PostRepository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.mylinledin.api.post.ApiPostService
import ru.netology.mylinledin.api.users.ApiUsersService
import ru.netology.mylinledin.dao.Post.PostDao
import ru.netology.mylinledin.dao.Post.PostRemoteKeyDao
import ru.netology.mylinledin.dao.wall.WallDao
import ru.netology.mylinledin.db.AppDb
import ru.netology.mylinledin.dto.posts.Attachment
import ru.netology.mylinledin.dto.posts.AttachmentType
import ru.netology.mylinledin.dto.posts.Media
import ru.netology.mylinledin.dto.posts.Post
import ru.netology.mylinledin.entity.PostEntity
import ru.netology.mylinledin.entity.toEntity
import ru.netology.mylinledin.error.ApiError
import ru.netology.mylinledin.model.AuthModel
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private  val wallDao: WallDao,
    private val apiPostService: ApiPostService,
    private val apiUsersService: ApiUsersService,
    postRemoteKeyDao: PostRemoteKeyDao,
    appDb: AppDb,
) : PostRepository {




    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 5, enablePlaceholders = false),
        pagingSourceFactory = { postDao.getPagingSource() },
        remoteMediator = PostRemoteMediator(
            apiPostService,
            postDao,
            postRemoteKeyDao,
            appDb,
        )
    ).flow
        .map { pagingData ->
            pagingData.map(PostEntity::toDto)
        }


//postDao.getAll().map { it.map(PostEntity::toDto) }.flowOn(Dispatchers.Default)

    override suspend fun getToken(login: String?, password: String?): AuthModel {
        val response = apiUsersService.updateUser(login, password)

        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        return response.body() ?: throw ApiError(response.code(), response.message())
    }

    override suspend fun newUser(login: String, password: String, name : String): AuthModel {
        val response = apiUsersService.registerUser(login, password, name)

        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        return response.body() ?: throw ApiError(response.code(), response.message())
    }

    override suspend fun saveWithAttachment(file: File,post :Post) {
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
        postDao.insert(PostEntity.fromDto(body))

    }

    private suspend fun upload(file: File): Media {
        return apiPostService.upload(
            MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())
        )
            .let { requireNotNull(it.body()) }
    }


    override suspend fun getAll() {
        val posts = apiPostService.getAll()
        if (!posts.isSuccessful) {
            throw ApiError(posts.code(), posts.message())
        }
        val body = posts.body() ?: throw ApiError(posts.code(), posts.message())
        //postDao.insert(posts.body()!!.map { PostEntity.fromDto(it) })
        postDao.insert(body.toEntity())
    }

    override fun getNewer(id: Long) = flow {
        while (true) {
            delay(10000)
            val posts = apiPostService.getNewer(id)
            if (!posts.isSuccessful) {
                throw ApiError(posts.code(), posts.message())
            }
            val body = posts.body() ?: emptyList()
            emit(body.size)
            postDao.insert(body.toEntity())
        }
    }.flowOn(Dispatchers.Default)


    override suspend fun likeById(post: Post) {
        if (!post.likedByMe) {
            val posts = apiPostService.likeById(post.id)
            if (!posts.isSuccessful) {
                throw ApiError(posts.code(), posts.message())
            }
            val body = posts.body() ?: throw ApiError(posts.code(), posts.message())
            postDao.insert(PostEntity.fromDto(body))
        } else {
            val posts = apiPostService.dislikeById(post.id)
            if (!posts.isSuccessful) {
                throw ApiError(posts.code(), posts.message())
            }
            val body = posts.body() ?: throw ApiError(posts.code(), posts.message())
            postDao.insert(PostEntity.fromDto(body))
        }
    }

    override suspend fun save(post: Post) {

        val posts = apiPostService.save(post)
        if (!posts.isSuccessful) {
            throw ApiError(posts.code(), posts.message())
        }
        val body = posts.body() ?: throw ApiError(posts.code(), posts.message())
        postDao.insert(PostEntity.fromDto(body))

    }

    override suspend fun removeById(id: Int) {
        val posts = apiPostService.removeById(id)
        if (!posts.isSuccessful) {
            throw ApiError(posts.code(), posts.message())
        } else {
            postDao.removeById(id)
                    }
    }

}

