package ru.netology.mylinledin.repository.di.job

import kotlinx.coroutines.flow.Flow
import ru.netology.mylinledin.dto.Job.Job

interface JobRepository {
    val dataJob: Flow<List<Job>>

    suspend fun getMyJob()

    suspend fun getUserJob(id: Int)

    suspend fun removeById(id: Int)

    suspend fun save(job: Job)
}