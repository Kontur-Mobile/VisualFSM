package ru.kontur.mobile.visualfsm

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow

/**
 * Manages the start and stop of state-based asynchronous tasks
 */
abstract class AsyncWorker<STATE : State, ACTION : Action<STATE>> {

    /**
     * Represents [a coroutine scope][CoroutineScope] for the currently running async task
     */
    protected open val taskScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    /**
     * Is [a coroutine scope][CoroutineScope] used to subscribe
     * to [store's][Store] [flow of states][State]
     */
    protected open val subscriptionScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private lateinit var store: Store<STATE, ACTION>
    private var launchedAsyncState: STATE? = null
    private var subscriptionContinuation: Job? = null
    private var launchedAsyncStateContinuation: Job? = null

    /**
     * Binds received [store][Store] to [async worker][AsyncWorker]
     * and starts [observing][Store.observeState] [states][State]
     *
     * @param store provided [Store]
     */
    fun bind(store: Store<STATE, ACTION>) {
        this.store = store
        subscriptionContinuation = subscriptionScope.launch {
            initSubscription(store.observeState())
        }
    }

    /**
     * Disposes async task and stops observing states
     */
    fun unbind() {
        dispose()
        subscriptionContinuation?.cancel()
    }

    /**
     * Provides a state flow to manage async work based on state changes
     *
     * @param states a [flow][Flow] of [states][State]
     */
    abstract suspend fun initSubscription(states: Flow<STATE>)

    /**
     * Submits an [action][Action] to be executed to the [store][Store]
     *
     * @param action [Action] to run
     */
    fun proceed(action: ACTION) {
        store.proceed(action)
    }

    /**
     * Starts async task for [stateToLaunch]
     * only if there are no tasks currently running with this state
     *
     * @param stateToLaunch [a state][State] that async task starts for
     * @param func a task that should be started
     */
    protected fun executeIfNotExist(stateToLaunch: STATE, func: suspend () -> Unit) {
        if (launchedAsyncStateContinuation?.isActive == true && stateToLaunch == launchedAsyncState) {
            return
        }

        executeAndDisposeExist(stateToLaunch, func)
    }

    /**
     * Starts async task for [stateToLaunch]
     * and disposes previously started task if there is currently running one
     *
     * @param stateToLaunch [a state][State] that async task starts for
     * @param func a task that should be started
     */
    protected fun executeAndDisposeExist(stateToLaunch: STATE, func: suspend () -> Unit) {
        launchedAsyncState = stateToLaunch
        launchedAsyncStateContinuation?.cancel()
        launchedAsyncStateContinuation = taskScope.launch { func() }
    }

    /**
     * Disposes async task
     */
    protected fun dispose() {
        launchedAsyncStateContinuation?.cancel()
        launchedAsyncState = null
    }
}