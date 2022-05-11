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
            .subscribe(::handleAsyncWorkStrategy, ::onStateSubscriptionError)
    }

    /**
     * Unbind with store, dispose async task and stops observing states
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
     * @return selected [strategy][AsyncWorkStrategy] for async work handling
     */
    abstract fun onNextState(state: STATE): AsyncWorkStrategyRx

    /**
     * Override onStateSubscriptionError if you need handle subscription error
     *
     * @param throwable a [Throwable]
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
        store?.proceed(action) ?: throw IllegalStateException("Use bind function to binding with Store")
    }

    /**
     * Construct AsyncWorkStrategy.ExecuteIfNotExist [strategy][AsyncWorkStrategy]
     *
     * @param state [a state][State] that async task starts for
     * @param func the function that subscribes to task rx chain, must return a disposable
     */
    protected fun executeIfNotExist(state: STATE, func: () -> Disposable): AsyncWorkStrategyRx {
        return AsyncWorkStrategyRx.ExecuteIfNotExist(state, func)
    }

    /**
     * Construct AsyncWorkStrategy.ExecuteAndDisposeExist [strategy][AsyncWorkStrategy]
     *
     * @param state [a state][State] that async task starts for
     * @param func the function that subscribes to task rx chain, must return a disposable
     */
    protected fun executeAndDisposeExist(state: STATE, func: () -> Disposable): AsyncWorkStrategyRx {
        return AsyncWorkStrategyRx.ExecuteAndDisposeExist(state, func)
    }

    /**
     * Handle new task with selected strategy
     */
    @Suppress("UNCHECKED_CAST")
    private fun handleAsyncWorkStrategy(strategy: AsyncWorkStrategyRx) {
        when (strategy) {
            AsyncWorkStrategyRx.Ignore -> Unit
            AsyncWorkStrategyRx.DisposeCurrent -> dispose()
            is AsyncWorkStrategyRx.ExecuteAndDisposeExist<*> -> {
                disposeAndLaunch(strategy.state as STATE, strategy.func)
            }
            is AsyncWorkStrategyRx.ExecuteIfNotExist<*> -> {
                if (launchedAsyncStateDisposable?.isDisposed != true || strategy.state == launchedAsyncState) {
                    disposeAndLaunch(strategy.state as STATE, strategy.func)
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