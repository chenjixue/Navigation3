package com.example.data.repository

import com.example.data.Synchronizer
import com.example.data.changeListSync
import com.example.database.dao.NoSaleResourceDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.example.database.dao.SaleResourceDao
import com.example.database.model.SaleResourceEntity
import com.example.database.model.asExternalModel
import com.example.database.model.asEntity
import com.example.database.model.asNoSaleEntity
import com.example.model.SaleResource
import com.example.network.NiaNetworkDataSource

internal class OfflineSaleRepository @Inject constructor(
    private val saleResourceDao: SaleResourceDao,
    private val noSaleResourceDao: NoSaleResourceDao,
    private val network: NiaNetworkDataSource,
) : SaleRepository {

    override fun getSaleResources(): Flow<List<SaleResource>> {
        return saleResourceDao.getSaleResources().map { entities ->
            entities.map(SaleResourceEntity::asExternalModel)
        }
    }

    override fun getSaleResources(data: String): Flow<List<SaleResource>> {
        return saleResourceDao.getSaleResources(data).map { entities ->
            entities.map(SaleResourceEntity::asExternalModel)
        }
    }

    override suspend fun deleteSaleResources(keys: List<String>) =
        saleResourceDao.deleteSaleResources(keys)

    override suspend fun setSaleResources(chouhuResource: List<SaleResource>) =
        saleResourceDao.upsertSaleResources(chouhuResource.map(SaleResource::asEntity))

    override fun getNoSaleResources(): Flow<List<SaleResource>> {
        return noSaleResourceDao.getNoSaleResources().map { entities ->
            entities.map { it.asExternalModel() }
        }
    }

    override fun getNoSaleResources(data: String): Flow<List<SaleResource>> {
        return noSaleResourceDao.getNoSaleResources(data).map { entities ->
            entities.map { it.asExternalModel() }
        }
    }

    override suspend fun deleteNoSaleResources(keys: List<String>) =
        noSaleResourceDao.deleteNoSaleResources(keys)

    override suspend fun setNoSaleResources(chouhuResource: List<SaleResource>) =
        noSaleResourceDao.upsertNoSaleResources(chouhuResource.map { it.asNoSaleEntity() })

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean =
        synchronizer.changeListSync(
        ).isSuccess
}
