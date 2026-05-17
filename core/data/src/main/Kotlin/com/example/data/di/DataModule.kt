package com.example.data.di

import com.example.data.repository.ChouhuRepository
import com.example.data.repository.ExpenseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
//import com.example.data.repository.OfflineFirstUserDataRepository
//import com.example.data.repository.UserDataRepository
//import com.example.data.repository.NewsRepository
//import com.example.data.repository.OfflineFirstNewsRepository
//import com.example.data.repository.OfflineFirstTopicsRepository
//import com.example.data.repository.TopicsRepository
import com.example.data.repository.OfflineChouhuRepository
import com.example.data.repository.OfflineExpenseRepository
import com.example.data.repository.OfflineFirstUserDataRepository
import com.example.data.repository.OfflineSaleRepository
import com.example.data.repository.SaleRepository
import com.example.data.repository.UserDataRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

//    @Binds
//    internal abstract fun bindsTopicRepository(
//        topicsRepository: OfflineFirstTopicsRepository,
//    ): TopicsRepository
//
//    @Binds
//    internal abstract fun bindsUserDataRepository(
//        userDataRepository: OfflineFirstUserDataRepository,
//    ): UserDataRepository

//    @Binds
//    internal abstract fun bindsNewsResourceRepository(
//        newsRepository: OfflineFirstNewsRepository,
//    ): NewsRepository

    @Binds
    internal abstract fun bindsNewsResourceRepository(
        chouhuRepository: OfflineChouhuRepository,
    ): ChouhuRepository

    @Binds
    internal abstract fun bindsUserDataRepository(
        userDataRepository: OfflineFirstUserDataRepository,
    ): UserDataRepository

    @Binds
    internal abstract fun bindsExpenseRepository(
        expenseRepository: OfflineExpenseRepository,
    ): ExpenseRepository

    @Binds
    internal abstract fun bindsSaleRepository(
        saleRepository: OfflineSaleRepository,
    ): SaleRepository
}