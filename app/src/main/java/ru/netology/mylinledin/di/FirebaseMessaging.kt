package ru.netology.mylinledin.di

import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class FirebaseMessaging {

    @Provides
    fun firebaseMessaging(): FirebaseMessaging = FirebaseMessaging.getInstance()
}