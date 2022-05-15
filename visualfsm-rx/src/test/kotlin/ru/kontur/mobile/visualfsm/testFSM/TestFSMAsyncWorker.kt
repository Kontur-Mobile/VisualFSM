package ru.kontur.mobile.visualfsm.testFSM

import io.reactivex.Single
import ru.kontur.mobile.visualfsm.AsyncWorkerRx
import ru.kontur.mobile.visualfsm.AsyncWorkerTaskRx
import ru.kontur.mobile.visualfsm.testFSM.action.TestFSMAction
import java.util.concurrent.TimeUnit

class TestFSMAsyncWorker : AsyncWorkerRx<TestFSMState, TestFSMAction>() {
    override fun onNextState(state: TestFSMState): AsyncWorkerTaskRx<TestFSMState> {
        return when (state) {
            TestFSMState.A -> AsyncWorkerTaskRx.Cancel()
            TestFSMState.B -> AsyncWorkerTaskRx.ExecuteAndCancelExist(state) {
                Single.just(true).delay(5, TimeUnit.SECONDS).subscribe({}, {})
            }
            TestFSMState.C -> AsyncWorkerTaskRx.Cancel()
        }
    }
}