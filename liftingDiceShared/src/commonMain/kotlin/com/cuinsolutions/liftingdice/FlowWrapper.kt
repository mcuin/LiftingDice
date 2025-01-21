package com.cuinsolutions.liftingdice

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FlowWrapper<out T> internal constructor(private val scope: CoroutineScope, private val flow: Flow<T & Any>) {

    private var job: Job? = null
    private var isCancelled = false

    fun cancel() {
        isCancelled = true
        job?.cancel()
    }

    fun collect(onEach: (T & Any) -> Unit, onCompletion: (Throwable?) -> Unit) {
        if (isCancelled) return
        job = scope.launch {
            flow.onEach(onEach).onCompletion { cause: Throwable? ->
                onCompletion(cause)
            }.collect()
        }
    }
}

internal fun <T> Flow<T & Any>.wrap(scope: CoroutineScope = MainScope()): FlowWrapper<T> = FlowWrapper(scope, this)