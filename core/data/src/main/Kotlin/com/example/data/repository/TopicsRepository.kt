package com.example.data.repository

import com.example.data.Syncable
import com.example.model.Topic
import kotlinx.coroutines.flow.Flow

interface TopicsRepository : Syncable {

    fun getTopics(): Flow<List<Topic>>

    fun getTopic(id: String): Flow<Topic>

}
