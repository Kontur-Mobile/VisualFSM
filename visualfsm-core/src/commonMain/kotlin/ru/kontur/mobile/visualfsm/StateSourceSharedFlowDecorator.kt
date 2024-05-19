package ru.kontur.mobile.visualfsm

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

internal class StateSourceSharedFlowDecorator<STATE : State>(
    private val stateSource: IStateSource<STATE>,
) : IStateSourceWithSharedFlow<STATE> {

    private val sharedFlow = MutableSharedFlow<STATE>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    init {
        sharedFlow.tryEmit(stateSource.getCurrentState())
    }

    override fun observeAllStates(): SharedFlow<STATE> {
        return sharedFlow.asSharedFlow()
    }

    override fun observeState(): StateFlow<STATE> {
        return stateSource.observeState()
    }

    override fun getCurrentState(): STATE {
        return stateSource.getCurrentState()
    }

    override fun updateState(newState: STATE) {
        stateSource.updateState(newState)
        sharedFlow.tryEmit(newState)
    }
}