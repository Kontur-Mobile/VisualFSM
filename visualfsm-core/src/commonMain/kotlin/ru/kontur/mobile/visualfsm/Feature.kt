package ru.kontur.mobile.visualfsm

import kotlinx.atomicfu.locks.*
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Is the facade for FSM. Provides access to subscription on [state][State] changes
 * and [proceed] method to execute [actions][Action]
 *
 * @param stateSource the [state source][IStateSource] for storing and subscribing to state,
 * can be external to implement a common state tree between parent and child state machines (optional)
 * @param asyncWorker [AsyncWorker] instance for manage state-based asynchronous tasks (optional)
 * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic
 * on provided event calls (like logging, debugging, or metrics) (optional)
 * @param transitionsFactory a function that returns a [TransitionsFactory] instance to create the transition list
 * for the action
 */
open class Feature<STATE : State, ACTION : Action<STATE>>(
    stateSource: IStateSource<STATE>,
    asyncWorker: AsyncWorker<STATE, ACTION>? = null,
    transitionCallbacks: TransitionCallbacks<STATE>? = null,
    transitionsFactory: Feature<STATE, ACTION>.() -> TransitionsFactory<STATE, ACTION>,
) : BaseFeature<STATE, ACTION>() {

    internal val synchronizedObject = SynchronizedObject()

    private var transitionsFactory: TransitionsFactory<STATE, ACTION>? = null

    private val store: Store<STATE, ACTION>

    init {
        this.transitionsFactory = transitionsFactory(this)
        store = Store(StateSourceSharedFlowDecorator(stateSource), transitionCallbacks)
        asyncWorker?.bind(this)
    }

    /**
     * @param initialState initial [state][State]
     * @param asyncWorker [AsyncWorker] instance for manage state-based asynchronous tasks (optional)
     * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic
     * on provided event calls (like logging, debugging, or metrics) (optional)
     * @param transitionsFactory a [TransitionsFactory] instance to create the transition list for the action
     */
    constructor(
        initialState: STATE,
        asyncWorker: AsyncWorker<STATE, ACTION>? = null,
        transitionCallbacks: TransitionCallbacks<STATE>? = null,
        transitionsFactory: TransitionsFactory<STATE, ACTION>,
    ) : this(RootStateSource(initialState), asyncWorker, transitionCallbacks, { transitionsFactory })

    /**
     * @param initialState initial [state][State]
     * @param asyncWorker [AsyncWorker] instance for manage state-based asynchronous tasks (optional)
     * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic
     * on provided event calls (like logging, debugging, or metrics) (optional)
     * @param transitionsFactory a function that returns a [TransitionsFactory] instance
     * to create the transition list for the action
     */
    constructor(
        initialState: STATE,
        asyncWorker: AsyncWorker<STATE, ACTION>? = null,
        transitionCallbacks: TransitionCallbacks<STATE>? = null,
        transitionsFactory: Feature<STATE, ACTION>.() -> TransitionsFactory<STATE, ACTION>,
    ) : this(RootStateSource(initialState), asyncWorker, transitionCallbacks, transitionsFactory)

    /**
     * @param stateSource the [state source][IStateSource] for storing and subscribing to state,
     * can be external to implement a common state tree between parent and child state machines (optional)
     * @param asyncWorker [AsyncWorker] instance for manage state-based asynchronous tasks (optional)
     * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic
     * on provided event calls (like logging, debugging, or metrics) (optional)
     * @param transitionsFactory a [TransitionsFactory] instance to create the transition list for the action
     */
    constructor(
        stateSource: IStateSource<STATE>,
        asyncWorker: AsyncWorker<STATE, ACTION>? = null,
        transitionCallbacks: TransitionCallbacks<STATE>? = null,
        transitionsFactory: TransitionsFactory<STATE, ACTION>,
    ) : this(stateSource, asyncWorker, transitionCallbacks, { transitionsFactory })

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
    internal fun observeAllStates(): SharedFlow<STATE> {
        return store.observeAllStates()
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
        synchronized(synchronizedObject) {
            val transitionsFactory = this.transitionsFactory
            return store.proceed(
                action.apply {
                    if (transitionsFactory != null) setTransitions(transitionsFactory.create(action))
                }
            )
        }
    }
}