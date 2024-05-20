package ru.kontur.mobile.visualfsm

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

internal interface IStateSourceWithSharedFlow<STATE : State> : IStateSource<STATE> {

    /**
     * Provides a [flow][StateFlow] of [states][State]
     *
     * @return a [flow][StateFlow] of [states][State]
     */
    fun observeAllStates(): SharedFlow<STATE>
}