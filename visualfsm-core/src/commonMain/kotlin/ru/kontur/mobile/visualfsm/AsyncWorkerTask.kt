package ru.kontur.mobile.visualfsm


/**
 * Task for [AsyncWorker] for manage state-based asynchronous work
 */
sealed class AsyncWorkerTask {

    /**
     * Cancel current task
     */
    object Cancel : AsyncWorkerTask()

    /**
     * Starts async work for [state]
     * only if there are no tasks currently running with this state
     *
     * @param state [a state][State] that async task starts for
     * @param func a suspend function that should be executed
     */
    data class ExecuteIfNotExist<STATE : State>(
        val state: STATE,
        val func: suspend () -> Unit
    ) : AsyncWorkerTask()

    /**
     * Starts async work for [state]
     * and cancels previously started task if there is currently running one
     *
     * @param state [a state][State] that async task starts for
     * @param func a suspend function that should be executed
     */
    data class ExecuteAndCancelExist<STATE : State>(
        val state: STATE,
        val func: suspend () -> Unit
    ) : AsyncWorkerTask()
}
