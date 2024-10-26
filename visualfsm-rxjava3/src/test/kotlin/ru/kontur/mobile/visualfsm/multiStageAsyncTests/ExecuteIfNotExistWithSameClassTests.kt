package ru.kontur.mobile.visualfsm.multiStageAsyncTests

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.TransitionCallbacks
import ru.kontur.mobile.visualfsm.multiStageAsyncTests.testFSM.TestFSMState
import ru.kontur.mobile.visualfsm.multiStageAsyncTests.testFSM.action.Start
import ru.kontur.mobile.visualfsm.multiStageAsyncTests.rx.TestFSMAsyncWorkerRx
import ru.kontur.mobile.visualfsm.multiStageAsyncTests.rx.TestFSMFeatureRx

class ExecuteIfNotExistWithSameClassTests {

    @Test
    fun doNotInterruptMultistageAsyncTaskTest() {
        val feature = TestFSMFeatureRx(
            initialState = TestFSMState.Initial,
            asyncWorker = TestFSMAsyncWorkerRx(),
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

                override fun onInitialStateReceived(initialState: TestFSMState) {
                }
            })

        val testObserver = feature.observeState().test()

        assertEquals(TestFSMState.Initial, feature.getCurrentState())

        feature.proceed(Start("async1", 10))

        testObserver.awaitCount(6)

        testObserver.assertValues(
            TestFSMState.Initial,
            TestFSMState.AsyncWithStage("async1", "stage0", 10),
            TestFSMState.AsyncWithStage("async1", "stage1", 10),
            TestFSMState.AsyncWithStage("async1", "stage2", 10),
            TestFSMState.AsyncWithStage("async1", "stage3", 10),
            TestFSMState.Complete("async1")
        )

        testObserver.dispose()
    }
}