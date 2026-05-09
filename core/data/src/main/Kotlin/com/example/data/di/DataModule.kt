package com.example.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.example.data.repository.OfflineFirstUserDataRepository
import com.example.data.repository.UserDataRepository
import com.example.data.repository.NewsRepository
import com.example.data.repository.OfflineFirstNewsRepository

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