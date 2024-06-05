package ru.kontur.mobile.visualfsm.dslTests.testFSM

import ru.kontur.mobile.visualfsm.AsyncWorker
import ru.kontur.mobile.visualfsm.AsyncWorkerTask
import ru.kontur.mobile.visualfsm.dslTests.testFSM.action.TestDSLAction
import kotlin.coroutines.CoroutineContext

class TestDslFSMAsyncWorker(coroutineDispatcher: CoroutineContext) : AsyncWorker<TestDSLFSMState, TestDSLAction>(coroutineDispatcher) {
    override fun onNextState(state: TestDSLFSMState): AsyncWorkerTask<TestDSLFSMState> {
        return when (state) {
            TestDSLFSMState.AsyncWorkerState.Loading -> {
                AsyncWorkerTask.ExecuteAndCancelExist(state) {

                }
            }

            is TestDSLFSMState.Initial -> AsyncWorkerTask.Cancel()
            is TestDSLFSMState.Loaded -> AsyncWorkerTask.Cancel()
        }
    }
}