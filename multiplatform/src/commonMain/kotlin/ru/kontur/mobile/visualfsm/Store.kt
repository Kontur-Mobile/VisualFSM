package ru.kontur.mobile.visualfsm

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.single


abstract class Store<STATE : State, ACTION : Action<STATE>>(
    initialState: STATE, private val transitionCallbacks: TransitionCallbacks<STATE>
) {
    private val stateFlow = MutableStateFlow(initialState)

    internal fun observeState(): Flow<STATE> {
        return stateFlow.asStateFlow()
    }

    internal suspend fun getStateSingle(): STATE {
        return stateFlow.single()
    }

    internal fun proceed(action: ACTION) {
        val currentState = stateFlow.value
        val newState = reduce(action, currentState)
        val changed = newState != currentState
        if (changed) {
            stateFlow.tryEmit(newState)
        }
    }

    private fun reduce(
        action: ACTION, state: STATE
    ): STATE {
        return action.run(state, transitionCallbacks)
    }
}