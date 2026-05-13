package com.example.sync.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import com.example.network.Dispatcher
import com.example.network.NiaDispatchers.IO
import com.example.data.Synchronizer
import com.example.data.store.NiaPreferencesDataSource
import com.example.sync.initializers.SyncConstraints
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import com.example.data.repository.NewsRepository
import com.example.data.repository.TopicsRepository
import com.example.data.store.ChangeListVersions
/**
 * Syncs the data layer by delegating to the appropriate repository instances with
 * sync functionality.
 */
@HiltWorker
internal class SyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val niaPreferences: NiaPreferencesDataSource,
    private val newsRepository: NewsRepository,
    private val topicRepository: TopicsRepository,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : CoroutineWorker(appContext, workerParams), Synchronizer {

    override suspend fun doWork(): Result = withContext(ioDispatcher) {

         val syncedSuccessfully = awaitAll(
            async { topicRepository.sync() },
             async { newsRepository.sync() }
         ).all { it }
//        val topicDeferred = async { topicRepository.sync() }
//        val newsDeferred = async { newsRepository.sync() }
//
//        val topicSyncSuccess = topicDeferred.await()
//        val newsSyncSuccess = newsDeferred.await()
//
//        if (!topicSyncSuccess) {
//            android.util.Log.e("SyncWorker", "Topic sync failed")
//        }
//        if (!newsSyncSuccess) {
//            android.util.Log.e("SyncWorker", "News sync failed")
//        }

//        val syncedSuccessfully = topicSyncSuccess && newsSyncSuccess

        if (syncedSuccessfully) {
//            searchContentsRepository.populateFtsData()
            Result.success()
        } else {
            Result.retry()
        }
    }

    override suspend fun getChangeListVersions(): ChangeListVersions =
        niaPreferences.getChangeListVersions()

    override suspend fun updateChangeListVersions(
        update: ChangeListVersions.() -> ChangeListVersions,
    ) = niaPreferences.updateChangeListVersion(update)

    companion object {
        /* 为什么绕一道用DelegatingWorker创建,
          因为class SyncWorker有很多NiaPreferencesDataSource,NewsRepository,TopicsRepository配置，直接
          OneTimeWorkRequestBuilder找不到,所以绕过一道DelegatingWorker 通过全局定义的全局定义了一个 @EntryPoint 接口
          拿到fun hiltWorkerFactory(): HiltWorkerFactory  拿到 HiltWorkerFactory 创建

          当然我们也可以通过全局配置
            @HiltAndroidApp
            class NiaApplication : Application(), Configuration.Provider {

            @Inject
            lateinit var workerFactory: HiltWorkerFactory

            override val workManagerConfiguration: Configuration
                get() = Configuration.Builder()
                    .setWorkerFactory(workerFactory)
                    .build()   
          }
         来让后面 OneTimeWorkRequestBuilder<SyncWorker>都用 HiltWorkerFactory 创建,
         但是WorkManager 的全局配置责任甩给 app 
         1. .无法保持 sync:work 作为库模块的独立性  
         2.  侵入 app 的 WorkManager 全局配置
        */
        fun startUpSyncWork() = OneTimeWorkRequestBuilder<DelegatingWorker>()
//             .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)  // 告诉是个紧急任务
            // .setConstraints(SyncConstraints)     // 必须联网才能拉取数据
            .setInputData(SyncWorker::class.delegatedData())
            .build()
    }

}