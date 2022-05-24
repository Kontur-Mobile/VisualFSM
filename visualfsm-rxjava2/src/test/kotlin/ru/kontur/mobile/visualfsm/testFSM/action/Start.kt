package ru.kontur.mobile.visualfsm.testFSM.action

import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.testFSM.TestFSMState

class Start(val label: String, val milliseconds: Int) : TestFSMAction() {

    inner class Start : Transition<TestFSMState.Initial, TestFSMState.Async>(TestFSMState.Initial::class, TestFSMState.Async::class) {
        override fun transform(state: TestFSMState.Initial) = TestFSMState.Async(label, milliseconds)
    }

    override fun getTransitions() = listOf(Start())
}