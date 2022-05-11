package ru.kontur.mobile.visualfsm

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.map

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
    protected open val subscriptionScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private var store: Store<STATE, ACTION>? = null
    private var launchedAsyncState: STATE? = null
    private var subscriptionJob: Job? = null
    private var launchedAsyncStateJob: Job? = null

    /**
     * Binds received [store][Store] to [async worker][AsyncWorker]
     * and starts [observing][Store.observeState] [states][State]
     *
     * @param store provided [Store]
     */
    fun bind(store: Store<STATE, ACTION>) {
        this.store = store
        subscriptionJob = subscriptionScope.launch {
            store.observeState().map {
                onNextState(it)
            }.collect {
                handleAsyncWorkStrategy(it)
            }
        }
    }

    /**
     * Unbind with store, cancel async task and stops observing states
     */
    fun unbind() {
        store = null
        cancel()
        subscriptionJob?.cancel()
    }

    /**
     * Provides a state to manage async work
     *
     * @param state a next [state][State]
     * @return selected [strategy][AsyncWorkStrategy] for async work handling
     */
    abstract fun onNextState(state: STATE): AsyncWorkStrategy

    /**
     * Submits an [action][Action] to be executed to the [store][Store]
     *
     * @param action [Action] to run
     */
    fun proceed(action: ACTION) {
        store?.proceed(action) ?: throw IllegalStateException("Use bind function to binding with Store")
    }

    /**
     * Starts async task for [stateToLaunch]
     * only if there are no tasks currently running with this state
     *
     * @param stateToLaunch [a state][State] that async task starts for
     * @param func a task that should be started
     */
    protected fun executeIfNotExist(stateToLaunch: STATE, func: suspend () -> Unit): AsyncWorkStrategy {
        return AsyncWorkStrategy.ExecuteIfNotExist(stateToLaunch, func)
    }

    /**
     * Starts async task for [stateToLaunch]
     * and cancel previously started task if there is currently running one
     *
     * @param stateToLaunch [a state][State] that async task starts for
     * @param func a task that should be started
     */
    protected fun executeAndCancelExist(stateToLaunch: STATE, func: suspend () -> Unit): AsyncWorkStrategy {
        return AsyncWorkStrategy.ExecuteAndCancelExist(stateToLaunch, func)
    }

    /**
     * Handle new task with selected strategy
     */
    @Suppress("UNCHECKED_CAST")
    private fun handleAsyncWorkStrategy(strategy: AsyncWorkStrategy) {
        when (strategy) {
            AsyncWorkStrategy.Ignore -> Unit
            AsyncWorkStrategy.CancelCurrent -> cancel()
            is AsyncWorkStrategy.ExecuteAndCancelExist<*> -> {
                cancelAndLaunch(strategy.state as STATE, strategy.func)
            }
            is AsyncWorkStrategy.ExecuteIfNotExist<*> -> {
                if (launchedAsyncStateJob?.isActive != true || strategy.state != launchedAsyncState) {
                    cancelAndLaunch(strategy.state as STATE, strategy.func)
                }
            }
        }
    }

    /**
     * Cancel current task and launch new
     */
    private fun cancelAndLaunch(stateToLaunch: STATE, func: suspend () -> Unit) {
        launchedAsyncState = stateToLaunch
        launchedAsyncStateJob?.cancel()
        launchedAsyncStateJob = taskScope.launch { func() }
    }

    /**
     * Cancel current task
     */
    private fun cancel() {
        launchedAsyncStateJob?.cancel()
        launchedAsyncState = null
    }
}