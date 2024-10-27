package ru.kontur.mobile.visualfsm.transitioncallbacks

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.TransitionCallbacks

class LogTransitionCallbacks<STATE : State>(
    private val logger: FSMLogger
) : TransitionCallbacks<STATE> {
    override fun onInitialStateReceived(initialState: STATE) {
        logger.i("")
    }

    override fun onActionLaunched(
        action: Action<STATE>,
        currentState: STATE
    ) {
        logger.i("")
    }

    override fun onTransitionSelected(
        action: Action<STATE>,
        transition: Transition<STATE, STATE>,
        currentState: STATE
    ) {
        logger.i("")
    }

    override fun onNewStateReduced(
        action: Action<STATE>,
        transition: Transition<STATE, STATE>,
        oldState: STATE,
        newState: STATE
    ) {
        logger.i("")
    }

    override fun onNoTransitionError(
        action: Action<STATE>,
        currentState: STATE,
    ) {
        logger.i("")
    }

    override fun onMultipleTransitionError(
        action: Action<STATE>,
        currentState: STATE,
    ) {
        logger.i("")
    }
}
