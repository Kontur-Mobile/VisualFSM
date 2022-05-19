package ru.kontur.mobile.visualfsm

import io.reactivex.Observable

/**
 * Is the facade for FSM. Provides access to subscription on [state][State] changes
 * and [proceed] method to execute [actions][Action]
 *
 * @param initialState initial [state][State]
 * @param asyncWorker [AsyncWorkerRx] instance for manage state-based asynchronous tasks (optional)
 * @param transitionCallbacks the [callbacks][TransitionCallbacks] that used on some [Action] and [Transition] actions (optional)
 */
open class FeatureRx<STATE : State, ACTION : Action<STATE>>(
    initialState: STATE,
    asyncWorker: AsyncWorkerRx<STATE, ACTION>? = null,
    transitionCallbacks: TransitionCallbacks<STATE>? = null,
) {
    private val store = StoreRx<STATE, ACTION>(initialState, transitionCallbacks)

    init {
        asyncWorker?.bind(this)
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
    open fun proceed(action: ACTION) {
        return store.proceed(action)
    }
}