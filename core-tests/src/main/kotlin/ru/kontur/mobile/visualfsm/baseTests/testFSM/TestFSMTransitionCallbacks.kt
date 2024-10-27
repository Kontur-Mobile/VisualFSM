package ru.kontur.mobile.visualfsm.baseTests.testFSM

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.transitioncallbacks.DefaultTransitionCallbacks

class TestFSMTransitionCallbacks<STATE : State> : DefaultTransitionCallbacks<STATE> {
    override fun onNoTransitionError(
        action: Action<STATE>,
        currentState: STATE
    ) {
        throw IllegalStateException("onNoTransitionError $action $currentState")
    }

    override fun onMultipleTransitionError(
        action: Action<STATE>,
        currentState: STATE
    ) {
        throw IllegalStateException("onMultipleTransitionError $action $currentState")
    }
}