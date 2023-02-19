package ru.kontur.mobile.visualfsm

import kotlinx.atomicfu.locks.*
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Is the facade for FSM. Provides access to subscription on [state][State] changes
 * and [proceed] method to execute [actions][Action]
 *
 * @param initialState initial [state][State]
 * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic on provided event calls (like logging, debugging, or metrics) (optional)
 */
open class Feature<STATE : State, ACTION : Action<STATE>>
@Deprecated(
    message = "Deprecated, because it not support code generation.\n" +
            "Code generation not configured or configured incorrectly.\n" +
            "See the quickstart file for more information on set up code generation (https://github.com/Kontur-Mobile/VisualFSM/blob/main/docs/Quickstart.md).",
    replaceWith = ReplaceWith("Constructor with transitionsFactory parameter.")
) constructor(
    initialState: STATE,
    transitionCallbacks: TransitionCallbacks<STATE>? = null
) : BaseFeature<STATE, ACTION>() {

    /**
     * @param initialState initial [state][State]
     * @param asyncWorker [AsyncWorker] instance for manage state-based asynchronous tasks (optional)
     * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic on provided event calls (like logging, debugging, or metrics) (optional)
     * @param transitionsFactory a [TransitionsFactory] instance to create the transition list for the action
     */
    @Suppress("DEPRECATION")
    constructor(
        initialState: STATE,
        asyncWorker: AsyncWorker<STATE, ACTION>? = null,
        transitionCallbacks: TransitionCallbacks<STATE>? = null,
        transitionsFactory: TransitionsFactory<STATE, ACTION>,
    ) : this(initialState, transitionCallbacks) {
        this.transitionsFactory = transitionsFactory
        asyncWorker?.bind(this)
    }

    /**
     * @param initialState initial [state][State]
     * @param asyncWorker [AsyncWorker] instance for manage state-based asynchronous tasks (optional)
     * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic on provided event calls (like logging, debugging, or metrics) (optional)
     * @param transitionsFactory a function that returns a [TransitionsFactory] instance to create the transition list for the action
     */
    @Suppress("DEPRECATION")
    constructor(
        initialState: STATE,
        asyncWorker: AsyncWorker<STATE, ACTION>? = null,
        transitionCallbacks: TransitionCallbacks<STATE>? = null,
        transitionsFactory: Feature<STATE, ACTION>.() -> TransitionsFactory<STATE, ACTION>,
    ) : this(initialState, transitionCallbacks) {
        this.transitionsFactory = transitionsFactory(this)
        asyncWorker?.bind(this)
    }

    /**
     * @param initialState initial [state][State]
     * @param asyncWorker [AsyncWorker] instance for manage state-based asynchronous tasks (optional)
     * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic on provided event calls (like logging, debugging, or metrics) (optional)
     */
    @Deprecated(
        message = "Deprecated, because it not support code generation.\n" +
                "Code generation not configured or configured incorrectly.\n" +
                "See the quickstart file for more information on set up code generation (https://github.com/Kontur-Mobile/VisualFSM/blob/main/docs/Quickstart.md).",
        replaceWith = ReplaceWith("Constructor with transitionsFactory parameter.")
    )
    @Suppress("DEPRECATION")
    constructor(
        initialState: STATE,
        asyncWorker: AsyncWorker<STATE, ACTION>? = null,
        transitionCallbacks: TransitionCallbacks<STATE>? = null
    ) : this(initialState, transitionCallbacks) {
        asyncWorker?.bind(this)
    }

    private var transitionsFactory: TransitionsFactory<STATE, ACTION>? = null

    private val store: Store<STATE, ACTION> = Store(initialState, transitionCallbacks)

    /**
     * Provides a [flow][StateFlow] of [states][State]
     *
     * @return a [flow][StateFlow] of [states][State]
     */
    fun observeState(): StateFlow<STATE> {
        return store.observeState()
    }

    /**
     * Provides a [flow][SharedFlow] of [states][State] for AsyncWorker
     *
     * @return a [flow][SharedFlow] of [states][State]
     */
    internal fun observeStateShared(): SharedFlow<STATE> {
        return store.observeStateShared()
    }

    /**
     * Returns current state
     *
     * @return current [state][State]
     */
    override fun getCurrentState(): STATE {
        return store.getCurrentState()
    }

    /**
     * Submits an [action][Action] to be executed to the [store][Store]
     *
     * @param action [Action] to run
     */
    override fun proceed(action: ACTION) {
        synchronized(this) {
            val transitionsFactory = this.transitionsFactory
            return store.proceed(
                action.apply {
                    if (transitionsFactory != null) setTransitions(transitionsFactory.create(action))
                }
            )
        }
    }
}