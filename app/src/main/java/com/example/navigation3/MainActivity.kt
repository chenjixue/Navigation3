package com.example.navigation3

import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.example.navigation3.ui.theme.Navigation3Theme
import com.example.navigation.NavigationState
import com.example.navigation.rememberNavigationState
import com.example.foryou.ForYouNavKey
import com.example.forhe.ForHeNavKey
import com.example.forit.ForItNavKey
import androidx.navigation3.runtime.entryProvider
import com.example.navigation.TOP_LEVEL_NAV_ITEMS
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavKey
import com.example.navigation.Navigator
import com.example.navigation.toEntries
import com.example.navigation.forYouEntry
import com.example.navigation.forHeEntry
import com.example.navigation.forItEntry

@Composable
internal fun NiaApp(
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
) {
    val navigationState = rememberNavigationState(ForYouNavKey, TOP_LEVEL_NAV_ITEMS.keys)
    val navigator = remember { Navigator(navigationState) }
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            TOP_LEVEL_NAV_ITEMS.forEach { (navKey, navItem) ->
                val selected = navKey == navigationState.currentTopLevelKey
                this.item(
                    selected = selected,
                    onClick = { navigator.navigate(navKey) },
                    icon = {
                        if (selected) {
                            Icon(
                                imageVector = navItem.selectedIcon,
                                contentDescription = null,
                            )
                        } else {
                            Icon(
                                imageVector = navItem.unselectedIcon,
                                contentDescription = null,
                            )
                        }
                    },
                    label = { Text(stringResource(navItem.iconTextId)) },
                    modifier = modifier
                )
            }
        },
        modifier = modifier,
    ) {
//        val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()
        val entryProvider = entryProvider {
            forYouEntry()
            forHeEntry()
            forItEntry()
//            bookmarksEntry(navigator)
//            interestsEntry(navigator)
//            topicEntry(navigator)
//            searchEntry(navigator)
        }

        NavDisplay(
            entries = navigationState.toEntries(entryProvider),
//            sceneStrategy = listDetailStrategy,
            onBack = { navigator.goBack() },
        )
    }
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Navigation3Theme {
                NiaApp()
            }
        }
    }
}

