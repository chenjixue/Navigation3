package com.example.data.repository

import com.example.data.Syncable
import com.example.model.ChouhuResource
import com.example.model.SaleResource
import kotlinx.coroutines.flow.Flow

interface SaleRepository: Syncable  {
    fun getSaleResources(): Flow<List<SaleResource>>

    fun getSaleResources(date: String): Flow<List<SaleResource>>

    suspend fun deleteSaleResources(keys: List<String>)

    suspend fun setSaleResources(saleResource: List<SaleResource>)

    fun getNoSaleResources(): Flow<List<SaleResource>>

    fun getNoSaleResources(date: String): Flow<List<SaleResource>>

    suspend fun deleteNoSaleResources(keys: List<String>)

    suspend fun setNoSaleResources(saleResource: List<SaleResource>)
}