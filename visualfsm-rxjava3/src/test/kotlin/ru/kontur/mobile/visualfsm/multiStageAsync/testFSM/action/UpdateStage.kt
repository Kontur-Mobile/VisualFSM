package ru.kontur.mobile.visualfsm.multiStageAsync.testFSM.action

import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.multiStageAsync.testFSM.TestFSMState

class UpdateStage(val newStage: String) : TestFSMAction() {

    inner class ChangeStage : Transition<TestFSMState.AsyncWithStage, TestFSMState.AsyncWithStage>() {
        override fun transform(state: TestFSMState.AsyncWithStage): TestFSMState.AsyncWithStage {
            return state.copy(stage = newStage)
        }
    }
}