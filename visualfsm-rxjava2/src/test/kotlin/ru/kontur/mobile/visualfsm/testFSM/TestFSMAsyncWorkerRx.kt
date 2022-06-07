package ru.kontur.mobile.visualfsm.testFSM

import io.reactivex.Single
import ru.kontur.mobile.visualfsm.rxjava2.AsyncWorkerRx
import ru.kontur.mobile.visualfsm.rxjava2.AsyncWorkerTaskRx
import ru.kontur.mobile.visualfsm.testFSM.TestFSMState.*
import ru.kontur.mobile.visualfsm.testFSM.action.Finish
import ru.kontur.mobile.visualfsm.testFSM.action.TestFSMAction
import java.util.concurrent.TimeUnit

class TestFSMAsyncWorkerRx : AsyncWorkerRx<TestFSMState, TestFSMAction>() {
    override fun onNextState(state: TestFSMState): AsyncWorkerTaskRx<TestFSMState> {
        return when (state) {
            is Async -> AsyncWorkerTaskRx.ExecuteAndCancelExist(state) {
                Single.fromCallable {
                    if ("error" == state.label) throw Exception("Error on async operation")
                }
                    .delay(state.milliseconds.toLong(), TimeUnit.MILLISECONDS)
                    .subscribe({
                        proceed(state, Finish(true))
                    }, {
                        proceed(state, Finish(false))
                    })
            }
            else -> AsyncWorkerTaskRx.Cancel()
        }
    }
}