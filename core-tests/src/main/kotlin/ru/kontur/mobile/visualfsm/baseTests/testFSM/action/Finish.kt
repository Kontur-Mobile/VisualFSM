package ru.kontur.mobile.visualfsm.baseTests.testFSM.action

import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.baseTests.testFSM.TestFSMState

class Finish(val success: Boolean) : TestFSMAction() {
    inner class Success : Transition<TestFSMState.Async, TestFSMState.Complete>() {
        override fun predicate(state: TestFSMState.Async) = success

        override fun transform(state: TestFSMState.Async): TestFSMState.Complete {
            Thread.sleep(10)
            return TestFSMState.Complete(state.label)
        }
    }

    inner class Error : Transition<TestFSMState.Async, TestFSMState.Error>() {
        override fun predicate(state: TestFSMState.Async) = !success

        override fun transform(state: TestFSMState.Async): TestFSMState.Error {
            Thread.sleep(10)
            return TestFSMState.Error
        }
    }
}