package ru.kontur.mobile.visualfsm

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.tools.VisualFSM
import ru.kontur.mobile.visualfsm.testFSMs.demoFSM.DemoFSMState
import ru.kontur.mobile.visualfsm.testFSMs.demoFSM.DemoFSMState.*
import ru.kontur.mobile.visualfsm.testFSMs.demoFSM.DemoFSMTransition
import ru.kontur.mobile.visualfsm.testFSMs.demoFSM.actions.DemoFSMAction

class FSMTestUtilsTests {
    companion object {
        private val expectedEdgesWithTransitionName = listOf(
            Triple(AsyncWorkState.DataIn.Saving::class, Error::class, "ErrorSaving"),
            Triple(AsyncWorkState.DataIn.Saving::class, FinalState.DataSent::class, "FinishSaving"),
            Triple(Error::class, Error::class, "RepeatError"),
            Triple(AsyncWorkState.DataOut.Finding::class, FinalState.DataReceived::class, "FinishFinding"),
            Triple(AsyncWorkState.DataOut.Loading::class, FinalState.DataReceived::class, "FinishLoading"),
            Triple(Initial::class, FinalInitialState::class, "NoData"),
            Triple(Initial::class, AsyncWorkState.DataOut.Finding::class, "StartFindData"),
            Triple(Initial::class, AsyncWorkState.DataOut.Loading::class, "StartLoadData"),
            Triple(Initial::class, AsyncWorkState.DataIn.Saving::class, "StartSaveData")
        )

        private val expectedEdgesWithActionName = listOf(
            Triple(AsyncWorkState.DataIn.Saving::class, Error::class, "HandleInData"),
            Triple(AsyncWorkState.DataIn.Saving::class, FinalState.DataSent::class, "HandleInData"),
            Triple(Error::class, Error::class, "HandleInData"),
            Triple(AsyncWorkState.DataOut.Finding::class, FinalState.DataReceived::class, "HandleOutData"),
            Triple(AsyncWorkState.DataOut.Loading::class, FinalState.DataReceived::class, "HandleOutData"),
            Triple(Initial::class, FinalInitialState::class, "SelectInitial"),
            Triple(Initial::class, AsyncWorkState.DataOut.Finding::class, "SelectInitial"),
            Triple(Initial::class, AsyncWorkState.DataOut.Loading::class, "SelectInitial"),
            Triple(Initial::class, AsyncWorkState.DataIn.Saving::class, "SelectInitial")
        )

        private val expectedFinalStates = listOf(FinalState.DataReceived::class, FinalState.DataSent::class, FinalInitialState::class)
    }

    @Test
    fun generateDemoFSMGraph() {
        println(
            VisualFSM.generateDigraph(
                baseActionClass = DemoFSMAction::class,
                baseTransitionClass = DemoFSMTransition::class,
                baseState = DemoFSMState::class,
                initialState = Initial::class,
                true
            )
        )

        Assertions.assertTrue(true)
    }

    @Test
    fun getFinalStatesTest() {
        val finalStates = VisualFSM.getFinalStates(
            baseActionClass = DemoFSMAction::class,
            baseTransitionClass = DemoFSMTransition::class,
            baseState = DemoFSMState::class,
        )

        Assertions.assertTrue(finalStates.containsAll(expectedFinalStates) && finalStates.size == expectedFinalStates.size)
    }

    @Test
    fun getEdgeListGraphTest() {
        val edgeListWithTransitionName = VisualFSM.getEdgeListGraph(
            baseActionClass = DemoFSMAction::class,
            baseTransitionClass = DemoFSMTransition::class,
            useTransitionName = true
        )

        val edgeListWithoutTransitionName = VisualFSM.getEdgeListGraph(
            baseActionClass = DemoFSMAction::class,
            baseTransitionClass = DemoFSMTransition::class,
            useTransitionName = false
        )

        Assertions.assertTrue(
            edgeListWithTransitionName.containsAll(expectedEdgesWithTransitionName) && edgeListWithTransitionName.size == expectedEdgesWithTransitionName.size
        )

        Assertions.assertTrue(
            edgeListWithoutTransitionName.containsAll(expectedEdgesWithActionName) && edgeListWithoutTransitionName.size == expectedEdgesWithActionName.size
        )
    }
}