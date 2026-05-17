package com.example.network.di

import android.content.Context
import com.example.network.demo.DemoAssetManager
import com.example.network.demo.DemoNiaNetworkDataSource
import com.example.network.NiaNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface FlavoredNetworkModule {
    @Binds
    fun binds(impl: DemoNiaNetworkDataSource): NiaNetworkDataSource
}
