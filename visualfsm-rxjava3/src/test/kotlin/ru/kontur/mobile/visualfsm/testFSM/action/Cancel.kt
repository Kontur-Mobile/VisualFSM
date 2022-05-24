package ru.kontur.mobile.visualfsm.testFSM.action

import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.testFSM.TestFSMState

class Cancel : TestFSMAction() {

    inner class Cancel : Transition<TestFSMState.Async, TestFSMState.Initial>(TestFSMState.Async::class, TestFSMState.Initial::class) {
        override fun transform(state: TestFSMState.Async) = TestFSMState.Initial
    }

    override fun getTransitions() = listOf(Cancel())
}