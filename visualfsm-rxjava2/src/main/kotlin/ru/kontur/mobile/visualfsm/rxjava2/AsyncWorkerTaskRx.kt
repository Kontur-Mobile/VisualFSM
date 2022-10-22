package ru.kontur.mobile.visualfsm.rxjava2

import io.reactivex.disposables.Disposable
import ru.kontur.mobile.visualfsm.State

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
     * or do not interrupt if there are currently running task with equals state
     *
     * @param state [a state][State] that async task starts for
     * @param func the function that subscribes to task rx chain, must return a disposable
     */
    data class ExecuteIfNotExist<STATE : State>(
        val state: STATE,
        val func: ExecuteIfNotExist<STATE>.() -> Disposable,
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
        val func: ExecuteAndCancelExist<STATE>.() -> Disposable,
    ) : AsyncWorkerTaskRx<STATE>()

    /**
     * Starts async work for [state]
     * or do not interrupt if there are currently running task with same state class
     * Used for tasks that deliver the result in several stages
     *
     * @param state [a state][State] that async task starts for
     * @param func the function that subscribes to task rx chain, must return a disposable
     */
    data class ExecuteIfNotExistWithSameClass<STATE : State>(
        val state: STATE,
        val func: ExecuteIfNotExistWithSameClass<STATE>.() -> Disposable,
    ) : AsyncWorkerTaskRx<STATE>()
}
