package com.example.navigation3

import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.example.navigation3.ui.theme.Navigation3Theme
import com.example.navigation.NavigationState
import com.example.navigation.rememberNavigationState
//import com.example.foryou.ForYouNavKey
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
//import com.example.forhe.ForHeNavKey
import com.example.forit.ForItNavKey
import androidx.navigation3.runtime.entryProvider
import com.example.navigation.TOP_LEVEL_NAV_ITEMS
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.flow.combine
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.activity.viewModels
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation3.runtime.NavKey
import com.example.navigation.Navigator
import com.example.navigation.toEntries
//import com.example.navigation.forYouEntry
//import com.example.navigation.forHeEntry
import com.example.navigation.forItEntry
import kotlin.text.get


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
//    private val viewModel: MainActivityViewModel by viewModels()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NiaApp(
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
) {
    val navigationState = rememberNavigationState(ForItNavKey, TOP_LEVEL_NAV_ITEMS.keys)
    val navigator = remember { Navigator(navigationState) }
    // NavigationSuiteScaffold(
    //     navigationSuiteItems = {
    //         TOP_LEVEL_NAV_ITEMS.forEach { (navKey, navItem) ->
    //             val selected = navKey == navigationState.currentTopLevelKey
    //             this.item(
    //                 selected = selected,
    //                 onClick = { navigator.navigate(navKey) },
    //                 icon = {
    //                     if (selected) {
    //                         Icon(
    //                             imageVector = navItem.selectedIcon,
    //                             contentDescription = null,
    //                         )
    //                     } else {
    //                         Icon(
    //                             imageVector = navItem.unselectedIcon,
    //                             contentDescription = null,
    //                         )
    //                     }
    //                 },
    //                 label = { Text(stringResource(navItem.iconTextId)) },
    //                 modifier = modifier
    //             )
    //         }
    //     },
    //     modifier = modifier,
    // ) {

        Box(
            modifier = modifier,
        ) {
            val destination = TOP_LEVEL_NAV_ITEMS[navigationState.currentTopLevelKey]
            
            Column {
//                if (navigationState.currentTopLevelKey != ForItNavKey) {
//                    CenterAlignedTopAppBar(
//                        title = { Text(text = if (destination?.titleTextId != null) stringResource(id = destination.titleTextId) else "") },
//                        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
//                    )
//                }
                val entryProvider = entryProvider {
//                forYouEntry()
//                forHeEntry()
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


    // }
}



