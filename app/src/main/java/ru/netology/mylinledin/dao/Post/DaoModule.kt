package ru.netology.mylinledin.dao.Post

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.mylinledin.db.AppDb
/*
@InstallIn(SingletonComponent::class)//база данных будет использоватьяс во всем приложении
@Module
object DaoModule {
    @Provides
    fun providePostDao(db:AppDb):PostDao=db.postDao()

    @Provides
    fun providePostRemoteKeyDao(db:AppDb):PostRemoteKeyDao=db.postRemoteKeyDao()

}*/