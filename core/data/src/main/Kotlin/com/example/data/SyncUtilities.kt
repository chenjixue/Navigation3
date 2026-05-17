package com.example.data

import android.util.Log
import com.example.network.model.NetworkChangeList
import kotlin.coroutines.cancellation.CancellationException
import com.example.data.store.ChangeListVersions
import com.example.model.ChouhuResource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.Flow

interface Synchronizer {
    suspend fun getChangeListVersions(): ChangeListVersions
    suspend fun updateChangeListVersions(update: ChangeListVersions.() -> ChangeListVersions)

    suspend fun Syncable.sync() = this@sync.syncWith(this@Synchronizer)
}


interface Syncable {
    suspend fun syncWith(synchronizer: Synchronizer): Boolean
}


private suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (cancellationException: CancellationException) {
    throw cancellationException
} catch (exception: Exception) {
    Log.i(
        "suspendRunCatching",
        "Failed to evaluate a suspendRunCatchingBlock. Returning failure Result",
        exception,
    )
    Result.failure(exception)
}


suspend fun Synchronizer.changeListSync(
//    modelGet: suspend (String) ->  Flow<List<ChouhuResource>>,
) = suspendRunCatching {
    val todayDateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    // 调用传入的闭包，并把今天的时间作为参数传进去
//    modelGet(todayDateString)
}
