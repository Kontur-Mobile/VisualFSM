package ru.kontur.mobile.visualfsm.testFSM.action

import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.testFSM.TestFSMState

class Start : TestFSMAction() {

    inner class AtoB : Transition<TestFSMState.A, TestFSMState.B>(TestFSMState.A::class, TestFSMState.B::class) {
        override fun transform(state: TestFSMState.A) = TestFSMState.B
    }

    override fun getTransitions() = listOf(AtoB())
}