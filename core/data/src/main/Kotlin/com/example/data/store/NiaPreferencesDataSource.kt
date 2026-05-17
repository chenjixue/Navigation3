package com.example.data.store

import android.util.Log
import androidx.datastore.core.DataStore
import com.example.data.store.UserPreferences
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import com.example.model.UserData
import kotlin.collections.remove


class NiaPreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>,
) {
//                viewedNewsResources = it.viewedNewsResourceIdsMap.keys,
//                followedTopics = it.followedTopicIdsMap.keys,
//                shouldHideOnboarding = it.shouldHideOnboarding,
    val userData = userPreferences.data
        .map {
            UserData(
                it.selectedDate
            )
        }

    suspend fun getChangeListVersions() = userPreferences.data
        .map {
            ChangeListVersions(
                topicVersion = it.topicChangeListVersion,
                newsResourceVersion = it.newsResourceChangeListVersion,
            )
        }
        .firstOrNull() ?: ChangeListVersions()

    suspend fun setTopicIdFollowed(topicId: String, followed: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    if (followed) {
                        followedTopicIds.put(topicId, true)
                    } else {
                        followedTopicIds.remove(topicId)
                    }
                    updateShouldHideOnboardingIfNecessary()
                }
            }
        } catch (ioException: IOException) {
            Log.e("NiaPreferences", "Failed to update user preferences", ioException)
        }
    }

    suspend fun setSelectedDate(date: String) {
        try {
            userPreferences.updateData {
                it.copy {
                    selectedDate =  date
                }
            }
        } catch (ioException: IOException) {
            Log.e("NiaPreferences", "Failed to update user preferences", ioException)
        }
    }

    suspend fun updateChangeListVersion(update: ChangeListVersions.() -> ChangeListVersions) {
        try {
            // 升级数据版本
            userPreferences.updateData { currentPreferences ->
                val updatedChangeListVersions = update(
                    ChangeListVersions(
                        topicVersion = currentPreferences.topicChangeListVersion,
                        newsResourceVersion = currentPreferences.newsResourceChangeListVersion,
                    ),
                )

                 /* 生成一个新的UserPreferences对象,
                UserPreferences,一般通过 toBuilder() 获取一个基于当前数据拷贝的可变 Builder 对象, 调用 Builder 的 set 方法修改数据 
               再调用 build() 生成一个全新的、不可变的 UserPreferences 对象,但是kotlin用一个扩展函数copy方法封装了上述流程,但本质还是一样 */
                currentPreferences.copy {
                    topicChangeListVersion = updatedChangeListVersions.topicVersion
                    newsResourceChangeListVersion = updatedChangeListVersions.newsResourceVersion
                }
            }
        } catch (ioException: IOException) {
            Log.e("NiaPreferences", "Failed to update user preferences", ioException)
        }
    }

    suspend fun setNewsResourcesViewed(newsResourceIds: List<String>, viewed: Boolean) {
        userPreferences.updateData { prefs ->
            prefs.copy {
                newsResourceIds.forEach { id ->
                    if (viewed) {
                        viewedNewsResourceIds.put(id, true)
                    } else {
                        viewedNewsResourceIds.remove(id)
                    }
                }
            }
        }
    }


    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        userPreferences.updateData {
            it.copy { this.shouldHideOnboarding = shouldHideOnboarding }
        }
    }
}

private fun UserPreferencesKt.Dsl.updateShouldHideOnboardingIfNecessary() {
    if (followedTopicIds.isEmpty() && followedAuthorIds.isEmpty()) {
        shouldHideOnboarding = false
    }
}