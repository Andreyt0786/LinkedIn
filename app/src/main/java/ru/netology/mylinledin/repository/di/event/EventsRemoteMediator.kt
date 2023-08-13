package ru.netology.mylinledin.repository.di.event

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import retrofit2.HttpException
import ru.netology.mylinledin.api.event.ApiEventService
import ru.netology.mylinledin.dao.post.event.EventsDao
import ru.netology.mylinledin.dao.post.event.EventsRemoteKeyDao
import ru.netology.mylinledin.db.AppDb
import ru.netology.mylinledin.entity.event.EventEntity
import ru.netology.mylinledin.entity.event.EventsRemoteKeyEntity
import ru.netology.mylinledin.error.ApiError
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class EventsRemoteMediator(
    private val apiService: ApiEventService,
    private val eventsDao: EventsDao,
    private val eventsRemoteKeyDao: EventsRemoteKeyDao,
    private val appDb: AppDb,
) : RemoteMediator<Int, EventEntity>() {


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EventEntity>
    ): MediatorResult {

        try {
            val result = when (loadType) {
                LoadType.REFRESH -> eventsRemoteKeyDao.max()?.let {
                    apiService.getAfterEvents(it, state.config.pageSize)
                }
                    ?: apiService.getLatestEvents(state.config.initialLoadSize)


                LoadType.PREPEND -> {
                    return MediatorResult.Success(true)

                }

                LoadType.APPEND -> {
                    val id = eventsRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    apiService.getBeforeEvents(id, state.config.pageSize)
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
                        eventsRemoteKeyDao.insert(
                            EventsRemoteKeyEntity(
                                EventsRemoteKeyEntity.KeyType.AFTER,
                                body.first().id,
                            )
                        )
                        if (eventsDao.isEmptyEvents()) {
                            eventsRemoteKeyDao.insert(
                                EventsRemoteKeyEntity(
                                    EventsRemoteKeyEntity.KeyType.BEFORE,
                                    body.last().id,
                                )
                            )
                        }
                    }

                    LoadType.APPEND -> {
                        eventsRemoteKeyDao.insert(
                            EventsRemoteKeyEntity(
                                type = EventsRemoteKeyEntity.KeyType.BEFORE,
                                key = body.last().id,
                            )
                        )
                    }

                    else -> Unit
                }

                eventsDao.insertEvents(body.map(EventEntity::fromDtoEvent))
            }
            return MediatorResult.Success(body.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }

}