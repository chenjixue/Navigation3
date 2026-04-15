package com.example.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.example.data.repository.interfaces.OfflineFirstUserDataRepository
import com.example.data.repository.interfaces.UserDataRepository
import com.example.data.repository.interfaces.NewsRepository
import com.example.data.repository.interfaces.OfflineFirstNewsRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    internal abstract fun bindsUserDataRepository(
        userDataRepository: OfflineFirstUserDataRepository,
    ): UserDataRepository

    @Binds
    internal abstract fun bindsNewsResourceRepository(
        newsRepository: OfflineFirstNewsRepository,
    ): NewsRepository
}