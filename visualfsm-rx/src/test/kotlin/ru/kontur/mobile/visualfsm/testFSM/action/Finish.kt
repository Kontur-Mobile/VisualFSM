package ru.kontur.mobile.visualfsm.testFSM.action

import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.testFSM.TestFSMState

class Finish(val result: Boolean) : TestFSMAction() {

    inner class BtoC : Transition<TestFSMState.B, TestFSMState.C>(TestFSMState.B::class, TestFSMState.C::class) {
        override fun predicate(state: TestFSMState.B) = result

        override fun transform(state: TestFSMState.B) = TestFSMState.C
    }

    inner class BtoD : Transition<TestFSMState.B, TestFSMState.D>(TestFSMState.B::class, TestFSMState.D::class) {
        override fun predicate(state: TestFSMState.B) = !result

        override fun transform(state: TestFSMState.B) = TestFSMState.D
    }

    override fun getTransitions() = listOf(BtoC(), BtoD())
}