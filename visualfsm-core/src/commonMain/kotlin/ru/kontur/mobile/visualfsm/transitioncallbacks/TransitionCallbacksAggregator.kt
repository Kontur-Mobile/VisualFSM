package ru.kontur.mobile.visualfsm.transitioncallbacks

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.TransitionCallbacks


class TransitionCallbacksAggregator<STATE : State, ACTION : Action<STATE>>(
    private val callbacksList: List<TransitionCallbacks<STATE, ACTION>>
) : TransitionCallbacks<STATE, ACTION> {
    override fun onInitialStateReceived(initialState: STATE) {
        callbacksList.forEach {
            it.onInitialStateReceived(initialState)
        }
    }

    override fun onActionLaunched(
        action: ACTION,
        currentState: STATE
    ) {
        callbacksList.forEach {
            it.onActionLaunched(action, currentState)
        }
    }

    override fun onTransitionSelected(
        action: ACTION,
        transition: Transition<STATE, STATE>,
        currentState: STATE
    ) {
        callbacksList.forEach {
            it.onTransitionSelected(action, transition, currentState)
        }
    }

    override fun onNewStateReduced(
        action: ACTION,
        transition: Transition<STATE, STATE>,
        oldState: STATE,
        newState: STATE
    ) {
        callbacksList.forEach {
            it.onNewStateReduced(
                action = action,
                transition = transition,
                oldState = oldState,
                newState = newState
            )
        }
    }

    override fun onNoTransitionError(
        action: ACTION,
        currentState: STATE,
    ) {
        callbacksList.forEach {
            it.onNoTransitionError(action, currentState)
        }
    }

    override fun onMultipleTransitionError(
        action: ACTION,
        currentState: STATE,
        suitableTransitions: List<Transition<STATE, STATE>>
    ) {
        callbacksList.forEach {
            it.onMultipleTransitionError(action, currentState, suitableTransitions)
        }
    }
}