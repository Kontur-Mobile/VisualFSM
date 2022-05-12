package ru.kontur.mobile.visualfsm

import io.reactivex.Observable

/**
 * Is the facade for FSM. Provides access to subscription on [state][State] changes
 * and manages [actions][Action] start with [proceed] method call
 *
 * @param store [StoreRx] instance
 * @param asyncWorker [AsyncWorkerRx] instance
 */
abstract class FeatureRx<STATE : State, ACTION : Action<STATE>>(
    private val store: StoreRx<STATE, ACTION>,
    asyncWorker: AsyncWorkerRx<STATE, ACTION>,
) {

    init {
        asyncWorker.bind(store)
    }

    /**
     * Provides a [observable][Observable] of [states][State]
     *
     * @return a [observable][Observable] of [states][State]
     */
    fun observeState(): Observable<STATE> {
        return store.observeState()
    }

    /**
     * Returns current state
     *
     * @return current [state][State]
     */
    fun getCurrentState(): STATE {
        return store.getCurrentState()
    }

    /**
     * Submits an [action][Action] to be executed to the [store][StoreRx]
     *
     * @param action [Action] to run
     */
    protected fun proceed(action: ACTION) {
        return store.proceed(action)
    }
}