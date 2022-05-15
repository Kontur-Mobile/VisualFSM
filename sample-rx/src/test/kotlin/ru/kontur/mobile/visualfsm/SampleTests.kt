package ru.kontur.mobile.visualfsm

import authFSM.AuthFSMState
import authFSM.AuthFSMTransition
import authFSM.actions.AuthFSMAction
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.tools.VisualFSM

class SampleTests {

    @Test
    fun generateDigraph() {
        println(
            VisualFSM.generateDigraph(
                AuthFSMAction::class,
                AuthFSMTransition::class,
                AuthFSMState::class,
                AuthFSMState.Login::class,
            )
        )
        Assertions.assertTrue(true)
    }

    @Test
    fun allStatesReachableTest() {
        val notReachableStates = VisualFSM.getUnreachableStates(
            AuthFSMAction::class,
            AuthFSMTransition::class,
            AuthFSMState::class,
            AuthFSMState.Login::class,
        )

        Assertions.assertTrue(
            notReachableStates.isEmpty(),
            "FSM have unreachable states: ${notReachableStates.joinToString(", ")}"
        )
    }
}