package com.example.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.forhe.ForHeNavKey
import com.example.forhe.ForHeScreen

fun EntryProviderScope<NavKey>.forHeEntry() {
    entry<ForHeNavKey> {
        ForHeScreen(
//            onTopicClick = navigator::navigateToTopic,
        )
    }
}