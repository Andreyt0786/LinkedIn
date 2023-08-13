package ru.netology.mylinledin.api.job

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.netology.mylinledin.dto.Job.Job

interface ApiJobService {

    @GET("my/jobs")
    suspend fun getMyJob(): Response<List<Job>>

    @GET("{id}/jobs")
    suspend fun getUserJob(@Path("id") id: Int): Response<List<Job>>


    @DELETE("my/jobs/{id}")
    suspend fun removeById(@Path("id") id: Int): Response<Unit>


    @POST("my/jobs")
    suspend fun save(@Body job: Job): Response<Job>


}



