package ru.kontur.mobile.visualfsm

import kotlinx.coroutines.flow.Flow

abstract class Feature<STATE : State, ACTION : Action<STATE>>(
    private val store: Store<STATE, ACTION>,
    asyncWorker: AsyncWorker<STATE, ACTION>,
) {
    init {
        asyncWorker.bind(store)
    }

    fun observeState(): Flow<STATE> {
        return store.observeState()
    }

    suspend fun getSingleState(): STATE {
        return store.getStateSingle()
    }

    protected fun proceed(action: ACTION) {
        return store.proceed(action)
    }
}