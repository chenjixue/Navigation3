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

/**
 * Syncs the data layer by delegating to the appropriate repository instances with
 * sync functionality.
 */
@HiltWorker
internal class SyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val niaPreferences: NiaPreferencesDataSource,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
//) : CoroutineWorker(appContext, workerParams), Synchronizer {
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(ioDispatcher) {

        val syncedSuccessfully = awaitAll(
//            async { topicRepository.sync() },
//            async { newsRepository.sync() },
            async { true }
        ).all { it }

        if (syncedSuccessfully) {
//            searchContentsRepository.populateFtsData()
            Result.success()
        } else {
            Result.retry()
        }
    }

    companion object {
        fun startUpSyncWork() = OneTimeWorkRequestBuilder<DelegatingWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)  // 告诉是个紧急任务
            .setConstraints(SyncConstraints)     // 必须联网才能拉取数据
            .setInputData(SyncWorker::class.delegatedData())
            .build()
    }

}