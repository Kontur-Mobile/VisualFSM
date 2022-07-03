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
@Deprecated(
    message = "Deprecated, because it not support code generation.\n" +
            "For enable code generation:\n" +
            "  1. Use annotation processor and tools dependencies in gradle files." +
            "  2. Add generated code to source code directories.\n" +
            "  3. Annotate the Feature class with the GenerateTransitionFactory annotation.\n" +
            "  4. Pass the transitionFactory parameter to the Feature constructor.\n" +
            "Please see the readme file (https://github.com/g0rd1/VisualFSM/blob/g0rd1/code-generation/docs/eng/Quickstart-ENG.md) for detailed information on set up code generation.",
    replaceWith = ReplaceWith("Constructor with transitionFactory parameter.")
)
constructor(initialState: STATE, asyncWorker: AsyncWorker<STATE, ACTION>? = null, transitionCallbacks: TransitionCallbacks<STATE>? = null) {

    /**
     * @param initialState initial [state][State]
     * @param asyncWorker [AsyncWorker] instance for manage state-based asynchronous tasks (optional)
     * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic on provided event calls (like logging, debugging, or metrics) (optional)
     * @param transitionFactory a [TransitionFactory] instance to create the transition list for the action
     */
    @Suppress("DEPRECATION")
    constructor(
        initialState: STATE,
        asyncWorker: AsyncWorker<STATE, ACTION>? = null,
        transitionCallbacks: TransitionCallbacks<STATE>? = null,
        transitionFactory: TransitionFactory<STATE, ACTION>,
    ) : this(initialState, asyncWorker, transitionCallbacks) {
        this.transitionFactory = transitionFactory
    }

    /**
     * @param initialState initial [state][State]
     * @param asyncWorker [AsyncWorker] instance for manage state-based asynchronous tasks (optional)
     * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic on provided event calls (like logging, debugging, or metrics) (optional)
     * @param transitionFactory a function that returns a [TransitionFactory] instance to create the transition list for the action
     */
    @Suppress("DEPRECATION")
    constructor(
        initialState: STATE,
        asyncWorker: AsyncWorker<STATE, ACTION>? = null,
        transitionCallbacks: TransitionCallbacks<STATE>? = null,
        transitionFactory: Feature<STATE, ACTION>.() -> TransitionFactory<STATE, ACTION>,
    ) : this(initialState, asyncWorker, transitionCallbacks) {
        this.transitionFactory = transitionFactory(this)
    }

    private var transitionFactory: TransitionFactory<STATE, ACTION>? = null

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
        val transitionFactory = this.transitionFactory
        return store.proceed(
            action.apply {
                if (transitionFactory != null) setTransitions(transitionFactory.create(action))
            }
        )
    }

    companion object
}