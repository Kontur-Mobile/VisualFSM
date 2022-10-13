package ru.kontur.mobile.visualfsm

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Stores current [state][State] and provides subscription on [state][State] updates.
 * It is the core of the state machine, takes an [action][Action] as input and returns [states][State] as output
 *
 * @param initialState initial [state][State]
 * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic on provided event calls (like logging, debugging, or metrics) (optional)
 * @param stateDependencyManager state dependency manager [StateDependencyManager]
 * @param backStateStack back state stack [BackStateStack]
 */
class Store<STATE : State, ACTION : Action<STATE>>(
    initialState: STATE,
    transitionCallbacks: TransitionCallbacks<STATE>?,
    stateDependencyManager: StateDependencyManager<STATE>?,
    backStateStack: BackStateStack<STATE>,
) : BaseStore<STATE, ACTION>(initialState, transitionCallbacks, stateDependencyManager, backStateStack) {

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
    override fun getCurrentState(): STATE {
        return stateFlow.value
    }

    /**
     * Set state
     *
     * @param newState new [state][State]
     */
    override fun setState(id: Int, newState: STATE) {
        if (newState != stateFlow.value) {
            currentStateId = id
            stateFlow.value = newState
        }
    }
}