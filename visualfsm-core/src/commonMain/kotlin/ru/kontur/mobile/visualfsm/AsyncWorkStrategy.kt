package ru.kontur.mobile.visualfsm


/**
 * Strategy for start and stop of state-based asynchronous tasks
 */
sealed class AsyncWorkStrategy {

    /**
     * Ignore next state
     */
    object Ignore : AsyncWorkStrategy()

    /**
     * Cancel current task
     */
    object CancelCurrent : AsyncWorkStrategy()

    /**
     * Starts async task for [stateToLaunch]
     * only if there are no tasks currently running with this state
     *
     * @param stateToLaunch [a state][State] that async task starts for
     * @param func a suspend function that should be executed
     */
    data class ExecuteIfNotExist<STATE : State>(
        val state: STATE,
        val func: suspend () -> Unit
    ) : AsyncWorkStrategy()

    /**
     * Starts async task for [stateToLaunch]
     * and cancels previously started task if there is currently running one
     *
     * @param stateToLaunch [a state][State] that async task starts for
     * @param func a suspend function that should be executed
     */
    data class ExecuteAndCancelExist<STATE : State>(
        val state: STATE,
        val func: suspend () -> Unit
    ) : AsyncWorkStrategy()
}
