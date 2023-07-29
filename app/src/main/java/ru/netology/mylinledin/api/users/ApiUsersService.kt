package ru.netology.mylinledin.api.users

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import ru.netology.mylinledin.dto.posts.Post
import ru.netology.mylinledin.dto.posts.PushToken
import ru.netology.mylinledin.dto.user.Users
import ru.netology.mylinledin.model.AuthModel

interface ApiUsersService {

    @GET("users")
    suspend fun getAllUsers(): Response<List<Users>>

    @GET("users/{id}")
    suspend fun getUser(): Users

    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun updateUser(
        @Field("login") login: String?,
        @Field("password") password: String?
    ): Response<AuthModel>


    @FormUrlEncoded
    @POST("users/registration")
    suspend fun registerUser(
        @Field("login") login: String,
        @Field("password") password: String,
        @Field("name") name: String
    ): Response<AuthModel>

    @POST("users/push-tokens")
    suspend fun uploadPushToken(@Body pushToken: PushToken): Response<Unit>
}