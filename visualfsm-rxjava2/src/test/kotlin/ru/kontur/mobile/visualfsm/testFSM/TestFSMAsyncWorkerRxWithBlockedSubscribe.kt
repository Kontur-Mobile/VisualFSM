package ru.kontur.mobile.visualfsm.testFSM

import io.reactivex.Single
import ru.kontur.mobile.visualfsm.rxjava2.AsyncWorkerRx
import ru.kontur.mobile.visualfsm.rxjava2.AsyncWorkerTaskRx
import ru.kontur.mobile.visualfsm.testFSM.TestFSMState.Async
import ru.kontur.mobile.visualfsm.testFSM.action.Finish
import ru.kontur.mobile.visualfsm.testFSM.action.TestFSMAction

class TestFSMAsyncWorkerRxWithBlockedSubscribe : AsyncWorkerRx<TestFSMState, TestFSMAction>() {
    override fun onNextState(state: TestFSMState): AsyncWorkerTaskRx<TestFSMState> {
        return when (state) {
            is Async -> AsyncWorkerTaskRx.ExecuteAndCancelExist(state) {
                Single.fromCallable {
                    if ("error" == state.label) throw Exception("Error on async operation")
                }
                    .subscribe({
                        Thread.sleep(state.milliseconds.toLong())
                        proceed(Finish(true))
                    }, {
                        Thread.sleep(state.milliseconds.toLong())
                        proceed(Finish(false))
                    })
            }
            else -> AsyncWorkerTaskRx.Cancel()
        }
    }
}