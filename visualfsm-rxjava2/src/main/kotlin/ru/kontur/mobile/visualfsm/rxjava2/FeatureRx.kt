package ru.kontur.mobile.visualfsm.rxjava2

import io.reactivex.Observable
import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.GeneratedActionFactory
import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.TransitionCallbacks

/**
 * Is the facade for FSM. Provides access to subscription on [state][State] changes
 * and [proceed] method to execute [actions][Action]
 *
 * @param initialState initial [state][State]
 * @param asyncWorker [AsyncWorkerRx] instance for manage state-based asynchronous tasks (optional)
 * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic on provided event calls (like logging, debugging, or metrics) (optional)
 */
open class FeatureRx<STATE : State, ACTION : Action<STATE>>
@Deprecated(message = "") // TODO Add message to annotation
constructor(
    initialState: STATE,
    asyncWorker: AsyncWorkerRx<STATE, ACTION>? = null,
    transitionCallbacks: TransitionCallbacks<STATE>? = null,
) {

    @Suppress("DEPRECATION")
    constructor(
        initialState: STATE,
        asyncWorker: AsyncWorkerRx<STATE, ACTION>? = null,
        transitionCallbacks: TransitionCallbacks<STATE>? = null,
        generatedActionFactory: GeneratedActionFactory<ACTION>,
    ) : this(initialState, asyncWorker, transitionCallbacks) {
        this.generatedActionFactory = generatedActionFactory
    }

    private var generatedActionFactory: GeneratedActionFactory<ACTION>? = null

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
    fun proceed(action: ACTION) {
        synchronized(this) {
            return store.proceed(generatedActionFactory?.create(action) ?: action)
        }
    }
}