package de.honoka.lavender.android.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun launchCoroutineOnUiThread(block: suspend () -> Unit) {
    CoroutineScope(Dispatchers.Main).launch {
        block()
    }
}