package ru.kontur.mobile.visualfsm.testFSM

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.TransitionCallbacks

class TestFSMTransitionCallbacks: TransitionCallbacks<TestFSMState> {
    override fun onActionLaunched(action: Action<TestFSMState>, currentState: TestFSMState) {
    }

    override fun onTransitionSelected(
        action: Action<TestFSMState>,
        transition: Transition<TestFSMState, TestFSMState>,
        currentState: TestFSMState
    ) {
    }

    override fun onNewStateReduced(
        action: Action<TestFSMState>,
        transition: Transition<TestFSMState, TestFSMState>,
        oldState: TestFSMState,
        newState: TestFSMState
    ) {
    }

    override fun onNoTransitionError(action: Action<TestFSMState>, currentState: TestFSMState) {
    }

    override fun onMultipleTransitionError(action: Action<TestFSMState>, currentState: TestFSMState) {
    }
}