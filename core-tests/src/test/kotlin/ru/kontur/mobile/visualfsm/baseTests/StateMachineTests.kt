package ru.kontur.mobile.visualfsm.baseTests

import kotlinx.coroutines.async
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.TransitionCallbacks
import ru.kontur.mobile.visualfsm.baseTests.testFSM.TestFSMAsyncWorker
import ru.kontur.mobile.visualfsm.baseTests.testFSM.TestFSMFeature
import ru.kontur.mobile.visualfsm.baseTests.testFSM.TestFSMState
import ru.kontur.mobile.visualfsm.baseTests.testFSM.action.Cancel
import ru.kontur.mobile.visualfsm.baseTests.testFSM.action.Start
import ru.kontur.mobile.visualfsm.baseTests.testFSM.action.TestFSMAction
import ru.kontur.mobile.visualfsm.tools.VisualFSM

class StateMachineTests {

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
            finalStates.size == 2 && finalStates.containsAll(
                listOf(
                    TestFSMState.Complete::class,
                    TestFSMState.Error::class
                )
            ),
            "FSM have not correct final states: ${finalStates.joinToString(", ")}"
        )
    }

    @Test
    fun startAsyncTest() {
        val feature = TestFSMFeature(TestFSMState.Initial, TestFSMAsyncWorker())

        assertEquals(TestFSMState.Initial, feature.getCurrentState())

        feature.proceed(Start("async1", 1))

        assertEquals(TestFSMState.Async("async1", 1), feature.getCurrentState())
    }

    @Test
    fun endAsyncTest() = runTest(UnconfinedTestDispatcher()) {
        val feature = TestFSMFeature(TestFSMState.Initial, TestFSMAsyncWorker())
        val states = mutableListOf<TestFSMState>()

        val job = async {
            feature.observeState().take(3).collect {
                states.add(it)
            }
        }

        assertEquals(TestFSMState.Initial, feature.getCurrentState())

        feature.proceed(Start("async1", 1))

        assertEquals(TestFSMState.Async("async1", 1), feature.getCurrentState())

        job.await()

        assertEquals(
            listOf(
                TestFSMState.Initial,
                TestFSMState.Async("async1", 1),
                TestFSMState.Complete("async1")
            ),
            states
        )
    }

    @Test
    fun errorAsyncTest() = runTest(UnconfinedTestDispatcher()) {
        val feature = TestFSMFeature(TestFSMState.Initial, TestFSMAsyncWorker())
        val states = mutableListOf<TestFSMState>()

        val job = async {
            feature.observeState().take(3).collect {
                states.add(it)
            }
        }

        assertEquals(TestFSMState.Initial, feature.getCurrentState())

        feature.proceed(Start("error", 1))

        assertEquals(TestFSMState.Async("error", 1), feature.getCurrentState())

        job.await()

        assertEquals(
            listOf(
                TestFSMState.Initial,
                TestFSMState.Async("error", 1),
                TestFSMState.Error
            ),
            states
        )
    }

    @Test
    fun cancelAsyncTest() = runTest(UnconfinedTestDispatcher()) {
        val feature = TestFSMFeature(TestFSMState.Initial, TestFSMAsyncWorker())
        val states = mutableListOf<TestFSMState>()

        val job = async {
            feature.observeState().take(5).collect {
                states.add(it)
            }
        }

        assertEquals(TestFSMState.Initial, feature.getCurrentState())

        feature.proceed(Start("async1", 100))

        assertEquals(TestFSMState.Async("async1", 100), feature.getCurrentState())

        feature.proceed(Cancel())

        //Task canceled
        assertEquals(TestFSMState.Initial, feature.getCurrentState())

        //Start new
        feature.proceed(Start("async2", 150))

        assertEquals(TestFSMState.Async("async2", 150), feature.getCurrentState())

        job.await()

        assertEquals(
            listOf(
                TestFSMState.Initial,
                TestFSMState.Async("async1", 100),
                TestFSMState.Initial,
                TestFSMState.Async("async2", 150),
                TestFSMState.Complete("async2")
            ),
            states
        )
    }

    @Test
    fun multiplyCancelByStartOtherAsyncTest() {
        for (i in 1..100) {
            println("Step: $i")
            cancelByStartOtherAsyncTest()
        }
    }

    private fun cancelByStartOtherAsyncTest() = runTest(UnconfinedTestDispatcher()) {
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

                override fun onRestoredFromBackStack(oldState: TestFSMState, newState: TestFSMState) {
                }
            })

        val states = mutableListOf<TestFSMState>()

        val job = async {
            feature.observeState().take(7).collect {
                states.add(it)
            }
        }

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

        job.await()

        assertEquals(
            listOf(
                TestFSMState.Initial,
                TestFSMState.Async("async1", 20),
                TestFSMState.Async("async2", 20),
                TestFSMState.Async("async3", 20),
                TestFSMState.Async("async4", 20),
                TestFSMState.Async("async5", 20),
                TestFSMState.Complete("async5")
            ),
            states
        )
    }
}