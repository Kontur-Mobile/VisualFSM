package ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.action

import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.TestFSMWBSState

class Finish(val success: Boolean) : TestFSMAction() {

    inner class Success : Transition<TestFSMWBSState.Async, TestFSMWBSState.Complete>() {
        override fun predicate(state: TestFSMWBSState.Async) = success

        override fun transform(state: TestFSMWBSState.Async): TestFSMWBSState.Complete {
            Thread.sleep(30)
            return TestFSMWBSState.Complete(state.label)
        }
    }

    inner class Error : Transition<TestFSMWBSState.Async, TestFSMWBSState.Error>() {
        override fun predicate(state: TestFSMWBSState.Async) = !success

        override fun transform(state: TestFSMWBSState.Async): TestFSMWBSState.Error {
            Thread.sleep(30)
            return TestFSMWBSState.Error
        }
    }
}