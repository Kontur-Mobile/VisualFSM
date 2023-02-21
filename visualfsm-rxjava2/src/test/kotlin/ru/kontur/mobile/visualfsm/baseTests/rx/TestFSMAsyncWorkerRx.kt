package ru.kontur.mobile.visualfsm.baseTests.rx

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.kontur.mobile.visualfsm.rxjava2.AsyncWorkerRx
import ru.kontur.mobile.visualfsm.rxjava2.AsyncWorkerTaskRx
import ru.kontur.mobile.visualfsm.baseTests.testFSM.TestFSMState
import ru.kontur.mobile.visualfsm.baseTests.testFSM.TestFSMState.Async
import ru.kontur.mobile.visualfsm.baseTests.testFSM.action.Finish
import ru.kontur.mobile.visualfsm.baseTests.testFSM.action.TestFSMAction
import java.util.concurrent.TimeUnit

class TestFSMAsyncWorkerRx : AsyncWorkerRx<TestFSMState, TestFSMAction>() {
    override fun onNextState(state: TestFSMState): AsyncWorkerTaskRx<TestFSMState> {
        return when (state) {
            is Async -> AsyncWorkerTaskRx.ExecuteAndCancelExist(state) {
                Single.fromCallable {
                    if ("error" == state.label) throw Exception("Error on async operation")
                }
                    .delay(state.milliseconds.toLong(), TimeUnit.MILLISECONDS)
                    .observeOn(Schedulers.newThread())
                    .subscribe({
                        proceed(Finish(true, state.salt))
                    }, {
                        proceed(Finish(false, state.salt))
                    })
            }
            else -> AsyncWorkerTaskRx.Cancel()
        }
    }
}