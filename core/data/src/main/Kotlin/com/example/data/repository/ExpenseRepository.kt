package com.example.data.repository

import com.example.data.Syncable
import com.example.model.NewsResource
import com.example.model.OtherExpenseResource
import com.example.model.PeopleExpenseResource
import kotlinx.coroutines.flow.Flow


/**
 * Data layer implementation for [NewsResource]
 */
interface ExpenseRepository : Syncable {
    /**
     * Returns available news resources that match the specified [query].
     */

    fun getPeopleExpenseResources(dataTime: String): Flow<List<PeopleExpenseResource>>
    fun getOtherExpenseResources(dataTime: String): Flow<List<OtherExpenseResource>>

    suspend fun deletePeopleExpenseResources(keys: List<String>)
    suspend fun setPeopleExpenseResources(peopleExpenseResources: List<PeopleExpenseResource>)

    suspend fun deleteOtherExpenseResources(keys: List<String>)

    suspend fun setOtherExpenseResources(otherExpenseResources: List<OtherExpenseResource>)
}