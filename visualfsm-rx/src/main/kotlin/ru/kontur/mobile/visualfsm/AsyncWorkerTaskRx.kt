package ru.kontur.mobile.visualfsm

import io.reactivex.disposables.Disposable

/**
 * Task for [AsyncWorkerRx] for manage state-based asynchronous work
 */
sealed class AsyncWorkerTaskRx<STATE : State> {

    /**
     * Dispose current task
     */
    class Cancel<STATE : State> : AsyncWorkerTaskRx<STATE>()

    /**
     * Starts async work for [state]
     * only if there are no tasks currently running with this state
     *
     * @param state [a state][State] that async task starts for
     * @param func the function that subscribes to task rx chain, must return a disposable
     */
    data class ExecuteIfNotExist<STATE : State>(
        val state: STATE,
        val func: () -> Disposable
    ) : AsyncWorkerTaskRx<STATE>()

    /**
     * Starts async work for [state]
     * and dispose previously started task if there is currently running one
     *
     * @param state [a state][State] that async task starts for
     * @param func the function that subscribes to task rx chain, must return a disposable
     */
    data class ExecuteAndCancelExist<STATE : State>(
        val state: STATE,
        val func: () -> Disposable
    ) : AsyncWorkerTaskRx<STATE>()
}
