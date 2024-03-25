package ru.kontur.mobile.visualfsm.sealedTests.testFSM.action

import ru.kontur.mobile.visualfsm.Edge
import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.sealedTests.testFSM.TestFSMState

class HideDialog : TestFSMAction() {

    @Edge("HideDialog")
    inner class FromShow : Transition<TestFSMState.NavigationState.DialogState.Show, TestFSMState.NavigationState.DialogState.Hide>() {
        override fun transform(state: TestFSMState.NavigationState.DialogState.Show): TestFSMState.NavigationState.DialogState.Hide {
            return TestFSMState.NavigationState.DialogState.Hide
        }
    }
}