package ru.kontur.mobile.visualfsm

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

abstract class AsyncWorkerRx<STATE : State, ACTION : Action<STATE>> {
    private lateinit var store: StoreRx<STATE, ACTION>
    private var launchedAsyncState: STATE? = null
    private var subscriptionDisposable: Disposable? = null
    private var launchedAsyncStateDisposable: Disposable? = null

    @Synchronized
    fun bind(store: StoreRx<STATE, ACTION>) {
        this.store = store
        subscriptionDisposable = initSubscription(store.observeState())
    }

    @Synchronized
    fun unbind() {
        dispose()
        subscriptionDisposable?.dispose()
    }

    abstract fun initSubscription(states: Observable<STATE>): Disposable

    fun proceed(action: ACTION) {
        store.proceed(action)
    }

    /**
     * Запускает асинхронную операцию [stateToLaunch], если нет активной с эквивалентным state.
     * Приоритет выполняющейся операции с эквивалентным state.
     */
    @Synchronized
    protected fun executeIfNotExist(stateToLaunch: STATE, func: () -> Disposable) {
        if (launchedAsyncStateDisposable?.isDisposed == false && stateToLaunch == launchedAsyncState) {
            return
        }

        executeAndDisposeExist(stateToLaunch, func)
    }

    /**
     * Запускает асинхронную операцию [stateToLaunch], если есть активная операция - отписываемся от результата старой операции.
     * Приоритет новой операции.
     */
    @Synchronized
    protected fun executeAndDisposeExist(stateToLaunch: STATE, func: () -> Disposable) {
        launchedAsyncState = stateToLaunch
        launchedAsyncStateDisposable?.dispose()
        launchedAsyncStateDisposable = func()
    }

    /**
     * Отписываемся от результата асинхронной операции
     */
    @Synchronized
    protected fun dispose() {
        launchedAsyncState = null
        launchedAsyncStateDisposable?.dispose()
    }
}