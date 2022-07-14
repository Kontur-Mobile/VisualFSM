package ru.kontur.mobile.visualfsm.rxjava2

import io.reactivex.Observable
import ru.kontur.mobile.visualfsm.*

/**
 * Is the facade for FSM. Provides access to subscription on [state][State] changes
 * and [proceed] method to execute [actions][Action]
 *
 * @param initialState initial [state][State]
 * @param asyncWorker [AsyncWorkerRx] instance for manage state-based asynchronous tasks (optional)
 * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic on provided event calls (like logging, debugging, or metrics) (optional)
 */
open class FeatureRx<STATE : State, ACTION : Action<STATE>>
@Deprecated(
    message = "Deprecated, because it not support code generation.\n" +
            "Code generation not configured or configured incorrectly.\n" +
            "See the quickstart file for more information on set up code generation (https://github.com/Kontur-Mobile/VisualFSM/blob/main/docs/eng/Quickstart-ENG.md).",
    replaceWith = ReplaceWith("Constructor with transitionsFactory parameter.")
)
constructor(
    initialState: STATE,
    asyncWorker: AsyncWorkerRx<STATE, ACTION>? = null,
    transitionCallbacks: TransitionCallbacks<STATE>? = null,
) : BaseFeature<STATE, ACTION>() {

    /**
     * @param initialState initial [state][State]
     * @param asyncWorker [AsyncWorkerRx] instance for manage state-based asynchronous tasks (optional)
     * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic on provided event calls (like logging, debugging, or metrics) (optional)
     * @param transitionsFactory a [TransitionsFactory] instance to create the transition list for the action
     */
    @Suppress("DEPRECATION")
    constructor(
        initialState: STATE,
        asyncWorker: AsyncWorkerRx<STATE, ACTION>? = null,
        transitionCallbacks: TransitionCallbacks<STATE>? = null,
        transitionsFactory: TransitionsFactory<STATE, ACTION>,
    ) : this(initialState, asyncWorker, transitionCallbacks) {
        this.transitionsFactory = transitionsFactory
    }

    /**
     * @param initialState initial [state][State]
     * @param asyncWorker [AsyncWorkerRx] instance for manage state-based asynchronous tasks (optional)
     * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic on provided event calls (like logging, debugging, or metrics) (optional)
     * @param transitionsFactory a function that returns a [TransitionsFactory] instance to create the transition list for the action
     */
    @Suppress("DEPRECATION")
    constructor(
        initialState: STATE,
        asyncWorker: AsyncWorkerRx<STATE, ACTION>? = null,
        transitionCallbacks: TransitionCallbacks<STATE>? = null,
        transitionsFactory: FeatureRx<STATE, ACTION>.() -> TransitionsFactory<STATE, ACTION>,
    ) : this(initialState, asyncWorker, transitionCallbacks) {
        this.transitionsFactory = transitionsFactory(this)
    }

    private var getTransitionsFactory: (FeatureRx<STATE, ACTION>.() -> TransitionsFactory<STATE, ACTION>)? = null

    private var transitionsFactory: TransitionsFactory<STATE, ACTION>? = null

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
    override fun getCurrentState(): STATE {
        return store.getCurrentState()
    }

    /**
     * Submits an [action][Action] to be executed to the [store][StoreRx]
     *
     * @param action [Action] to run
     */
    override fun proceed(action: ACTION) {
        val transitionsFactory = this.transitionsFactory
        return store.proceed(
            action.apply {
                if (transitionsFactory != null) setTransitions(transitionsFactory.create(action))
            }
        )
    }
}