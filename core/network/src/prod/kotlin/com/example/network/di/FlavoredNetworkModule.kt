package com.example.network.di

import com.example.network.NiaNetworkDataSource
import com.example.network.retrofit.RetrofitNiaNetwork
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface FlavoredNetworkModule {
    @Binds
    fun binds(impl: RetrofitNiaNetwork): NiaNetworkDataSource
}
