package ru.kontur.mobile.visualfsm.testFSMWithBackStack

import kotlinx.coroutines.delay
import ru.kontur.mobile.visualfsm.AsyncWorker
import ru.kontur.mobile.visualfsm.AsyncWorkerTask
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.TestFSMState.Async
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.action.Finish
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.action.TestFSMAction

class TestFSMAsyncWorker : AsyncWorker<TestFSMState, TestFSMAction>() {
    override fun onNextState(state: TestFSMState): AsyncWorkerTask<TestFSMState> {
        return when (state) {
            is Async -> AsyncWorkerTask.ExecuteAndCancelExist(state) {
                delay(state.milliseconds.toLong())
                if ("error" == state.label) {
                    proceed(Finish(false))
                } else {
                    proceed(Finish(true))
                }
            }
            else -> AsyncWorkerTask.Cancel()
        }
    }
}