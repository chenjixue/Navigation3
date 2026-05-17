package com.example.data.repository

import com.example.data.Synchronizer
import com.example.data.changeListSync
import com.example.data.model.asEntity
import com.example.data.store.ChangeListVersions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.example.database.dao.ChouhuResourceDao
import com.example.database.model.ChouhuResourceEntity
import com.example.database.model.TopicEntity
import com.example.database.model.asExternalModel
import com.example.database.model.asEntity
import com.example.model.ChouhuResource
import com.example.network.NiaNetworkDataSource
import com.example.model.Topic
import com.example.network.model.NetworkTopic
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class OfflineChouhuRepository @Inject constructor(
    private val chouhuResourceDao: ChouhuResourceDao,
    private val network: NiaNetworkDataSource,
) : ChouhuRepository {

    override fun getChouhuResources(): Flow<List<ChouhuResource>> {
        return chouhuResourceDao.getChouhuResources().map { entities ->
            entities.map(ChouhuResourceEntity::asExternalModel)
        }
    }

    override fun getChouhuResources(data: String): Flow<List<ChouhuResource>> {
        return chouhuResourceDao.getChouhuResources(data).map { entities ->
            entities.map(ChouhuResourceEntity::asExternalModel)
        }
    }

    override suspend fun deleteChouhuResources(keys: List<String>) =
        chouhuResourceDao.deleteChouhuResources(keys)

    override suspend fun setChouhuResources(chouhuResource: List<ChouhuResource>) =
        chouhuResourceDao.upsertChouhuResources(chouhuResource.map(ChouhuResource::asEntity))

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean =
        synchronizer.changeListSync(
        ).isSuccess
}
