package ru.kontur.mobile.visualfsm.testFSM.action

import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.testFSM.TestFSMState

class Finish(val success: Boolean) : TestFSMAction() {

    inner class Success : Transition<TestFSMState.Async, TestFSMState.Complete>(TestFSMState.Async::class, TestFSMState.Complete::class) {
        override fun predicate(state: TestFSMState.Async) = success

        override fun transform(state: TestFSMState.Async) = TestFSMState.Complete(state.label)
    }

    inner class Error : Transition<TestFSMState.Async, TestFSMState.Error>(TestFSMState.Async::class, TestFSMState.Error::class) {
        override fun predicate(state: TestFSMState.Async) = !success

        override fun transform(state: TestFSMState.Async) = TestFSMState.Error
    }

    override fun getTransitions() = listOf(Success(), Error())
}