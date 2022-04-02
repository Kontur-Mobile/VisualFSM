package ru.kontur.mobile.visualfsm

import io.reactivex.Observable
import io.reactivex.Single

abstract class FeatureRx<STATE : State, ACTION : Action<STATE>>(
    private val store: StoreRx<STATE, ACTION>,
    asyncWorker: AsyncWorkerRx<STATE, ACTION>,
) {
    init {
        asyncWorker.bind(store)
    }

    fun observeState(): Observable<STATE> {
        return store.observeState()
    }

    fun getSingleState(): Single<STATE> {
        return store.getStateSingle()
    }

    protected fun proceed(action: ACTION) {
        return store.proceed(action)
    }
}