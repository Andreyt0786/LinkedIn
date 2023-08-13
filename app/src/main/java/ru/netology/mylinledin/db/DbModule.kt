package ru.netology.mylinledin.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.mylinledin.dao.post.PostDao
import ru.netology.mylinledin.dao.post.PostRemoteKeyDao
import ru.netology.mylinledin.dao.post.event.EventsDao
import ru.netology.mylinledin.dao.post.event.EventsRemoteKeyDao
import ru.netology.mylinledin.dao.job.JobDao
import ru.netology.mylinledin.dao.wall.WallDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)//база данных будет использоватьяс во всем приложении
@Module
object DbModule {

    @Singleton
    //для создания функции в ручную пишется аннотация
    @Provides
    fun provideDb(
        @ApplicationContext
        context: Context
    ): AppDb = Room.databaseBuilder(context, AppDb::class.java, "app.db")
        .fallbackToDestructiveMigration()
        .allowMainThreadQueries()
        .build()

    @Provides
    fun providePostDao(
        appDb: AppDb
    ): PostDao = appDb.postDao()

    @Provides
    fun provideWallDao(
        appDb: AppDb
    ): WallDao = appDb.wallDao()

    @Provides
    fun providePostRemoteKeyDao(
        appDb: AppDb
    ): PostRemoteKeyDao = appDb.postRemoteKeyDao()

    @Provides
    fun provideEventsDao(
        appDb: AppDb
    ): EventsDao = appDb.eventDao()

    @Provides
    fun provideEventsRemoteKeyDao(
        appDb: AppDb
    ): EventsRemoteKeyDao = appDb.eventRemoteKeyDao()

    @Provides
    fun provideJobDao(
        appDb: AppDb
    ): JobDao = appDb.jobDao()
}