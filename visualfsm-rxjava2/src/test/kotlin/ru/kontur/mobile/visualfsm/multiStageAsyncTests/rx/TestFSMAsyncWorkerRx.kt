package ru.kontur.mobile.visualfsm.multiStageAsyncTests.rx

import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import ru.kontur.mobile.visualfsm.rxjava2.AsyncWorkerRx
import ru.kontur.mobile.visualfsm.rxjava2.AsyncWorkerTaskRx
import ru.kontur.mobile.visualfsm.multiStageAsyncTests.testFSM.TestFSMState
import ru.kontur.mobile.visualfsm.multiStageAsyncTests.testFSM.action.Finish
import ru.kontur.mobile.visualfsm.multiStageAsyncTests.testFSM.action.TestFSMAction
import ru.kontur.mobile.visualfsm.multiStageAsyncTests.testFSM.action.UpdateStage
import java.util.concurrent.TimeUnit

class TestFSMAsyncWorkerRx : AsyncWorkerRx<TestFSMState, TestFSMAction>() {
    override fun onNextState(state: TestFSMState): AsyncWorkerTaskRx<TestFSMState> {
        return when (state) {
            is TestFSMState.AsyncWithStage -> AsyncWorkerTaskRx.ExecuteIfNotExistWithSameClass(state) {
                Flowable.fromIterable(listOf("stage1", "stage2", "stage3"))
                    .doOnNext {
                        if ("error" == state.label) throw Exception("Error on async operation")
                    }
                    .delay(state.milliseconds.toLong(), TimeUnit.MILLISECONDS)
                    .doOnNext {
                        proceed(UpdateStage(it))
                    }.ignoreElements()
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