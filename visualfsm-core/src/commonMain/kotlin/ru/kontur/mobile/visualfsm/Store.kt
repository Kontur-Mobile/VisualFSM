package ru.kontur.mobile.visualfsm

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.single

/**
 * Stores current [state][State] and provides subscription on [state][State] updates.
 * It is the core of the state machine, takes an [action][Action] as input and returns [states][State] as output
 *
 * @param initialState initial [state][State]
 * @param transitionCallbacks the [callbacks][TransitionCallbacks] that used on some [Action] and [Transition] actions
 */
abstract class Store<STATE : State, ACTION : Action<STATE>>(
    initialState: STATE, private val transitionCallbacks: TransitionCallbacks<STATE>
) {

    private val stateFlow = MutableStateFlow(initialState)

    /**
     * Provides a [flow][Flow] of [states][State]
     *
     * @return a [flow][Flow] of [states][State]
     */
    internal fun observeState(): Flow<STATE> {
        return stateFlow.asStateFlow()
    }

    /**
     * Returns current state
     *
     * @return current [state][State]
     */
    internal suspend fun getStateSingle(): STATE {
        return stateFlow.single()
    }

    /**
     * Changes current state
     *
     * @param action [Action] that was launched
     */
    internal fun proceed(action: ACTION) {
        val currentState = stateFlow.value
        val newState = reduce(action, currentState)
        val changed = newState != currentState
        if (changed) {
            stateFlow.tryEmit(newState)
        }
    }

    /**
     * Runs [action's][Action] transition of [states][State]
     *
     * @param action launched [action][Action]
     * @param state new [state][State]
     * @return new [state][State]
     */
    private fun reduce(
        action: ACTION, state: STATE
    ): STATE {
        return action.run(state, transitionCallbacks)
    }
}