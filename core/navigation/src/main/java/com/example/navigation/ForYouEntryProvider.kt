package com.example.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.foryou.ForYouNavKey
import com.example.foryou.ForYouScreen

fun EntryProviderScope<NavKey>.forYouEntry() {
    entry<ForYouNavKey> {
        ForYouScreen(
//            onTopicClick = navigator::navigateToTopic,
        )
    }
}