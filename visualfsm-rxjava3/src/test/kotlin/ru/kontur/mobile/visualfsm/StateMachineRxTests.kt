package ru.kontur.mobile.visualfsm

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.TestFSMState
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.action.Cancel
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.action.Start
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.action.TestFSMAction
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.rx.TestFSMAsyncWorkerRx
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.rx.TestFSMFeatureRx
import ru.kontur.mobile.visualfsm.tools.VisualFSM

class StateMachineRxTests {

    @Test
    fun generateDigraphTest() {
        val digraph = VisualFSM.generateDigraph(
            baseAction = TestFSMAction::class,
            baseState = TestFSMState::class,
            initialState = TestFSMState.Initial::class
        )

        assertEquals(
            "\n" +
                    "digraph TestFSMStateTransitions {\n" +
                    "\"Initial\"\n" +
                    "\"Async\" -> \"Initial\" [label=\" Cancel\"]\n" +
                    "\"Complete\" -> \"Initial\" [label=\" Close\"]\n" +
                    "\"Async\" -> \"Error\" [label=\" Error\"]\n" +
                    "\"Async\" -> \"Complete\" [label=\" Success\"]\n" +
                    "\"Initial\" -> \"Async\" [label=\" Start\"]\n" +
                    "\"Async\" -> \"Async\" [label=\" StartOther\"]\n" +
                    "}\n" +
                    "\n", digraph
        )
    }

    @Test
    fun allStatesReachableTest() {
        val notReachableStates = VisualFSM.getUnreachableStates(
            baseAction = TestFSMAction::class,
            baseState = TestFSMState::class,
            initialState = TestFSMState.Initial::class
        )

        assertTrue(
            notReachableStates.isEmpty(),
            "FSM have unreachable states: ${notReachableStates.joinToString(", ")}"
        )
    }

    @Test
    fun oneFinalStateTest() {
        val finalStates = VisualFSM.getFinalStates(
            baseAction = TestFSMAction::class,
            baseState = TestFSMState::class,
        )

        assertTrue(
            finalStates.size == 1 && finalStates.containsAll(
                listOf(
                    TestFSMState.Error::class
                )
            ),
            "FSM have not correct final states: ${finalStates.joinToString(", ")}"
        )
    }

    @Test
    fun startAsyncTest() {
        val feature = TestFSMFeatureRx(TestFSMState.Initial, TestFSMAsyncWorkerRx())

        assertEquals(TestFSMState.Initial, feature.getCurrentState())

        feature.proceed(Start("async1", 1))

        assertEquals(TestFSMState.Async("async1", 1), feature.getCurrentState())
    }

    @Test
    fun endAsyncTest() {
        val feature = TestFSMFeatureRx(TestFSMState.Initial, TestFSMAsyncWorkerRx())
        val testObserver = feature.observeState().test()

        assertEquals(TestFSMState.Initial, feature.getCurrentState())

        feature.proceed(Start("async1", 1))

        assertEquals(TestFSMState.Async("async1", 1), feature.getCurrentState())

        testObserver.awaitCount(3)

        testObserver.assertValues(
            TestFSMState.Initial,
            TestFSMState.Async("async1", 1),
            TestFSMState.Complete("async1")
        )

        testObserver.dispose()
    }

    @Test
    fun errorAsyncTest() {
        val feature = TestFSMFeatureRx(TestFSMState.Initial, TestFSMAsyncWorkerRx())
        val testObserver = feature.observeState().test()

        assertEquals(TestFSMState.Initial, feature.getCurrentState())

        feature.proceed(Start("error", 1))

        assertEquals(TestFSMState.Async("error", 1), feature.getCurrentState())

        testObserver.awaitCount(3)
        testObserver.assertValues(
            TestFSMState.Initial,
            TestFSMState.Async("error", 1),
            TestFSMState.Error
        )
        testObserver.dispose()
    }

    @Test
    fun cancelAsyncTest() {
        val feature = TestFSMFeatureRx(TestFSMState.Initial, TestFSMAsyncWorkerRx())
        val testObserver = feature.observeState().test()

        assertEquals(TestFSMState.Initial, feature.getCurrentState())

        feature.proceed(Start("async1", 100))

        assertEquals(TestFSMState.Async("async1", 100), feature.getCurrentState())

        feature.proceed(Cancel())

        //Task canceled
        assertEquals(TestFSMState.Initial, feature.getCurrentState())

        //Start new
        feature.proceed(Start("async2", 150))

        assertEquals(TestFSMState.Async("async2", 150), feature.getCurrentState())

        testObserver.awaitCount(5)
        testObserver.assertValues(
            TestFSMState.Initial,
            TestFSMState.Async("async1", 100),
            TestFSMState.Initial,
            TestFSMState.Async("async2", 150),
            TestFSMState.Complete("async2")
        )
        testObserver.dispose()
    }

    @Test
    fun multiplyCancelByStartOtherAsyncTest() {
        for (i in 1..100) {
            println("Step: $i")
            cancelByStartOtherAsyncTest()
        }
    }

    private fun cancelByStartOtherAsyncTest() {
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

                override fun onRestoredFromBackStack(oldState: TestFSMState, newState: TestFSMState) {
                }
            })

        val testObserver = feature.observeState().test()

        assertEquals(TestFSMState.Initial, feature.getCurrentState())

        feature.proceed(Start("async1", 20))
        Thread.sleep(10)
        feature.proceed(Start("async2", 20))
        Thread.sleep(10)
        feature.proceed(Start("async3", 20))
        Thread.sleep(10)
        feature.proceed(Start("async4", 20))
        Thread.sleep(10)
        feature.proceed(Start("async5", 20))

        assertEquals(TestFSMState.Async("async5", 20), feature.getCurrentState())

        testObserver.awaitCount(7)

        testObserver.assertValues(
            TestFSMState.Initial,
            TestFSMState.Async("async1", 20),
            TestFSMState.Async("async2", 20),
            TestFSMState.Async("async3", 20),
            TestFSMState.Async("async4", 20),
            TestFSMState.Async("async5", 20),
            TestFSMState.Complete("async5")
        )

        testObserver.dispose()
    }
}