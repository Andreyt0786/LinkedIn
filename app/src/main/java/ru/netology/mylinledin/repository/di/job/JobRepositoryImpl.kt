package ru.netology.mylinledin.repository.di.job

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
import ru.netology.mylinledin.api.job.ApiJobService
import ru.netology.mylinledin.api.post.ApiPostService
import ru.netology.mylinledin.api.users.ApiUsersService
import ru.netology.mylinledin.dao.Post.PostDao
import ru.netology.mylinledin.dao.Post.PostRemoteKeyDao
import ru.netology.mylinledin.dao.job.JobDao
import ru.netology.mylinledin.dao.wall.WallDao
import ru.netology.mylinledin.db.AppDb
import ru.netology.mylinledin.dto.Job.Job
import ru.netology.mylinledin.dto.posts.Attachment
import ru.netology.mylinledin.dto.posts.AttachmentType
import ru.netology.mylinledin.dto.posts.Media
import ru.netology.mylinledin.dto.posts.Post
import ru.netology.mylinledin.entity.PostEntity
import ru.netology.mylinledin.entity.WallEntity.WallEntity
import ru.netology.mylinledin.entity.WallEntity.toWallEntity
import ru.netology.mylinledin.entity.job.JobEntity
import ru.netology.mylinledin.entity.job.toJobEntity
import ru.netology.mylinledin.entity.toEntity
import ru.netology.mylinledin.error.ApiError
import ru.netology.mylinledin.model.AuthModel
import java.io.File
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





