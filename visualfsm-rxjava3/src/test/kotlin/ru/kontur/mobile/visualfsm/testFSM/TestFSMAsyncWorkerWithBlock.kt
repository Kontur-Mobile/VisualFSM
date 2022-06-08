package ru.kontur.mobile.visualfsm.testFSM

import ru.kontur.mobile.visualfsm.AsyncWorker
import ru.kontur.mobile.visualfsm.AsyncWorkerTask
import ru.kontur.mobile.visualfsm.testFSM.TestFSMState.Async
import ru.kontur.mobile.visualfsm.testFSM.action.Finish
import ru.kontur.mobile.visualfsm.testFSM.action.TestFSMAction

class TestFSMAsyncWorkerWithBlock : AsyncWorker<TestFSMState, TestFSMAction>() {
    override fun onNextState(state: TestFSMState): AsyncWorkerTask<TestFSMState> {
        return when (state) {
            is Async -> AsyncWorkerTask.ExecuteAndCancelExist(state) {
                Thread.sleep(state.milliseconds.toLong())
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