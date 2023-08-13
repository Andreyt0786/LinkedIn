package ru.netology.mylinledin.repository.di.event

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.mylinledin.dto.event.Event
import java.io.File

interface EventRepository {
    val dataEvent: Flow<PagingData<Event>>
    suspend fun getAllEvents()
    suspend fun likeByIdEvents(event: Event)
    suspend fun saveEvents(event: Event)
    suspend fun removeByIdEvents(id: Int)
    suspend fun saveWithAttachmentEvents(file: File, event: Event)


}