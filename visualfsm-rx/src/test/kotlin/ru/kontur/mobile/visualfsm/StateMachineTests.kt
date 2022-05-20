package ru.kontur.mobile.visualfsm

import org.junit.jupiter.api.Assertions
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
            baseActionClass = TestFSMAction::class,
            baseState = TestFSMState::class,
            initialState = TestFSMState.A::class
        )

        Assertions.assertEquals(
            "\n" +
                    "digraph TestFSMStateTransitions {\n" +
                    "\"A\"\n" +
                    "\"B\" -> \"A\" [label=\" BtoA\"]\n" +
                    "\"B\" -> \"C\" [label=\" BtoC\"]\n" +
                    "\"B\" -> \"D\" [label=\" BtoD\"]\n" +
                    "\"A\" -> \"B\" [label=\" AtoB\"]\n" +
                    "}\n" +
                    "\n", digraph
        )
    }

    @Test
    fun allStatesReachableTest() {
        val notReachableStates = VisualFSM.getUnreachableStates(
            baseActionClass = TestFSMAction::class,
            baseState = TestFSMState::class,
            initialState = TestFSMState.A::class
        )

        Assertions.assertTrue(
            notReachableStates.isEmpty(),
            "FSM have unreachable states: ${notReachableStates.joinToString(", ")}"
        )
    }

    @Test
    fun oneFinalStateTest() {
        val finalStates = VisualFSM.getFinalStates(
            baseActionClass = TestFSMAction::class,
            baseState = TestFSMState::class,
        )

        Assertions.assertTrue(
            finalStates.size == 2 && finalStates.containsAll(listOf(TestFSMState.C::class, TestFSMState.D::class)),
            "FSM have not correct final states: ${finalStates.joinToString(", ")}"
        )
    }

    @Test
    fun startAsyncTest() {
        val feature = FeatureRx(TestFSMState.A, TestFSMAsyncWorker())

        Assertions.assertTrue(feature.getCurrentState() == TestFSMState.A)

        feature.proceed(Start())

        Assertions.assertTrue(feature.getCurrentState() == TestFSMState.B)
    }

    @Test
    fun cancelAsyncTest() {
        val feature = FeatureRx(TestFSMState.A, TestFSMAsyncWorker())

        Assertions.assertTrue(feature.getCurrentState() == TestFSMState.A)

        feature.proceed(Start())

        Assertions.assertTrue(feature.getCurrentState() == TestFSMState.B)

        feature.proceed(Cancel())

        Assertions.assertTrue(feature.getCurrentState() == TestFSMState.A)
    }
}