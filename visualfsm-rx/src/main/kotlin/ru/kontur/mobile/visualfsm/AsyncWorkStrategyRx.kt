package ru.kontur.mobile.visualfsm

import io.reactivex.disposables.Disposable

/**
 * Strategy for start and stop of state-based asynchronous tasks
 */
sealed class AsyncWorkStrategyRx {

    /**
     * Ignore next state
     */
    object Ignore : AsyncWorkStrategyRx()

    /**
     * Dispose current task
     */
    object DisposeCurrent : AsyncWorkStrategyRx()

    /**
     * Starts async task for [state]
     * only if there are no tasks currently running with this state
     *
     * @param state [a state][State] that async task starts for
     * @param func the function that subscribes to task rx chain, must return a disposable
     */
    data class ExecuteIfNotExist<STATE : State>(
        val state: STATE,
        val func: () -> Disposable
    ) : AsyncWorkStrategyRx()

    /**
     * Starts async task for [state]
     * and dispose previously started task if there is currently running one
     *
     * @param state [a state][State] that async task starts for
     * @param func the function that subscribes to task rx chain, must return a disposable
     */
    data class ExecuteAndDisposeExist<STATE : State>(
        val state: STATE,
        val func: () -> Disposable
    ) : AsyncWorkStrategyRx()
}
