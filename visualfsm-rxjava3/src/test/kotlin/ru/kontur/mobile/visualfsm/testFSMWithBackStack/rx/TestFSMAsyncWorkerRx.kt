package ru.kontur.mobile.visualfsm.testFSMWithBackStack.rx

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.kontur.mobile.visualfsm.rxjava3.AsyncWorkerRx
import ru.kontur.mobile.visualfsm.rxjava3.AsyncWorkerTaskRx
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.TestFSMState
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.TestFSMState.Async
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.action.Finish
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.action.TestFSMAction
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
                        proceed(Finish(true))
                    }, {
                        proceed(Finish(false))
                    })
            }
            else -> AsyncWorkerTaskRx.Cancel()
        }
    }
}