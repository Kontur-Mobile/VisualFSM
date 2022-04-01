package ru.kontur.mobile.visualfsm

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow

abstract class AsyncWorker<STATE : State, ACTION : Action<STATE>> {
    private val mainScope = CoroutineScope(Dispatchers.Default)
    private lateinit var store: Store<STATE, ACTION>
    private var launchedAsyncState: STATE? = null
    private var subscriptionContinuation: Job? = null
    private var launchedAsyncStateContinuation: Job? = null

    fun bind(store: Store<STATE, ACTION>) {
        this.store = store
        subscriptionContinuation = initSubscription(store.observeState())
    }

    fun unbind() {
        dispose()
        subscriptionContinuation?.cancel()
    }

    abstract fun initSubscription(states: Flow<STATE>): Job

    fun proceed(action: ACTION) {
        store.proceed(action)
    }

    /**
     * Запускает асинхронную операцию [stateToLaunch], если нет активной с эквивалентным state.
     * Приоритет выполняющейся операции с эквивалентным state.
     */
    protected fun executeIfNotExist(stateToLaunch: STATE, func: suspend () -> Unit) {
        if (launchedAsyncStateContinuation?.isActive == true && stateToLaunch == launchedAsyncState) {
            return
        }

        executeAndDisposeExist(stateToLaunch, func)
    }

    /**
     * Запускает асинхронную операцию [stateToLaunch], если есть активная операция - отписываемся от результата старой операции.
     * Приоритет новой операции.
     */
    protected fun executeAndDisposeExist(stateToLaunch: STATE, func: suspend () -> Unit) {
        launchedAsyncState = stateToLaunch
        launchedAsyncStateContinuation?.cancel()
        launchedAsyncStateContinuation = mainScope.launch { func() }
    }

    /**
     * Отписываемся от результата асинхронной операции
     */
    protected fun dispose() {
        launchedAsyncState = null
        launchedAsyncStateContinuation?.cancel()
    }
}