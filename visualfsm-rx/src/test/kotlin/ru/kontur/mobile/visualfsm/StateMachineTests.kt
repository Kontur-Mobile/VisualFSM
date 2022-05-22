package ru.kontur.mobile.visualfsm

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.testFSM.TestFSMAsyncWorker
import ru.kontur.mobile.visualfsm.testFSM.TestFSMState
import ru.kontur.mobile.visualfsm.testFSM.action.Cancel
import ru.kontur.mobile.visualfsm.testFSM.action.Start
import ru.kontur.mobile.visualfsm.testFSM.action.TestFSMAction
import ru.kontur.mobile.visualfsm.tools.VisualFSM

class StateMachineTests {

    @Test
    fun generateDigraphTest() {
        val digraph = VisualFSM.generateDigraph(
            baseAction = TestFSMAction::class,
            baseState = TestFSMState::class,
            initialState = TestFSMState.Initial::class
        )

        Assertions.assertEquals(
            "\n" +
                    "digraph TestFSMStateTransitions {\n" +
                    "\"Initial\"\n" +
                    "\"Async\" -> \"Initial\" [label=\" Cancel\"]\n" +
                    "\"Async\" -> \"Error\" [label=\" Error\"]\n" +
                    "\"Async\" -> \"Complete\" [label=\" Success\"]\n" +
                    "\"Initial\" -> \"Async\" [label=\" Start\"]\n" +
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
        val feature = Feature(TestFSMState.Initial, TestFSMAsyncWorker())

        assertTrue(feature.getCurrentState() == TestFSMState.Initial)

        feature.proceed(Start("async1", 1))

        assertTrue(feature.getCurrentState() == TestFSMState.Async("async1", 1))
    }

    @Test
    fun endAsyncTest() = runTest(UnconfinedTestDispatcher()) {
        val feature = Feature(TestFSMState.Initial, TestFSMAsyncWorker())
        val states = mutableListOf<TestFSMState>()

        val job = async {
            feature.observeState().take(3).collect {
                states.add(it)
            }
        }

        assertTrue(feature.getCurrentState() == TestFSMState.Initial)

        feature.proceed(Start("async1", 1))

        assertTrue(feature.getCurrentState() == TestFSMState.Async("async1", 1))

        job.await()

        assertEquals(
            states,
            listOf(
                TestFSMState.Initial,
                TestFSMState.Async("async1", 1),
                TestFSMState.Complete("async1")
            )
        )
    }

    @Test
    fun errorAsyncTest() = runTest(UnconfinedTestDispatcher()) {
        val feature = Feature(TestFSMState.Initial, TestFSMAsyncWorker())
        val states = mutableListOf<TestFSMState>()

        val job = async {
            feature.observeState().take(3).collect {
                states.add(it)
            }
        }

        assertTrue(feature.getCurrentState() == TestFSMState.Initial)

        feature.proceed(Start("error", 1))

        assertTrue(feature.getCurrentState() == TestFSMState.Async("error", 1))

        job.await()

        assertEquals(
            states,
            listOf(
                TestFSMState.Initial,
                TestFSMState.Async("error", 1),
                TestFSMState.Error
            )
        )
    }

    @Test
    fun cancelAsyncTest() = runTest(UnconfinedTestDispatcher()) {
        val feature = Feature(TestFSMState.Initial, TestFSMAsyncWorker())
        val states = mutableListOf<TestFSMState>()

        val job = async {
            feature.observeState().take(5).collect {
                states.add(it)
            }
        }

        assertTrue(feature.getCurrentState() == TestFSMState.Initial)

        feature.proceed(Start("async1", 100))

        assertTrue(feature.getCurrentState() == TestFSMState.Async("async1", 100))

        feature.proceed(Cancel())

        //Task canceled
        assertTrue(feature.getCurrentState() == TestFSMState.Initial)

        //Start new
        feature.proceed(Start("async2", 150))

        assertTrue(feature.getCurrentState() == TestFSMState.Async("async2", 150))

        job.await()

        assertEquals(
            states,
            listOf(
                TestFSMState.Initial,
                TestFSMState.Async("async1", 100),
                TestFSMState.Initial,
                TestFSMState.Async("async2", 150),
                TestFSMState.Complete("async2")
            )
        )
    }
}