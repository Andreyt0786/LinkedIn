package ru.netology.mylinledin.repository.di.job

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.netology.mylinledin.api.job.ApiJobService
import ru.netology.mylinledin.dao.job.JobDao
import ru.netology.mylinledin.dto.Job.Job
import ru.netology.mylinledin.entity.job.JobEntity
import ru.netology.mylinledin.entity.job.toJobEntity
import ru.netology.mylinledin.error.ApiError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobRepositoryImpl @Inject constructor(

    private val apiJobService: ApiJobService,
    private val jobDao: JobDao,
) : JobRepository {

    override suspend fun getUserJob(id: Int) {
        jobDao.clear()
        val jobs = apiJobService.getUserJob(id)
        if (!jobs.isSuccessful) {
            throw ApiError(jobs.code(), jobs.message())
        }
        val body = jobs.body() ?: throw ApiError(jobs.code(), jobs.message())
        jobDao.insert(body.toJobEntity())
    }

    override suspend fun getMyJob() {
        jobDao.clear()
        val jobs = apiJobService.getMyJob()
        if (!jobs.isSuccessful) {
            throw ApiError(jobs.code(), jobs.message())
        }
        val body = jobs.body() ?: throw ApiError(jobs.code(), jobs.message())
        jobDao.insert(body.toJobEntity())
    }

    override suspend fun removeById(id: Int) {
        val posts = apiJobService.removeById(id)
        if (!posts.isSuccessful) {
            throw ApiError(posts.code(), posts.message())
        } else {
            jobDao.removeByIdJob(id)
        }
    }


    override val dataJob =
        jobDao.getAllJob().map { it.map(JobEntity::toDto) }.flowOn(Dispatchers.Default)


    override suspend fun save(job: Job) {

        val posts = apiJobService.save(job)
        if (!posts.isSuccessful) {
            throw ApiError(posts.code(), posts.message())
        }
        val body = posts.body() ?: throw ApiError(posts.code(), posts.message())
        jobDao.insert(JobEntity.fromDto(body))
    }

}





