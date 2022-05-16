package ru.kontur.mobile.visualfsm

import kotlinx.coroutines.flow.Flow

/**
 * Is the facade for FSM. Provides access to subscription on [state][State] changes
 * and manages [actions][Action] start with [proceed] method call
 *
 * @param store [Store] instance
 * @param asyncWorker [AsyncWorker] instance
 */
abstract class Feature<STATE : State, ACTION : Action<STATE>>(
    private val store: Store<STATE, ACTION>,
    asyncWorker: AsyncWorker<STATE, ACTION>,
) {

    init {
        asyncWorker.bind(store)
    }

    /**
     * Provides a [flow][Flow] of [states][State]
     *
     * @return a [flow][Flow] of [states][State]
     */
    fun observeState(): Flow<STATE> {
        return store.observeState()
    }

    /**
     * Returns current state
     *
     * @return current [state][State]
     */
    fun getSingleState(): STATE {
        return store.getCurrentState()
    }

    /**
     * Submits an [action][Action] to be executed to the [store][Store]
     *
     * @param action [Action] to run
     */
    protected fun proceed(action: ACTION) {
        return store.proceed(action)
    }
}