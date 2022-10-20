package ru.kontur.mobile.visualfsm.multiStageAsync

import kotlinx.coroutines.async
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.TransitionCallbacks
import ru.kontur.mobile.visualfsm.multiStageAsync.testFSM.TestFSMAsyncWorker
import ru.kontur.mobile.visualfsm.multiStageAsync.testFSM.TestFSMFeature
import ru.kontur.mobile.visualfsm.multiStageAsync.testFSM.TestFSMState
import ru.kontur.mobile.visualfsm.multiStageAsync.testFSM.action.Start

class ExecuteIfNotExistWithSameClassTests {

    @Test
    fun doNotInterruptMultistageAsyncTaskTest() = runTest(UnconfinedTestDispatcher()) {
        val feature = TestFSMFeature(
            initialState = TestFSMState.Initial,
            asyncWorker = TestFSMAsyncWorker(),
            transitionCallbacks = object : TransitionCallbacks<TestFSMState> {
                override fun onActionLaunched(action: Action<TestFSMState>, currentState: TestFSMState) {
                }

                override fun onTransitionSelected(
                    action: Action<TestFSMState>,
                    transition: Transition<TestFSMState, TestFSMState>,
                    currentState: TestFSMState,
                ) {
                }

                override fun onNewStateReduced(
                    action: Action<TestFSMState>,
                    transition: Transition<TestFSMState, TestFSMState>,
                    oldState: TestFSMState,
                    newState: TestFSMState
                ) {
                }

                override fun onNoTransitionError(action: Action<TestFSMState>, currentState: TestFSMState) {
                    throw IllegalStateException("onNoTransitionError $action $currentState")
                }

                override fun onMultipleTransitionError(action: Action<TestFSMState>, currentState: TestFSMState) {
                    throw IllegalStateException("onMultipleTransitionError $action $currentState")
                }
            })

        val states = mutableListOf<TestFSMState>()

        val job = async {
            feature.observeState().take(6).collect {
                states.add(it)
            }
        }

        assertEquals(TestFSMState.Initial, feature.getCurrentState())

        feature.proceed(Start("async1", 10))

        job.await()

        assertEquals(
            listOf(
                TestFSMState.Initial,
                TestFSMState.AsyncWithStage("async1", "stage0", 10),
                TestFSMState.AsyncWithStage("async1", "stage1", 10),
                TestFSMState.AsyncWithStage("async1", "stage2", 10),
                TestFSMState.AsyncWithStage("async1", "stage3", 10),
                TestFSMState.Complete("async1")
            ),
            states
        )
    }
}