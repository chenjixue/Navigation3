package com.example.data.repository.interfaces

import com.example.data.Synchronizer
import com.example.database.dao.NewsResourceDao
import com.example.model.NewsResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.example.database.model.PopulatedNewsResource
import com.example.database.model.asExternalModel


private const val SYNC_BATCH_SIZE = 40


internal class OfflineFirstNewsRepository @Inject constructor(
    private val newsResourceDao: NewsResourceDao,
) : NewsRepository {

    override fun getNewsResources(
        query: NewsResourceQuery,
    ): Flow<List<NewsResource>> = newsResourceDao.getNewsResources(
        useFilterTopicIds = query.filterTopicIds != null,
        filterTopicIds = query.filterTopicIds ?: emptySet(),
        useFilterNewsIds = query.filterNewsIds != null,
        filterNewsIds = query.filterNewsIds ?: emptySet(),
    )
        .map { it.map(PopulatedNewsResource::asExternalModel) }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        TODO("Not yet implemented")
    }
}
