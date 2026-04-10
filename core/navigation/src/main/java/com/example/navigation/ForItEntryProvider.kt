package com.example.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.forit.ForItNavKey
import com.example.forit.ForItScreen

fun EntryProviderScope<NavKey>.forItEntry() {
    entry<ForItNavKey> {
        ForItScreen(
//            onTopicClick = navigator::navigateToTopic,
        )
    }
}