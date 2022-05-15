package ru.kontur.mobile.visualfsm.testFSM.action

import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.testFSM.TestFSMState

class Stop : TestFSMAction() {

    inner class BtoA : Transition<TestFSMState.B, TestFSMState.A>(TestFSMState.B::class, TestFSMState.A::class) {
        override fun transform(state: TestFSMState.B) = TestFSMState.A
    }

    override fun getTransitions() = listOf(BtoA())
}