package ru.kontur.mobile.visualfsm.sealedTests.testFSM.action

import ru.kontur.mobile.visualfsm.Edge
import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.sealedTests.testFSM.TestFSMState

class NavigateToNext : TestFSMAction() {

    @Edge("NavigateNext")
    inner class FromInitial : Transition<TestFSMState.Initial, TestFSMState.NavigationState.Screen.Next>() {
        override fun transform(state: TestFSMState.Initial): TestFSMState.NavigationState.Screen.Next {
            return TestFSMState.NavigationState.Screen.Next(count = state.count)
        }
    }
}