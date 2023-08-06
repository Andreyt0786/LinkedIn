package ru.netology.mylinledin.repository.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.mylinledin.repository.di.PostRepository.PostRepository
import ru.netology.mylinledin.repository.di.PostRepository.PostRepositoryImpl
import ru.netology.mylinledin.repository.di.event.EventRepository
import ru.netology.mylinledin.repository.di.event.EventRepositoryImpl
import ru.netology.mylinledin.repository.di.wall.WallRepository
import ru.netology.mylinledin.repository.di.wall.WallRepositoryImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)//база данных будет использоватьяс во всем приложении
@Module
interface RepositoryModule {

    @Singleton
    //binds связывает интерфейс и реализацию
    @Binds
    fun bindsPostRepository(impl: PostRepositoryImpl): PostRepository

    @Singleton

    @Binds
    fun bindsEventsRepository(impl: EventRepositoryImpl): EventRepository


    @Singleton

    @Binds
    fun bindsWallRepository(impl: WallRepositoryImpl): WallRepository

}