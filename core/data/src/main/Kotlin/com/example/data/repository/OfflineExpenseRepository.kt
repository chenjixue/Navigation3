package com.example.data.repository

import com.example.data.Synchronizer
import com.example.data.changeListSync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.example.database.dao.PeopleExpenseResourceDao
import com.example.database.dao.OtherExpenseResourceDao
import com.example.database.model.OtherExpenseResourceEntity
import com.example.database.model.PeopleExpenseResourceEntity
import com.example.database.model.asExternalModel
import com.example.database.model.asEntity
import com.example.model.OtherExpenseResource
import com.example.model.PeopleExpenseResource

internal class OfflineExpenseRepository @Inject constructor(
    private val peopleExpenseResourceDao: PeopleExpenseResourceDao,
    private val otherExpenseResourceDao: OtherExpenseResourceDao,
) : ExpenseRepository {

    override fun getPeopleExpenseResources(dataTime: String): Flow<List<PeopleExpenseResource>> {
        return peopleExpenseResourceDao.getPeopleExpenseResources(dataTime).map { entities ->
            entities.map(PeopleExpenseResourceEntity::asExternalModel)
        }
    }

    override fun getPeopleExpenseResources(): Flow<List<PeopleExpenseResource>> {
        return peopleExpenseResourceDao.getPeopleExpenseResources().map { entities ->
            entities.map(PeopleExpenseResourceEntity::asExternalModel)
        }
    }

    override fun getOtherExpenseResources(dataTime: String): Flow<List<OtherExpenseResource>> {
        return otherExpenseResourceDao.getOtherExpenseResources(dataTime).map { entities ->
            entities.map(OtherExpenseResourceEntity::asExternalModel)
        }
    }

    override fun getOtherExpenseResources(): Flow<List<OtherExpenseResource>> {
        return otherExpenseResourceDao.getOtherExpenseResources().map { entities ->
            entities.map(OtherExpenseResourceEntity::asExternalModel)
        }
    }

    override suspend fun deletePeopleExpenseResources(keys: List<String>) =
        peopleExpenseResourceDao.deletePeopleExpenseResources(keys)

    override suspend fun setPeopleExpenseResources(peopleExpenseResource: List<PeopleExpenseResource>) =
        peopleExpenseResourceDao.upsertPeopleExpenseResources(peopleExpenseResource.map(PeopleExpenseResource::asEntity))

    override suspend fun deleteOtherExpenseResources(keys: List<String>) =
        otherExpenseResourceDao.deleteOtherExpenseResources(keys)

    override suspend fun setOtherExpenseResources(otherExpenseResource: List<OtherExpenseResource>) =
        otherExpenseResourceDao.upsertOtherExpenseResources(otherExpenseResource.map(OtherExpenseResource::asEntity))

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean =
        synchronizer.changeListSync(
        ).isSuccess
}
