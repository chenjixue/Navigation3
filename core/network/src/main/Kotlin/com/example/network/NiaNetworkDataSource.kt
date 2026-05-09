package com.example.network

import com.example.network.model.NetworkChangeList
import com.example.network.model.NetworkNewsResource

/**
 * Interface representing network calls to the NIA backend
 */
interface NiaNetworkDataSource {
    suspend fun getNewsResources(ids: List<String>? = null): List<NetworkNewsResource>
    suspend fun getNewsResourceChangeList(after: Int? = null): List<NetworkChangeList>
}
