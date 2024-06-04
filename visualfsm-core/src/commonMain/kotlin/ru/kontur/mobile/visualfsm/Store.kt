package ru.kontur.mobile.visualfsm

import kotlinx.coroutines.flow.*

/**
 * Stores current [state][State] and provides subscription on [state][State] updates.
 * It is the core of the state machine, takes an [action][Action] as input and returns [states][State] as output
 *
 * @param stateSource the [state source][IStateSource] for storing and subscribing to state,
 * can be external to implement a common state tree between parent and child state machines
 * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic
 * on provided event calls (like logging, debugging, or metrics) (optional)
 */
internal class Store<STATE : State, ACTION : Action<STATE>>(
    private val stateSource: IStateSourceWithSharedFlow<STATE>,
    private val transitionCallbacks: TransitionCallbacks<STATE>?
) {

    /**
     * Provides a [flow][StateFlow] of [states][State]
     *
     * @return a [flow][StateFlow] of [states][State]
     */
    internal fun observeState(): StateFlow<STATE> {
        return stateSource.observeState()
    }

    /**
     * Provides a [flow][SharedFlow] of [states][State]
     *
     * @return a [flow][SharedFlow] of [states][State]
     */
    internal fun observeAllStates(): SharedFlow<STATE> {
        return stateSource.observeAllStates()
    }

    /**
     * Returns current state
     *
     * @return current [state][State]
     */
    internal fun getCurrentState(): STATE {
        return stateSource.getCurrentState()
    }

    /**
     * Changes current state
     *
     * @param action [Action] that was launched
     */
    internal fun proceed(action: ACTION) {
        val newState = reduce(action, getCurrentState())
        stateSource.updateState(newState)
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