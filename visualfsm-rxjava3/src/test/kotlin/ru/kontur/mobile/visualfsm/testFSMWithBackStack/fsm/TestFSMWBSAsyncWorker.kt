package ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm

import kotlinx.coroutines.delay
import ru.kontur.mobile.visualfsm.AsyncWorker
import ru.kontur.mobile.visualfsm.AsyncWorkerTask
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.TestFSMWBSState.Async
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.action.Finish
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.action.TestFSMAction

class TestFSMWBSAsyncWorker : AsyncWorker<TestFSMWBSState, TestFSMAction>() {
    override fun onNextState(state: TestFSMWBSState): AsyncWorkerTask<TestFSMWBSState> {
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