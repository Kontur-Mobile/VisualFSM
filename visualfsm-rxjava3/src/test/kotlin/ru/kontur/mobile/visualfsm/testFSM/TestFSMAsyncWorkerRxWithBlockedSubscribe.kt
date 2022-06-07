package ru.kontur.mobile.visualfsm.testFSM

import io.reactivex.rxjava3.core.Single
import ru.kontur.mobile.visualfsm.rxjava3.AsyncWorkerRx
import ru.kontur.mobile.visualfsm.rxjava3.AsyncWorkerTaskRx
import ru.kontur.mobile.visualfsm.testFSM.TestFSMState.*
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
                        proceed(state, Finish(true))
                    }, {
                        Thread.sleep(state.milliseconds.toLong())
                        proceed(state, Finish(false))
                    })
            }
            else -> AsyncWorkerTaskRx.Cancel()
        }
    }
}