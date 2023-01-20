package ru.kontur.mobile.visualfsm.baseTests

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
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
import ru.kontur.mobile.visualfsm.helper.runFSMFeatureTest
import ru.kontur.mobile.visualfsm.tools.VisualFSM

@OptIn(ExperimentalCoroutinesApi::class)
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
        val feature = TestFSMFeature(TestFSMState.Initial, TestFSMAsyncWorker(StandardTestDispatcher()))
        assertEquals(TestFSMState.Initial, feature.getCurrentState())

        feature.proceed(Start("async1", 1))
        assertEquals(TestFSMState.Async("async1", 1), feature.getCurrentState())
    }

    @Test
    fun endAsyncTest() = runFSMFeatureTest(
        featureFactory = { dispatcher ->
            TestFSMFeature(TestFSMState.Initial, TestFSMAsyncWorker(dispatcher))
        }
    ) { feature, states ->
        feature.proceed(Start("async1", 1000))
        advanceUntilIdle()

        assertEquals(
            listOf(
                TestFSMState.Initial,
                TestFSMState.Async("async1", 1000),
                TestFSMState.Complete("async1")
            ),
            states
        )
    }

    @Test
    fun errorAsyncTest() = runFSMFeatureTest(
        featureFactory = { dispatcher ->
            TestFSMFeature(TestFSMState.Initial, TestFSMAsyncWorker(dispatcher))
        }
    ) { feature, states ->
        feature.proceed(Start("error", 1000))
        advanceUntilIdle()

        assertEquals(
            listOf(
                TestFSMState.Initial,
                TestFSMState.Async("error", 1000),
                TestFSMState.Error
            ),
            states
        )
    }

    @Test
    fun cancelAsyncTest() = runFSMFeatureTest(
        featureFactory = { dispatcher ->
            TestFSMFeature(TestFSMState.Initial, TestFSMAsyncWorker(dispatcher))
        }
    ) { feature, states ->
        feature.proceed(Start("async1", 100))
        advanceTimeBy(50)

        // Cancel task
        feature.proceed(Cancel())
        advanceUntilIdle()

        // Start new
        feature.proceed(Start("async2", 150))
        advanceUntilIdle()

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
    fun destroyFeatureAsyncCancellationTest() {
        var exceptionHandled = false

        runFSMFeatureTest(
            featureFactory = { dispatcher ->
                TestFSMFeature(
                    TestFSMState.Initial,
                    TestFSMAsyncWorker(dispatcher, onSubscriptionError = {
                        exceptionHandled = true
                    })
                )
            }
        ) { feature, states ->
            feature.proceed(Start("async1", 100))
            advanceTimeBy(50)

            feature.onDestroy()
            advanceUntilIdle()

            assertEquals(
                listOf(
                    TestFSMState.Initial,
                    TestFSMState.Async("async1", 100)
                ),
                states
            )
        }
        assertEquals(false, exceptionHandled)
    }

    @Test
    fun stateSubscriptionErrorHandlerTest() {
        var exceptionHandled = false

        runFSMFeatureTest(
            featureFactory = { dispatcher ->
                TestFSMFeature(
                    TestFSMState.Initial,
                    TestFSMAsyncWorker(dispatcher, onSubscriptionError = {
                        exceptionHandled = true
                    })
                )
            }
        ) { feature, states ->
            feature.proceed(Start("uncaught error", 1000))
            advanceUntilIdle()

            assertEquals(
                listOf(
                    TestFSMState.Initial,
                    TestFSMState.Async("uncaught error", 1000)
                ),
                states
            )
        }
        assertEquals(true, exceptionHandled)
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
            asyncWorker = TestFSMAsyncWorker(Dispatchers.Default),
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