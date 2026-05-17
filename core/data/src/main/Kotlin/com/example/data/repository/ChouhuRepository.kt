package com.example.data.repository

import android.R
import com.example.model.ChouhuResource
import com.example.data.Syncable
import kotlinx.coroutines.flow.Flow

interface ChouhuRepository : Syncable {
    fun getChouhuResources(): Flow<List<ChouhuResource>>

    fun getChouhuResources(date: String): Flow<List<ChouhuResource>>

    suspend fun deleteChouhuResources(keys: List<String>)

    suspend fun setChouhuResources(chouhuResource: List<ChouhuResource>)
}

