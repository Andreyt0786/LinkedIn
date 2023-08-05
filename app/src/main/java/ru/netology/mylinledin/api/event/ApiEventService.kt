package ru.netology.mylinledin.api.event

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import ru.netology.mylinledin.dto.event.Event
import ru.netology.mylinledin.dto.posts.Media

interface ApiEventService {

    @GET("events")
    suspend fun getAllEvents(): Response<List<Event>>

    @GET("events/latest")
    suspend fun getLatestEvents(@Query("count") count: Int): Response<List<Event>>

    @GET("events/{id}/newer")
    suspend fun getNewerEvents(@Path("id") id: Int): Response<List<Event>>

    @GET("events/{id}/before")
    suspend fun getBeforeEvents(@Path("id") id: Int, @Query("count") count: Int): Response<List<Event>>

    @GET("events/{id}/after")
    suspend fun getAfterEvents(@Path("id") id: Int, @Query("count") count: Int): Response<List<Event>>

    @DELETE("events/{id}")
    suspend fun removeByIdEvents(@Path("id") id: Int): Response<Unit>

    @POST("events/{id}/likes")
    suspend fun likeByIdEvents(@Path("id") id: Int): Response<Event>

    @DELETE("events/{id}/likes")
    suspend fun dislikeByIdEvents(@Path("id") id: Int): Response<Event>

    @POST("event")
    suspend fun saveEvents(@Body event: Event): Response<Event>

    @GET("events/{id}")
    suspend fun getEvents(@Path("id") id: Int): Response<Event>

    @Multipart
    @POST("media")
    suspend fun upload(@Part part: MultipartBody.Part): Response<Media>


}
