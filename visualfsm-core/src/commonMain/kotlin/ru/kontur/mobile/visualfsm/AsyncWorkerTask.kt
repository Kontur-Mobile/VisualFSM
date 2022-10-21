package ru.kontur.mobile.visualfsm


/**
 * Task for [AsyncWorker] for manage state-based asynchronous work
 */
sealed class AsyncWorkerTask<STATE : State> {

    /**
     * Cancel current task
     */
    class Cancel<STATE : State> : AsyncWorkerTask<STATE>()

    /**
     * Starts async work for [state]
     * or do not interrupt if there are currently running task with equals state
     *
     * @param state [a state][State] that async task starts for
     * @param func a suspend function that should be executed
     */
    data class ExecuteIfNotExist<STATE : State>(
        val state: STATE,
        val func: suspend ExecuteIfNotExist<STATE>.() -> Unit,
    ) : AsyncWorkerTask<STATE>()

    /**
     * Starts async work for [state]
     * and cancels previously started task if there is currently running one
     *
     * @param state [a state][State] that async task starts for
     * @param func a suspend function that should be executed
     */
    data class ExecuteAndCancelExist<STATE : State>(
        val state: STATE,
        val func: suspend ExecuteAndCancelExist<STATE>.() -> Unit,
    ) : AsyncWorkerTask<STATE>()

    /**
     * Starts async work for [state]
     * or do not interrupt if there are currently running task with same state class
     * Used for tasks that deliver the result in several stages
     *
     * @param state [a state][State] that async task starts for
     * @param func a suspend function that should be executed
     */
    data class ExecuteIfNotExistWithSameClass<STATE : State>(
        val state: STATE,
        val func: suspend ExecuteIfNotExistWithSameClass<STATE>.() -> Unit,
    ) : AsyncWorkerTask<STATE>()
}
