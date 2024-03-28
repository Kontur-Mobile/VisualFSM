package ru.kontur.mobile.visualfsm.sealedTests.testFSM.action

import ru.kontur.mobile.visualfsm.Edge
import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.sealedTests.testFSM.TestFSMState

class NavigateToBack : TestFSMAction() {

    @Edge("NavigateBack")
    inner class FromNavigateState : Transition<TestFSMState.Initial, TestFSMState.NavigationState.Screen.Back>() {
        override fun transform(state: TestFSMState.Initial): TestFSMState.NavigationState.Screen.Back {
            return TestFSMState.NavigationState.Screen.Back(count = state.count)
        }
    }
}