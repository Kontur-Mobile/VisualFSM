package ru.kontur.mobile.visualfsm

import kotlinx.coroutines.flow.Flow
import kotlinx.atomicfu.locks.*
import ru.kontur.mobile.visualfsm.backStack.BackStackStrategy
import ru.kontur.mobile.visualfsm.feature.BaseFeature
import ru.kontur.mobile.visualfsm.store.Store

/**
 * Is the facade for FSM. Provides access to subscription on [state][State] changes
 * and [proceed] method to execute [actions][Action]
 *
 * @param initialState initial [state][State]
 * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic on provided event calls (like logging, debugging, or metrics) (optional)
 * @param stateDependencyManager state dependency manager [StateDependencyManager]
 * @param restoredBackStates list Pairs id and state for restored back state stack
 */
open class Feature<STATE : State, ACTION : Action<STATE>>
@Deprecated(
    message = "Deprecated, because it not support code generation.\n" +
            "Code generation not configured or configured incorrectly.\n" +
            "See the quickstart file for more information on set up code generation (https://github.com/Kontur-Mobile/VisualFSM/blob/main/docs/Quickstart.md).",
    replaceWith = ReplaceWith("Constructor with transitionsFactory parameter.")
) constructor(
    initialState: STATE,
    initialStateAddToBackStackStrategy: BackStackStrategy = BackStackStrategy.NO_ADD,
    transitionCallbacks: TransitionCallbacks<STATE>? = null,
    stateDependencyManager: StateDependencyManager<STATE>? = null,
    restoredBackStates: List<Pair<Int, STATE>> = listOf(),
) : BaseFeature<STATE, ACTION>(stateDependencyManager, transitionCallbacks, restoredBackStates) {

    /**
     * @param initialState initial [state][State]
     * @param asyncWorker [AsyncWorker] instance for manage state-based asynchronous tasks (optional)
     * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic on provided event calls (like logging, debugging, or metrics) (optional)
     * @param transitionsFactory a [TransitionsFactory] instance to create the transition list for the action
     * @param stateDependencyManager state dependency manager [StateDependencyManager]
     * @param restoredBackStates list Pairs id and state for restored back state stack
     */
    @Suppress("DEPRECATION")
    constructor(
        initialState: STATE,
        initialStateAddToBackStackStrategy: BackStackStrategy = BackStackStrategy.NO_ADD,
        asyncWorker: AsyncWorker<STATE, ACTION>? = null,
        transitionCallbacks: TransitionCallbacks<STATE>? = null,
        transitionsFactory: TransitionsFactory<STATE, ACTION>,
        stateDependencyManager: StateDependencyManager<STATE>? = null,
        restoredBackStates: List<Pair<Int, STATE>> = listOf(),
    ) : this(initialState, initialStateAddToBackStackStrategy, transitionCallbacks, stateDependencyManager, restoredBackStates) {
        this.transitionsFactory = transitionsFactory
        asyncWorker?.bind(this)
    }

    /**
     * @param initialState initial [state][State]
     * @param asyncWorker [AsyncWorker] instance for manage state-based asynchronous tasks (optional)
     * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic on provided event calls (like logging, debugging, or metrics) (optional)
     * @param transitionsFactory a function that returns a [TransitionsFactory] instance to create the transition list for the action
     * @param stateDependencyManager state dependency manager [StateDependencyManager]
     * @param restoredBackStates list Pairs id and state for restored back state stack
     */
    @Suppress("DEPRECATION")
    constructor(
        initialState: STATE,
        initialStateAddToBackStackStrategy: BackStackStrategy = BackStackStrategy.NO_ADD,
        asyncWorker: AsyncWorker<STATE, ACTION>? = null,
        transitionCallbacks: TransitionCallbacks<STATE>? = null,
        transitionsFactory: Feature<STATE, ACTION>.() -> TransitionsFactory<STATE, ACTION>,
        stateDependencyManager: StateDependencyManager<STATE>? = null,
        restoredBackStates: List<Pair<Int, STATE>> = listOf(),
    ) : this(initialState, initialStateAddToBackStackStrategy, transitionCallbacks, stateDependencyManager, restoredBackStates) {
        this.transitionsFactory = transitionsFactory(this)
        asyncWorker?.bind(this)
    }

    /**
     * @param initialState initial [state][State]
     * @param asyncWorker [AsyncWorker] instance for manage state-based asynchronous tasks (optional)
     * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic on provided event calls (like logging, debugging, or metrics) (optional)
     * @param stateDependencyManager state dependency manager [StateDependencyManager]
     * @param restoredBackStates list Pairs id and state for restored back state stack
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
        initialStateAddToBackStackStrategy: BackStackStrategy = BackStackStrategy.NO_ADD,
        asyncWorker: AsyncWorker<STATE, ACTION>? = null,
        transitionCallbacks: TransitionCallbacks<STATE>? = null,
        stateDependencyManager: StateDependencyManager<STATE>? = null,
        restoredBackStates: List<Pair<Int, STATE>> = listOf(),
    ) : this(initialState, initialStateAddToBackStackStrategy, transitionCallbacks, stateDependencyManager, restoredBackStates) {
        asyncWorker?.bind(this)
    }

    private var transitionsFactory: TransitionsFactory<STATE, ACTION>? = null

    override val store: Store<STATE, ACTION> =
        Store(initialState, transitionCallbacks, stateDependencyManager, backStatesStack)

    /**
     * Provides a [flow][Flow] of [states][State]
     *
     * @return a [flow][Flow] of [states][State]
     */
    fun observeState(): Flow<STATE> {
        return store.observeState()
    }

    /**
     * Submits an [action][Action] to be executed to the [store][Store]
     *
     * @param action [Action] to run
     */
    override fun proceed(action: ACTION) {
        synchronized(this) {
            val transitionsFactory = this.transitionsFactory
            store.proceed(
                action.apply {
                    if (transitionsFactory != null) setTransitions(transitionsFactory.create(action))
                }
            )
        }
    }
}