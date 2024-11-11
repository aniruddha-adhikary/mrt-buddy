package net.adhikary.mrtbuddy.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import  kotlinx.coroutines.flow.collect

import kotlinx.coroutines.withContext


@Composable
fun <T> ObserveAsEvents(
    flow: Flow<T>,
    onEvent: (T) -> Unit
) {
    LaunchedEffect(flow) {
        flow.collect { event ->
            onEvent(event)
        }
    }
}

@Composable
fun<T> Flow<T>.observeAsActions(onEach:(T) -> Unit){
    val flow = this
    LaunchedEffect(key1 =  flow){
        flow.onEach(onEach).collect()
    }
}