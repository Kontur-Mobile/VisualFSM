package ru.kontur.mobile.visualfsm.sealedTests.testFSM.action

import ru.kontur.mobile.visualfsm.Edge
import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.sealedTests.testFSM.TestFSMState

class NavigateCompleted : TestFSMAction() {

    @Edge("NavigateCompleted")
    inner class FromNavigateStateBack : Transition<TestFSMState.NavigationState, TestFSMState.Initial>() {
        override fun transform(state: TestFSMState.NavigationState): TestFSMState.Initial {
            return TestFSMState.Initial
        }
    }
}