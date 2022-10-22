package ru.kontur.mobile.visualfsm.multiStageAsyncTests.testFSM.action

import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.multiStageAsyncTests.testFSM.TestFSMState

class Cancel : TestFSMAction() {

    inner class Cancel : Transition<TestFSMState.AsyncWithStage, TestFSMState.Initial>() {
        override fun transform(state: TestFSMState.AsyncWithStage): TestFSMState.Initial {
            return TestFSMState.Initial
        }
    }
}