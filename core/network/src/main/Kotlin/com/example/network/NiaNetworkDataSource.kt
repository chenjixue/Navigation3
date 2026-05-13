package com.example.network

import com.example.network.model.NetworkChangeList
import com.example.network.model.NetworkNewsResource
import com.example.network.model.NetworkTopic

/**
 * Interface representing network calls to the NIA backend
 */
interface NiaNetworkDataSource {
    suspend fun getTopics(ids: List<String>? = null): List<NetworkTopic>
    suspend fun getNewsResources(ids: List<String>? = null): List<NetworkNewsResource>
    suspend fun getNewsResourceChangeList(after: Int? = null): List<NetworkChangeList>
    suspend fun getTopicChangeList(after: Int? = null): List<NetworkChangeList>
}
