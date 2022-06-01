package ru.kontur.mobile.visualfsm

import kotlinx.coroutines.flow.Flow

/**
 * Is the facade for FSM. Provides access to subscription on [state][State] changes
 * and [proceed] method to execute [actions][Action]
 *
 * @param initialState initial [state][State]
 * @param asyncWorker [AsyncWorker] instance for manage state-based asynchronous tasks (optional)
 * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic on provided event calls (like logging, debugging, or metrics) (optional)
 */
open class Feature<STATE : State, ACTION : Action<STATE>>
@Deprecated(message = "") // TODO Add message to annotation
constructor(initialState: STATE, asyncWorker: AsyncWorker<STATE, ACTION>? = null, transitionCallbacks: TransitionCallbacks<STATE>? = null) {

    @Suppress("DEPRECATION")
    constructor(
        initialState: STATE,
        asyncWorker: AsyncWorker<STATE, ACTION>? = null,
        transitionCallbacks: TransitionCallbacks<STATE>? = null,
        generatedActionFactory: GeneratedActionFactory<ACTION>,
    ) : this(initialState, asyncWorker, transitionCallbacks) {
        this.generatedActionFactory = generatedActionFactory
    }

    private var generatedActionFactory: GeneratedActionFactory<ACTION>? = null

    private val store: Store<STATE, ACTION> = Store(initialState, transitionCallbacks)

    init {
        asyncWorker?.bind(this)
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
    fun getCurrentState(): STATE {
        return store.getCurrentState()
    }

    /**
     * Submits an [action][Action] to be executed to the [store][Store]
     *
     * @param action [Action] to run
     */
    fun proceed(action: ACTION) {
        return store.proceed(generatedActionFactory?.create(action) ?: action)
    }
}