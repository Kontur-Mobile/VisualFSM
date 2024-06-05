package ru.kontur.mobile.visualfsm.dslTests

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.TransitionCallbacks
import ru.kontur.mobile.visualfsm.dslTests.testFSM.TestDSLFSMFeature
import ru.kontur.mobile.visualfsm.dslTests.testFSM.TestDSLFSMState
import ru.kontur.mobile.visualfsm.dslTests.testFSM.TestDslFSMAsyncWorker
import ru.kontur.mobile.visualfsm.dslTests.testFSM.action.StartLoading
import ru.kontur.mobile.visualfsm.helper.runFSMFeatureTest

class DSLClassInActionTests {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun doSealedFromStateActionTest() = runFSMFeatureTest(
        featureFactory = { dispatcher ->
            TestDSLFSMFeature(
                initialState = TestDSLFSMState.Initial(0),
                asyncWorker = TestDslFSMAsyncWorker(dispatcher),
                transitionCallbacks = object : TransitionCallbacks<TestDSLFSMState> {
                    override fun onActionLaunched(action: Action<TestDSLFSMState>, currentState: TestDSLFSMState) {
                    }

                    override fun onTransitionSelected(
                        action: Action<TestDSLFSMState>,
                        transition: Transition<TestDSLFSMState, TestDSLFSMState>,
                        currentState: TestDSLFSMState,
                    ) {
                    }

                    override fun onNewStateReduced(
                        action: Action<TestDSLFSMState>,
                        transition: Transition<TestDSLFSMState, TestDSLFSMState>,
                        oldState: TestDSLFSMState,
                        newState: TestDSLFSMState,
                    ) {
                    }

                    override fun onNoTransitionError(action: Action<TestDSLFSMState>, currentState: TestDSLFSMState) {
                        throw IllegalStateException("onNoTransitionError $action $currentState")
                    }

                    override fun onMultipleTransitionError(action: Action<TestDSLFSMState>, currentState: TestDSLFSMState) {

                    }
                })

        }
    ) { feature, states ->
        feature.proceed(StartLoading(0))
        advanceUntilIdle()
        assertEquals(
            listOf(
                TestDSLFSMState.Initial(0),
                TestDSLFSMState.AsyncWorkerState.Loading
            ),
            states
        )
    }

//    @Test
//    fun generateDigraphTest() {
//        val digraph = VisualFSM.generateDigraph(
//            baseAction = TestDSLAction::class,
//            baseState = TestDSLFSMState::class,
//            initialState = TestDSLFSMState.Initial::class
//        )
//        println(digraph)
//        assertEquals(
//            "digraph TestDSLFSMStateTransitions {\n" +
//                "\"Initial\" []\n" +
//                "\"NavigationState.DialogState.Hide\" []\n" +
//                "\"NavigationState.DialogState.Show\" []\n" +
//                "\"NavigationState.Screen.Back\" []\n" +
//                "\"NavigationState.Screen.Next\" []\n" +
//                "\"NavigationState.DialogState.Show\" -> \"NavigationState.DialogState.Hide\" [label=\" HideDialog \"]\n" +
//                "\"NavigationState.DialogState.Hide\" -> \"Initial\" [label=\" NavigateCompleted \"]\n" +
//                "\"NavigationState.DialogState.Show\" -> \"Initial\" [label=\" NavigateCompleted \"]\n" +
//                "\"NavigationState.Screen.Back\" -> \"Initial\" [label=\" NavigateCompleted \"]\n" +
//                "\"NavigationState.Screen.Next\" -> \"Initial\" [label=\" NavigateCompleted \"]\n" +
//                "\"Initial\" -> \"NavigationState.Screen.Back\" [label=\" NavigateBack \"]\n" +
//                "\"Initial\" -> \"NavigationState.Screen.Next\" [label=\" NavigateNext \"]\n" +
//                "\"NavigationState.DialogState.Hide\" -> \"NavigationState.DialogState.Hide\" [label=\" ObserveChange \"]\n" +
//                "\"NavigationState.DialogState.Show\" -> \"NavigationState.DialogState.Show\" [label=\" ObserveChange \"]\n" +
//                "\"Initial\" -> \"Initial\" [label=\" ObserveChange \"]\n" +
//                "\"Initial\" -> \"NavigationState.DialogState.Show\" [label=\" ShowDialog \"]\n" +
//                "}\n", digraph
//        )
//    }
}