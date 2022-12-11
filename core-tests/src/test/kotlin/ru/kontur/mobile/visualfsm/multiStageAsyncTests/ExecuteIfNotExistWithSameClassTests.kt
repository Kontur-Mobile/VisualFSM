package ru.kontur.mobile.visualfsm.multiStageAsyncTests

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.TransitionCallbacks
import ru.kontur.mobile.visualfsm.helper.runFSMFeatureTest
import ru.kontur.mobile.visualfsm.multiStageAsyncTests.testFSM.TestFSMAsyncWorker
import ru.kontur.mobile.visualfsm.multiStageAsyncTests.testFSM.TestFSMFeature
import ru.kontur.mobile.visualfsm.multiStageAsyncTests.testFSM.TestFSMState
import ru.kontur.mobile.visualfsm.multiStageAsyncTests.testFSM.action.Start

@OptIn(ExperimentalCoroutinesApi::class)
class ExecuteIfNotExistWithSameClassTests {
    @Test
    fun doNotInterruptMultistageAsyncTaskTest() = runFSMFeatureTest(
        featureFactory = { dispatcher ->
            TestFSMFeature(
                initialState = TestFSMState.Initial,
                asyncWorker = TestFSMAsyncWorker(dispatcher),
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

        }
    ) { feature, states ->
        feature.proceed(Start("async1", 10))
        advanceUntilIdle()

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