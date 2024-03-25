package ru.kontur.mobile.visualfsm.sealedTests.testFSM.action

import ru.kontur.mobile.visualfsm.Edge
import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.sealedTests.testFSM.TestFSMState

class ShowDialog : TestFSMAction() {

    @Edge("ShowDialog")
    inner class FromInitial : Transition<TestFSMState.Initial, TestFSMState.NavigationState.DialogState.Show>() {
        override fun transform(state: TestFSMState.Initial): TestFSMState.NavigationState.DialogState.Show {
            return TestFSMState.NavigationState.DialogState.Show
        }
    }
}