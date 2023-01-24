package ru.kontur.mobile.visualfsm.multiStageAsyncTests.testFSM

import kotlinx.coroutines.delay
import ru.kontur.mobile.visualfsm.AsyncWorker
import ru.kontur.mobile.visualfsm.AsyncWorkerTask
import ru.kontur.mobile.visualfsm.multiStageAsyncTests.testFSM.action.Finish
import ru.kontur.mobile.visualfsm.multiStageAsyncTests.testFSM.action.TestFSMAction
import ru.kontur.mobile.visualfsm.multiStageAsyncTests.testFSM.action.UpdateStage
import kotlin.coroutines.CoroutineContext

class TestFSMAsyncWorker(coroutineDispatcher: CoroutineContext) : AsyncWorker<TestFSMState, TestFSMAction>(coroutineDispatcher) {
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