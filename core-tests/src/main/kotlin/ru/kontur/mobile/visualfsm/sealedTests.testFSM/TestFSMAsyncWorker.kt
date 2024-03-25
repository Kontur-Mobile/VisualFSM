package ru.kontur.mobile.visualfsm.sealedTests.testFSM

import ru.kontur.mobile.visualfsm.AsyncWorker
import ru.kontur.mobile.visualfsm.AsyncWorkerTask
import ru.kontur.mobile.visualfsm.sealedTests.testFSM.action.NavigateCompleted
import ru.kontur.mobile.visualfsm.sealedTests.testFSM.action.TestFSMAction
import kotlin.coroutines.CoroutineContext

class TestFSMAsyncWorker(coroutineDispatcher: CoroutineContext) : AsyncWorker<TestFSMState, TestFSMAction>(coroutineDispatcher) {
    override fun onNextState(state: TestFSMState): AsyncWorkerTask<TestFSMState> {
        return when (state) {
            is TestFSMState.NavigationState.Screen -> {
                AsyncWorkerTask.ExecuteIfNotExistWithSameClass(state) {
                    proceed(NavigateCompleted())
                }
            }

            is TestFSMState.NavigationState.DialogState,
            TestFSMState.Initial,
            -> AsyncWorkerTask.Cancel()
        }
    }
}