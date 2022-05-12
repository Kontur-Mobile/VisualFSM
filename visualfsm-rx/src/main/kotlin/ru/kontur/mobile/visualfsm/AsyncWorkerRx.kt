package ru.kontur.mobile.visualfsm

import io.reactivex.disposables.Disposable

/**
 * Manages the start and stop of state-based asynchronous tasks
 */
abstract class AsyncWorkerRx<STATE : State, ACTION : Action<STATE>> {
    private var store: StoreRx<STATE, ACTION>? = null
    private var launchedAsyncState: STATE? = null
    private var subscriptionDisposable: Disposable? = null
    private var launchedAsyncStateDisposable: Disposable? = null

    /**
     * Binds received [store][StoreRx] to [async worker][AsyncWorkerRx]
     * and starts [observing][StoreRx.observeState] [states][State]
     *
     * @param store provided [StoreRx]
     */
    @Synchronized
    fun bind(store: StoreRx<STATE, ACTION>) {
        this.store = store
        subscriptionDisposable = store.observeState()
            .map(::onNextState)
            .subscribe(::handleTask, ::onStateSubscriptionError)
    }

    /**
     * Unbind from store, dispose async task and stops observing states
     */
    @Synchronized
    fun unbind() {
        store = null
        dispose()
        subscriptionDisposable?.dispose()
    }

    /**
     * Provides a state to manage async work
     *
     * @param state a next [state][State]
     * @return [AsyncWorkerTask] for async work handling
     */
    abstract fun onNextState(state: STATE): AsyncWorkerTaskRx

    /**
     * Called when catched subscription error
     * Override this for logs or metrics
     * Call of this method signals the presence of unhandled exceptions in the [onNextState] method.
     * @param throwable catched [Throwable]
     */
    open fun onStateSubscriptionError(throwable: Throwable) {
        throw throwable
    }

    /**
     * Submits an [action][Action] to be executed in the [StoreRx]
     *
     * @param action launched [Action]
     */
    fun proceed(action: ACTION) {
        store?.proceed(action) ?: throw IllegalStateException("Use bind function to binding to Store")
    }

    /**
     * Handle new task
     */
    @Suppress("UNCHECKED_CAST")
    private fun handleTask(task: AsyncWorkerTaskRx) {
        when (task) {
            AsyncWorkerTaskRx.Cancel -> dispose()
            is AsyncWorkerTaskRx.ExecuteAndCancelExist<*> -> {
                disposeAndLaunch(task.state as STATE, task.func)
            }
            is AsyncWorkerTaskRx.ExecuteIfNotExist<*> -> {
                if (launchedAsyncStateDisposable?.isDisposed != false || task.state != launchedAsyncState) {
                    disposeAndLaunch(task.state as STATE, task.func)
                }
            }
        }
    }

    /**
     * Dispose current task and launch new
     */
    @Synchronized
    private fun disposeAndLaunch(stateToLaunch: STATE, func: () -> Disposable) {
        launchedAsyncState = stateToLaunch
        launchedAsyncStateDisposable?.dispose()
        launchedAsyncStateDisposable = func()
    }

    /**
     * Disposes async task
     */
    @Synchronized
    private fun dispose() {
        launchedAsyncStateDisposable?.dispose()
        launchedAsyncState = null
    }
}