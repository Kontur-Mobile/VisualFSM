package ru.kontur.mobile.visualfsm

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 * Manages the start and stop of state-based asynchronous tasks
 */
abstract class AsyncWorkerRx<STATE : State, ACTION : Action<STATE>> {
    private lateinit var store: StoreRx<STATE, ACTION>
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
        subscriptionDisposable = initSubscription(store.observeState())
    }

    /**
     * Disposes async task and stops observing states
     */
    @Synchronized
    fun unbind() {
        dispose()
        subscriptionDisposable?.dispose()
    }

    /**
     * Provides a state flow to manage async work based on state changes
     *
     * @param states a [flow][Flow] of [states][State]
     */
    abstract fun initSubscription(states: Observable<STATE>): Disposable

    /**
     * Submits an [action][Action] to be executed in the [StoreRx]
     *
     * @param action launched [Action]
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
    @Synchronized
    protected fun executeIfNotExist(stateToLaunch: STATE, func: () -> Disposable) {
        if (launchedAsyncStateDisposable?.isDisposed == false && stateToLaunch == launchedAsyncState) {
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
    @Synchronized
    protected fun executeAndDisposeExist(stateToLaunch: STATE, func: () -> Disposable) {
        launchedAsyncState = stateToLaunch
        launchedAsyncStateDisposable?.dispose()
        launchedAsyncStateDisposable = func()
    }

    /**
     * Disposes async task
     */
    @Synchronized
    protected fun dispose() {
        launchedAsyncState = null
        launchedAsyncStateDisposable?.dispose()
    }
}