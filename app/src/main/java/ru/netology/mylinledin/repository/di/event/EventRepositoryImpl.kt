package ru.netology.mylinledin.repository.di.event

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.mylinledin.api.event.ApiEventService
import ru.netology.mylinledin.api.post.ApiPostService
import ru.netology.mylinledin.dao.post.event.EventsDao
import ru.netology.mylinledin.dao.post.event.EventsRemoteKeyDao
import ru.netology.mylinledin.db.AppDb
import ru.netology.mylinledin.dto.event.Attach
import ru.netology.mylinledin.dto.event.AttachmentType
import ru.netology.mylinledin.dto.event.Event
import ru.netology.mylinledin.dto.posts.Media
import ru.netology.mylinledin.entity.event.EventEntity
import ru.netology.mylinledin.entity.event.toEntityEvents
import ru.netology.mylinledin.error.ApiError
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepositoryImpl @Inject constructor(
    private val eventsDao: EventsDao,
    private val apiEventsService: ApiEventService,
    eventsRemoteKeyDao: EventsRemoteKeyDao,
    appDb: AppDb,
    private val apiPostService: ApiPostService,
) : EventRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val dataEvent: Flow<PagingData<Event>> = Pager(
        config = PagingConfig(pageSize = 5, enablePlaceholders = false),
        pagingSourceFactory = { eventsDao.getPagingSourceEvents() },
        remoteMediator = EventsRemoteMediator(
            apiEventsService,
            eventsDao,
            eventsRemoteKeyDao,
            appDb,
        )
    ).flow
        .map { pagingData ->
            pagingData.map(EventEntity::toDtoEvent)
        }

    override suspend fun saveWithAttachmentEvents(file: File, event: Event) {
        val media = upload(file)
        val events = apiEventsService.saveEvents(
            event.copy(
                attachment = Attach(
                    url = media.url,
                    type = AttachmentType.IMAGE
                )
            )
        )
        if (!events.isSuccessful) {
            throw ApiError(events.code(), events.message())
        }
        val body = events.body() ?: throw ApiError(events.code(), events.message())
        eventsDao.insertEvents(EventEntity.fromDtoEvent(body))

    }

    private suspend fun upload(file: File): Media {
        return apiPostService.upload(
            MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())
        )
            .let { requireNotNull(it.body()) }
    }


    override suspend fun getAllEvents() {
        val events = apiEventsService.getAllEvents()
        if (!events.isSuccessful) {
            throw ApiError(events.code(), events.message())
        }
        val body = events.body() ?: throw ApiError(events.code(), events.message())
        eventsDao.insertEvents(body.toEntityEvents())
    }


    override suspend fun likeByIdEvents(event: Event) {
        if (!event.likedByMe) {
            val events = apiEventsService.likeByIdEvents(event.id)
            if (!events.isSuccessful) {
                throw ApiError(events.code(), events.message())
            }
            val body = events.body() ?: throw ApiError(events.code(), events.message())
            eventsDao.insertEvents(EventEntity.fromDtoEvent(body))
        } else {
            val events = apiEventsService.dislikeByIdEvents(event.id)
            if (!events.isSuccessful) {
                throw ApiError(events.code(), events.message())
            }
            val body = events.body() ?: throw ApiError(events.code(), events.message())
            eventsDao.insertEvents(EventEntity.fromDtoEvent(body))
        }
    }

    override suspend fun saveEvents(event: Event) {

        val events = apiEventsService.saveEvents(event)

        if (!events.isSuccessful) {
            throw ApiError(events.code(), events.message())
        }
        val body = events.body() ?: throw ApiError(events.code(), events.message())
        eventsDao.insertEvents(EventEntity.fromDtoEvent(body))
    }

    override suspend fun removeByIdEvents(id: Int) {
        val events = apiEventsService.removeByIdEvents(id)
        if (!events.isSuccessful) {
            throw ApiError(events.code(), events.message())
        } else {
            eventsDao.removeByIdEvents(id)
        }
    }


}

