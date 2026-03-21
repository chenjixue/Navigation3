package com.example.foryou

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
//import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ForYouScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Greeting("foryou")
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier,
        style = MaterialTheme.typography.displayLarge
    )
}

//@Preview(showBackground = true)
//@Composable
fun GreetingPreview() {
//    Navigation3Theme {
//        Greeting("Android")
//    }
}