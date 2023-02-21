package ru.kontur.mobile.visualfsm.baseTests.testFSM

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import ru.kontur.mobile.visualfsm.AsyncWorker
import ru.kontur.mobile.visualfsm.AsyncWorkerTask
import ru.kontur.mobile.visualfsm.baseTests.testFSM.TestFSMState.Async
import ru.kontur.mobile.visualfsm.baseTests.testFSM.action.Finish
import ru.kontur.mobile.visualfsm.baseTests.testFSM.action.TestFSMAction
import kotlin.coroutines.CoroutineContext

class TestFSMAsyncWorker(
    coroutineDispatcher: CoroutineContext,
    private val onSubscriptionError: ((Throwable) -> Unit)? = null
) : AsyncWorker<TestFSMState, TestFSMAction>(coroutineDispatcher) {
    override fun onNextState(state: TestFSMState): AsyncWorkerTask<TestFSMState> {
        return when (state) {
            is Async -> AsyncWorkerTask.ExecuteAndCancelExist(state) {
                try {
                    delay(state.milliseconds.toLong())
                    if (state.label.contains("error")) {
                        throw IllegalStateException(state.label)
                    }
                    proceed(Finish(success = true, state.salt))
                } catch (e: Exception) {
                    if (e is CancellationException) {
                        throw e
                    }
                    if (e.message == "uncaught error") {
                        throw e
                    }
                    proceed(Finish(success = false, state.salt))
                }
            }
            else -> AsyncWorkerTask.Cancel()
        }
    }

    override fun onStateSubscriptionError(throwable: Throwable) {
        if (onSubscriptionError == null) {
            super.onStateSubscriptionError(throwable)
        } else {
            onSubscriptionError.invoke(throwable)
        }
    }
}