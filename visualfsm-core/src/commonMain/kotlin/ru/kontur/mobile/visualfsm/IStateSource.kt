package ru.kontur.mobile.visualfsm

import kotlinx.coroutines.flow.StateFlow

interface IStateSource<STATE : State> {

    /**
     * Provides a [flow][StateFlow] of [states][State]
     *
     * @return a [flow][StateFlow] of [states][State]
     */
    fun observeState(): StateFlow<STATE>

    /**
     * Returns current state
     *
     * @return current [state][State]
     */
    fun getCurrentState(): STATE

    /**
     * Update current state
     *
     * @param newState [State] for update
     */
    fun updateState(newState: STATE)
}