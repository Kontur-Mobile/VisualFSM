package ru.kontur.mobile.visualfsm

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

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
            }.onEach {
                handleTask(it)
            }.catch {
                onStateSubscriptionError(it)
            }.collect()
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
     * @return task - an [AsyncWorkerTask] for async work handling
     */
    abstract fun onNextState(state: STATE): AsyncWorkerTask

    /**
     * Override onStateSubscriptionError if you need handle subscription error
     *
     * @param throwable a [Throwable]
     */
    open fun onStateSubscriptionError(throwable: Throwable) {
        throw throwable
    }

    /**
     * Submits an [action][Action] to be executed to the [store][Store]
     *
     * @param action [Action] to run
     */
    fun proceed(action: ACTION) {
        store?.proceed(action) ?: throw IllegalStateException("Use bind function to binding with Store")
    }

    /**
     * Handle new task
     */
    @Suppress("UNCHECKED_CAST")
    private fun handleTask(task: AsyncWorkerTask) {
        when (task) {
            AsyncWorkerTask.Cancel -> cancel()
            is AsyncWorkerTask.ExecuteAndCancelExist<*> -> {
                cancelAndLaunch(task.state as STATE, task.func)
            }
            is AsyncWorkerTask.ExecuteIfNotExist<*> -> {
                if (launchedAsyncStateJob?.isActive != true || task.state != launchedAsyncState) {
                    cancelAndLaunch(task.state as STATE, task.func)
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