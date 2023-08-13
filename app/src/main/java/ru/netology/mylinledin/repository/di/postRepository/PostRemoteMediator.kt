package ru.netology.mylinledin.repository.di.postRepository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import retrofit2.HttpException
import ru.netology.mylinledin.api.post.ApiPostService
import ru.netology.mylinledin.dao.post.PostDao
import ru.netology.mylinledin.dao.post.PostRemoteKeyDao
import ru.netology.mylinledin.db.AppDb
import ru.netology.mylinledin.entity.PostEntity
import ru.netology.mylinledin.entity.PostRemoteKeyEntity
import ru.netology.mylinledin.error.ApiError
import java.io.IOException


@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val apiService: ApiPostService,
    private val postDao: PostDao,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    private val appDb: AppDb,
) : RemoteMediator<Int, PostEntity>() {


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {

        try {
            val result = when (loadType) {
                LoadType.REFRESH -> postRemoteKeyDao.max()?.let {
                    apiService.getAfter(it, state.config.pageSize)
                } ?: apiService.getLatest(state.config.initialLoadSize)


                LoadType.PREPEND -> {
                    return MediatorResult.Success(true)

                }

                LoadType.APPEND -> {
                    val id = postRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    apiService.getBefore(id, state.config.pageSize)
                }
            }
            if (!result.isSuccessful) {
                throw HttpException(result)
            }
            val body = result.body() ?: throw ApiError(
                result.code(),
                result.message()
            )
            val data = result.body().orEmpty()

            if (data.isEmpty()) return MediatorResult.Success(false)

            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                PostRemoteKeyEntity.KeyType.AFTER,
                                body.first().id,
                            )
                        )
                        if (postDao.isEmpty()) {
                            postRemoteKeyDao.insert(
                                PostRemoteKeyEntity(
                                    PostRemoteKeyEntity.KeyType.BEFORE,
                                    body.last().id,
                                )
                            )
                        }
                    }

                    LoadType.APPEND -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                type = PostRemoteKeyEntity.KeyType.BEFORE,
                                key = body.last().id,
                            )
                        )
                    }

                    else -> Unit
                }

                postDao.insert(body.map(PostEntity::fromDto))
            }
            return MediatorResult.Success(body.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }


}