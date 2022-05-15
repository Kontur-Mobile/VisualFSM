package ru.kontur.mobile.visualfsm

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.tools.VisualFSM.generateDigraph
import ru.kontur.mobile.visualfsm.tools.VisualFSM.getUnreachableStates
import ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability.ExtraStateReachabilityFSMState
import ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability.actions.ExtraStateReachabilityFSMAction

class ExtraStateReachabilityFSMTests {
    @Test
    fun generateDigraph() {
        println(
            generateDigraph(
                ExtraStateReachabilityFSMAction::class,
                ExtraStateReachabilityFSMState::class,
                ExtraStateReachabilityFSMState.Initial::class,
            )
        )
        assertTrue(true)
    }

    @Test
    fun allStatesReachableTest() {
        val notReachableStates = getUnreachableStates(
            ExtraStateReachabilityFSMAction::class,
            ExtraStateReachabilityFSMState::class,
            ExtraStateReachabilityFSMState.Initial::class,
        )

        assertTrue(
            notReachableStates.size == 1,
            "FSM hah zero states or more than one state: ${notReachableStates.joinToString(", ")}"
        )

        assertTrue(
            notReachableStates.first() == ExtraStateReachabilityFSMState.ExtraState::class,
            "Unreachable state should be 'ExtraState'"
        )
    }
}