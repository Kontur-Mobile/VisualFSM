package ru.kontur.mobile.visualfsm

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

/**
 * Stores current [state][State] and provides subscription on [state][State] updates.
 * It is the core of the state machine, takes an [action][Action] as input and returns [states][State] as output
 *
 * @param initialState initial [state][State]
 * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic on provided event calls (like logging, debugging, or metrics) (optional)
 */
internal class Store<STATE : State, ACTION : Action<STATE>>(
    initialState: STATE, private val transitionCallbacks: TransitionCallbacks<STATE>?
) {

    private val sharedFlow = MutableSharedFlow<STATE>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val stateFlow = MutableStateFlow(initialState)

    init {
        sharedFlow.tryEmit(initialState)
    }

    /**
     * Provides a [flow][StateFlow] of [states][State]
     *
     * @return a [flow][StateFlow] of [states][State]
     */
    internal fun observeState(): StateFlow<STATE> {
        return stateFlow.asStateFlow()
    }

    /**
     * Provides a [flow][SharedFlow] of [states][State]
     *
     * @return a [flow][SharedFlow] of [states][State]
     */
    internal fun observeAllStates(): SharedFlow<STATE> {
        return sharedFlow.asSharedFlow()
    }

    /**
     * Returns current state
     *
     * @return current [state][State]
     */
    internal fun getCurrentState(): STATE {
        return stateFlow.value
    }

    /**
     * Changes current state
     *
     * @param action [Action] that was launched
     */
    internal fun proceed(action: ACTION) {
        val newState = reduce(action, getCurrentState())
        stateFlow.value = newState
        sharedFlow.tryEmit(newState)
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