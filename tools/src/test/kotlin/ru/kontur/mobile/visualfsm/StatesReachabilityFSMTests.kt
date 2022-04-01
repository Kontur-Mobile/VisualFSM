package ru.kontur.mobile.visualfsm

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability.AllStatesReachabilityFSMState
import ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability.AllStatesReachabilityFSMTransition
import ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability.actions.AllStatesReachabilityFSMAction
import ru.kontur.mobile.visualfsm.tools.VisualFSM

class StatesReachabilityFSMTests {
    @Test
    fun generateDigraph() {
        println(
            VisualFSM.generateDigraph(
                AllStatesReachabilityFSMAction::class,
                AllStatesReachabilityFSMTransition::class,
                AllStatesReachabilityFSMState::class,
                AllStatesReachabilityFSMState.Initial::class,
            )
        )
        assertTrue(true)
    }

    @Test
    fun allStatesReachableTest() {
        val notReachableStates = VisualFSM.getUnreachableStates(
            AllStatesReachabilityFSMAction::class,
            AllStatesReachabilityFSMTransition::class,
            AllStatesReachabilityFSMState::class,
            AllStatesReachabilityFSMState.Initial::class,
        )

        assertTrue(
            notReachableStates.isEmpty(),
            "FSM have unreachable states: ${notReachableStates.joinToString(", ")}"
        )
    }
}