package ru.netology.mylinledin.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.netology.mylinledin.dao.Post.PostDao
import ru.netology.mylinledin.dao.Post.PostRemoteKeyDao
import ru.netology.mylinledin.entity.PostEntity
import ru.netology.mylinledin.entity.PostRemoteKeyEntity

@Database(entities = [PostEntity::class, PostRemoteKeyEntity::class], version = 1)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
}