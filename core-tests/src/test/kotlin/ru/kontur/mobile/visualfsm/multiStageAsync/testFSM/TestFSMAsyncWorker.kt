package ru.kontur.mobile.visualfsm.multiStageAsync.testFSM

import kotlinx.coroutines.delay
import ru.kontur.mobile.visualfsm.AsyncWorker
import ru.kontur.mobile.visualfsm.AsyncWorkerTask
import ru.kontur.mobile.visualfsm.multiStageAsync.testFSM.action.Finish
import ru.kontur.mobile.visualfsm.multiStageAsync.testFSM.action.TestFSMAction
import ru.kontur.mobile.visualfsm.multiStageAsync.testFSM.action.UpdateStage

class TestFSMAsyncWorker : AsyncWorker<TestFSMState, TestFSMAction>() {
    override fun onNextState(state: TestFSMState): AsyncWorkerTask<TestFSMState> {
        return when (state) {
            is TestFSMState.AsyncWithStage -> AsyncWorkerTask.ExecuteIfNotExistWithSameClass(state) {
                delay(state.milliseconds.toLong())
                proceed(UpdateStage("stage1"))

                delay(state.milliseconds.toLong())
                proceed(UpdateStage("stage2"))

                delay(state.milliseconds.toLong())
                proceed(UpdateStage("stage3"))

                delay(state.milliseconds.toLong())
                proceed(Finish(true))
            }

            else -> AsyncWorkerTask.Cancel()
        }
    }
}