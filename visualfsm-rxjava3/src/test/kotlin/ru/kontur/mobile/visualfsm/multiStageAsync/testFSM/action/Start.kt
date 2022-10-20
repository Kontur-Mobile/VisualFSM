package ru.kontur.mobile.visualfsm.multiStageAsync.testFSM.action

import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.multiStageAsync.testFSM.TestFSMState

class Start(val label: String, val milliseconds: Int) : TestFSMAction() {

    inner class Start : Transition<TestFSMState.Initial, TestFSMState.AsyncWithStage>() {
        override fun transform(state: TestFSMState.Initial): TestFSMState.AsyncWithStage {
            return TestFSMState.AsyncWithStage(label, "stage0", milliseconds)
        }
    }

    inner class StartOther : Transition<TestFSMState.AsyncWithStage, TestFSMState.AsyncWithStage>() {
        override fun transform(state: TestFSMState.AsyncWithStage): TestFSMState.AsyncWithStage {
            return TestFSMState.AsyncWithStage(label, "stage0", milliseconds)
        }
    }
}