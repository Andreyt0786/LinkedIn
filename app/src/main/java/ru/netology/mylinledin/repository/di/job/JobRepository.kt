package ru.netology.mylinledin.repository.di.job

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.mylinledin.dto.Job.Job
import ru.netology.mylinledin.dto.event.Event
import ru.netology.mylinledin.dto.posts.Post
import java.io.File

interface JobRepository {
    val dataJob: Flow<List<Job>>

    suspend fun getMyJob()

    suspend fun getUserJob(id:Int)

    suspend fun removeById(id: Int)

    suspend fun save(job: Job)
}