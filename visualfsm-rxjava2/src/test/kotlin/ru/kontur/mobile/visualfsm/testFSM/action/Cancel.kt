package ru.kontur.mobile.visualfsm.testFSM.action

import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.testFSM.TestFSMState

class Cancel : TestFSMAction() {

    inner class Cancel : Transition<TestFSMState.Async, TestFSMState.Initial>() {
        override fun transform(state: TestFSMState.Async) = TestFSMState.Initial
    }
}