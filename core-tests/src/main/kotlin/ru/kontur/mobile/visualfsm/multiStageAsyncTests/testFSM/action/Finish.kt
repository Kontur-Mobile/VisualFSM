package ru.kontur.mobile.visualfsm.multiStageAsyncTests.testFSM.action

import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.multiStageAsyncTests.testFSM.TestFSMState

class Finish(val success: Boolean) : TestFSMAction() {

    inner class Success : Transition<TestFSMState.AsyncWithStage, TestFSMState.Complete>() {
        override fun predicate(state: TestFSMState.AsyncWithStage) = success

        override fun transform(state: TestFSMState.AsyncWithStage): TestFSMState.Complete {
            return TestFSMState.Complete(state.label)
        }
    }

    inner class Error : Transition<TestFSMState.AsyncWithStage, TestFSMState.Error>() {
        override fun predicate(state: TestFSMState.AsyncWithStage) = !success

        override fun transform(state: TestFSMState.AsyncWithStage): TestFSMState.Error {
            return TestFSMState.Error
        }
    }
}