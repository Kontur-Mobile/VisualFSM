package ru.kontur.mobile.visualfsm.testFSMWithBackStack

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.TransitionCallbacks
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.TestFSMWBSState
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.action.Cancel
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.action.Start
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.action.TestFSMAction
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.rx.TestFSMAsyncWorkerRx
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.rx.TestFSMFeatureRx
import ru.kontur.mobile.visualfsm.tools.VisualFSM

class StateMachineRxTests {

    @Test
    fun generateDigraphTest() {
        val digraph = VisualFSM.generateDigraph(
            baseAction = TestFSMAction::class,
            baseState = TestFSMWBSState::class,
            initialState = TestFSMWBSState.Initial::class
        )

        assertEquals(
            "\n" +
                    "digraph TestFSMWBSStateTransitions {\n" +
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
            baseState = TestFSMWBSState::class,
            initialState = TestFSMWBSState.Initial::class
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
            baseState = TestFSMWBSState::class,
        )

        assertTrue(
            finalStates.size == 1 && finalStates.containsAll(
                listOf(
                    TestFSMWBSState.Error::class
                )
            ),
            "FSM have not correct final states: ${finalStates.joinToString(", ")}"
        )
    }

    @Test
    fun startAsyncTest() {
        val feature = TestFSMFeatureRx(TestFSMWBSState.Initial, TestFSMAsyncWorkerRx())

        assertEquals(TestFSMWBSState.Initial, feature.getCurrentState())

        feature.proceed(Start("async1", 1))

        assertEquals(TestFSMWBSState.Async("async1", 1), feature.getCurrentState())
    }

    @Test
    fun endAsyncTest() {
        val feature = TestFSMFeatureRx(TestFSMWBSState.Initial, TestFSMAsyncWorkerRx())
        val testObserver = feature.observeState().test()

        assertEquals(TestFSMWBSState.Initial, feature.getCurrentState())

        feature.proceed(Start("async1", 1))

        assertEquals(TestFSMWBSState.Async("async1", 1), feature.getCurrentState())

        testObserver.awaitCount(3)

        testObserver.assertValues(
            TestFSMWBSState.Initial,
            TestFSMWBSState.Async("async1", 1),
            TestFSMWBSState.Complete("async1")
        )

        testObserver.dispose()
    }

    @Test
    fun errorAsyncTest() {
        val feature = TestFSMFeatureRx(TestFSMWBSState.Initial, TestFSMAsyncWorkerRx())
        val testObserver = feature.observeState().test()

        assertEquals(TestFSMWBSState.Initial, feature.getCurrentState())

        feature.proceed(Start("error", 1))

        assertEquals(TestFSMWBSState.Async("error", 1), feature.getCurrentState())

        testObserver.awaitCount(3)
        testObserver.assertValues(
            TestFSMWBSState.Initial,
            TestFSMWBSState.Async("error", 1),
            TestFSMWBSState.Error
        )
        testObserver.dispose()
    }

    @Test
    fun cancelAsyncTest() {
        val feature = TestFSMFeatureRx(TestFSMWBSState.Initial, TestFSMAsyncWorkerRx())
        val testObserver = feature.observeState().test()

        assertEquals(TestFSMWBSState.Initial, feature.getCurrentState())

        feature.proceed(Start("async1", 100))

        assertEquals(TestFSMWBSState.Async("async1", 100), feature.getCurrentState())

        feature.proceed(Cancel())

        //Task canceled
        assertEquals(TestFSMWBSState.Initial, feature.getCurrentState())

        //Start new
        feature.proceed(Start("async2", 150))

        assertEquals(TestFSMWBSState.Async("async2", 150), feature.getCurrentState())

        testObserver.awaitCount(5)
        testObserver.assertValues(
            TestFSMWBSState.Initial,
            TestFSMWBSState.Async("async1", 100),
            TestFSMWBSState.Initial,
            TestFSMWBSState.Async("async2", 150),
            TestFSMWBSState.Complete("async2")
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
            initialState = TestFSMWBSState.Initial,
            asyncWorker = TestFSMAsyncWorkerRx(),
            transitionCallbacks = object : TransitionCallbacks<TestFSMWBSState> {
                override fun onActionLaunched(action: Action<TestFSMWBSState>, currentState: TestFSMWBSState) {
                }

                override fun onTransitionSelected(
                    action: Action<TestFSMWBSState>,
                    transition: Transition<TestFSMWBSState, TestFSMWBSState>,
                    currentState: TestFSMWBSState,
                ) {
                }

                override fun onNewStateReduced(
                    action: Action<TestFSMWBSState>,
                    transition: Transition<TestFSMWBSState, TestFSMWBSState>,
                    oldState: TestFSMWBSState,
                    newState: TestFSMWBSState
                ) {
                }

                override fun onNoTransitionError(action: Action<TestFSMWBSState>, currentState: TestFSMWBSState) {
                    throw IllegalStateException("onNoTransitionError $action $currentState")
                }

                override fun onMultipleTransitionError(action: Action<TestFSMWBSState>, currentState: TestFSMWBSState) {
                    throw IllegalStateException("onMultipleTransitionError $action $currentState")
                }

                override fun onRestoredFromBackStack(oldState: TestFSMWBSState, newState: TestFSMWBSState) {
                }
            })

        val testObserver = feature.observeState().test()

        assertEquals(TestFSMWBSState.Initial, feature.getCurrentState())

        feature.proceed(Start("async1", 20))
        Thread.sleep(10)
        feature.proceed(Start("async2", 20))
        Thread.sleep(10)
        feature.proceed(Start("async3", 20))
        Thread.sleep(10)
        feature.proceed(Start("async4", 20))
        Thread.sleep(10)
        feature.proceed(Start("async5", 20))

        assertEquals(TestFSMWBSState.Async("async5", 20), feature.getCurrentState())

        testObserver.awaitCount(7)

        testObserver.assertValues(
            TestFSMWBSState.Initial,
            TestFSMWBSState.Async("async1", 20),
            TestFSMWBSState.Async("async2", 20),
            TestFSMWBSState.Async("async3", 20),
            TestFSMWBSState.Async("async4", 20),
            TestFSMWBSState.Async("async5", 20),
            TestFSMWBSState.Complete("async5")
        )

        testObserver.dispose()
    }
}