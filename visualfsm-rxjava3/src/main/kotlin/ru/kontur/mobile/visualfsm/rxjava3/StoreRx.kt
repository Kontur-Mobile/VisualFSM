package ru.kontur.mobile.visualfsm.rxjava3

import io.reactivex.rxjava3.core.Observable
import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.TransitionCallbacks

/**
 * Stores current [state][State] and provides subscription on [state][State] updates.
 * It is the core of the state machine, takes an [action][Action] as input and returns [states][State] as output
 *
 * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic on provided event calls (like logging, debugging, or metrics) (optional)
 */
internal class StoreRx<STATE : State, ACTION : Action<STATE>>(
    private val stateKeeper: IStateKeeperRx<STATE>,
    private val transitionCallbacks: TransitionCallbacks<STATE>?,
) {

    /**
     * Provides a [observable][Observable] of [states][State]
     *
     * @return a [observable][Observable] of [states][State]
     */
    internal fun observeState(): Observable<STATE> {
        return stateKeeper.observeState()
    }

    /**
     * Returns current state
     *
     * @return current [state][State]
     */
    internal fun getCurrentState(): STATE {
        return stateKeeper.getCurrentState()
    }

    /**
     * Changes current state
     *
     * @param action [Action] that was launched
     */
    internal fun proceed(action: ACTION) {
        val currentState = getCurrentState()
        val newState = reduce(action, currentState)
        stateKeeper.updateState(newState)
    }

    /**
     * Runs [action's][Action] transition of [states][State]
     *
     * @param action launched [action][Action]
     * @param state new [state][State]
     * @return new [state][State]
     */
    private fun reduce(
        action: ACTION,
        state: STATE
    ): STATE {
        return action.run(state, transitionCallbacks)
    }
}