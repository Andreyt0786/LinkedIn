package ru.netology.mylinledin.api



import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.netology.mylinledin.BuildConfig
import ru.netology.mylinledin.api.event.ApiEventService
import ru.netology.mylinledin.api.job.ApiJobService
import ru.netology.mylinledin.auth.AppAuth
import ru.netology.mylinledin.api.post.ApiPostService
import ru.netology.mylinledin.api.users.ApiUsersService
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)//база данных будет использоватьяс во всем приложении
@Module
class ApiModule {

    companion object {
        private const val BASE_URL = "${BuildConfig.BASE_URL}/api/"
    }

    @Singleton
    @Provides
    fun provideLogging(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Singleton
    @Provides
    fun provideOkHttp(
        logging: HttpLoggingInterceptor,
        appAuth: AppAuth,
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor { chain ->
            val token = appAuth.authStateFlow.value.token
            val request = if (token != null) {
                chain.request()
                    .newBuilder()
                    .addHeader("Authorization", token)
                    .build()
            } else {
                chain.request()
            }

            chain.proceed(request)
        }
        .build()

    @Singleton
    @Provides
    fun provideRetrofit(
        okhttp: OkHttpClient,
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okhttp)
        .build()

    @Singleton
    @Provides
    fun provideApiPostService(
        retrofit: Retrofit
    ):ApiPostService= retrofit.create()

    @Singleton
    @Provides
    fun provideApiUsersService(
        retrofit: Retrofit
    ): ApiUsersService = retrofit.create()

    @Singleton
    @Provides
    fun provideApiEventService(
        retrofit: Retrofit
    ): ApiEventService = retrofit.create()


    @Singleton
    @Provides
    fun provideApiJobService(
        retrofit: Retrofit
    ): ApiJobService = retrofit.create()

}