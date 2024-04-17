package ru.kontur.mobile.visualfsm.sealedTests.testFSM.action

import ru.kontur.mobile.visualfsm.Edge
import ru.kontur.mobile.visualfsm.SelfTransition
import ru.kontur.mobile.visualfsm.sealedTests.testFSM.TestFSMState

class ObserveChange(
    private val count: Int,
) : TestFSMAction() {

    @Edge("ObserveChange")
    inner class FromInitial : SelfTransition<TestFSMState.Initial>() {
        override fun transform(state: TestFSMState.Initial): TestFSMState.Initial {
            return state.copy(count = count)
        }
    }

    @Edge("ObserveChange")
    inner class FromDialogState : SelfTransition<TestFSMState.NavigationState.DialogState>() {
        override fun transform(state: TestFSMState.NavigationState.DialogState): TestFSMState.NavigationState.DialogState {
            return state.copySealed(count = count)
        }
    }
}