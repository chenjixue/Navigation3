/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.database.model.NewsResourceEntity
import com.example.database.model.PopulatedNewsResource
import com.example.model.NewsResource
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [NewsResource] and [NewsResourceEntity] access
 */
@Dao
interface NewsResourceDao {

    /**
     * Fetches news resources that match the query parameters
     */
    @Transaction
    @Query(
        value = """
            SELECT * FROM news_resources
            WHERE 
                CASE WHEN :useFilterNewsIds
                    THEN id IN (:filterNewsIds)
                    ELSE 1
                END
             AND
                CASE WHEN :useFilterTopicIds
                    THEN id IN
                        (
                            SELECT news_resource_id FROM news_resources_topics
                            WHERE topic_id IN (:filterTopicIds)
                        )
                    ELSE 1
                END
            ORDER BY publish_date DESC
    """,
    )
    fun getNewsResources(
        useFilterTopicIds: Boolean = false,
        filterTopicIds: Set<String> = emptySet(),
        useFilterNewsIds: Boolean = false,
        filterNewsIds: Set<String> = emptySet(),
    ): Flow<List<PopulatedNewsResource>>


}
