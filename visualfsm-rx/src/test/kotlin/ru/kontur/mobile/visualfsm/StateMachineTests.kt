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
            baseActionClass = TestFSMAction::class,
            baseState = TestFSMState::class,
            initialState = TestFSMState.Initial::class
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
        val feature = FeatureRx(TestFSMState.Initial, TestFSMAsyncWorker())

        Assertions.assertTrue(feature.getCurrentState() == TestFSMState.Initial)

        feature.proceed(Start("async1", 1))

        Assertions.assertTrue(feature.getCurrentState() == TestFSMState.Async("async1", 1))
    }

    @Test
    fun endAsyncTest() {
        val feature = FeatureRx(TestFSMState.Initial, TestFSMAsyncWorker())
        val testObserver = feature.observeState().test()

        Assertions.assertTrue(feature.getCurrentState() == TestFSMState.Initial)

        feature.proceed(Start("async1", 1))

        Assertions.assertTrue(feature.getCurrentState() == TestFSMState.Async("async1", 1))

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
        val feature = FeatureRx(TestFSMState.Initial, TestFSMAsyncWorker())
        val testObserver = feature.observeState().test()

        Assertions.assertTrue(feature.getCurrentState() == TestFSMState.Initial)

        feature.proceed(Start("error", 1))

        Assertions.assertTrue(feature.getCurrentState() == TestFSMState.Async("error", 1))

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
        val feature = FeatureRx(TestFSMState.Initial, TestFSMAsyncWorker())
        val testObserver = feature.observeState().test()

        Assertions.assertTrue(feature.getCurrentState() == TestFSMState.Initial)

        feature.proceed(Start("async1", 100))

        Assertions.assertTrue(feature.getCurrentState() == TestFSMState.Async("async1", 100))

        feature.proceed(Cancel())

        //Task canceled
        Assertions.assertTrue(feature.getCurrentState() == TestFSMState.Initial)

        //Start new
        feature.proceed(Start("async2", 150))

        Assertions.assertTrue(feature.getCurrentState() == TestFSMState.Async("async2", 150))


        testObserver.awaitCount(5)
        testObserver.assertValues(
            TestFSMState.Initial,
            TestFSMState.Async("async1", 100),
            TestFSMState.Initial,
            TestFSMState.Async("async2", 150),
            TestFSMState.Complete("async2"))
        testObserver.dispose()
    }
}