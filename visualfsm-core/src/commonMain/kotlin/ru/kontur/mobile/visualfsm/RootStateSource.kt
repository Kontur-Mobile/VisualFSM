package ru.kontur.mobile.visualfsm

import kotlinx.coroutines.flow.*

internal class RootStateSource<STATE : State>(
    initialState: STATE,
) : IStateSource<STATE> {

    private val stateFlow = MutableStateFlow(initialState)

    override fun observeState(): StateFlow<STATE> {
        return stateFlow.asStateFlow()
    }

    override fun getCurrentState(): STATE {
        return stateFlow.value
    }

    override fun updateState(newState: STATE) {
        stateFlow.value = newState
    }
}