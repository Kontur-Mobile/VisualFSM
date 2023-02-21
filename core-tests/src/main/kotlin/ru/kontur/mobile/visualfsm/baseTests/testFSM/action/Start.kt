package ru.kontur.mobile.visualfsm.baseTests.testFSM.action

import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.baseTests.testFSM.TestFSMState

class Start(val label: String, val milliseconds: Int, val salt: String = "") : TestFSMAction() {
    inner class Start : Transition<TestFSMState.Initial, TestFSMState.Async>() {
        override fun transform(state: TestFSMState.Initial): TestFSMState.Async {
            Thread.sleep(10)
            return TestFSMState.Async(label, milliseconds, salt)
        }
    }

    inner class StartOther : Transition<TestFSMState.Async, TestFSMState.Async>() {
        override fun transform(state: TestFSMState.Async): TestFSMState.Async {
            Thread.sleep(10)
            return TestFSMState.Async(label, milliseconds, salt)
        }
    }
}