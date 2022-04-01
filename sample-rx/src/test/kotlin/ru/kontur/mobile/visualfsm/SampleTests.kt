package ru.kontur.mobile.visualfsm

import demoFSM.DemoFSMState
import demoFSM.DemoFSMTransition
import demoFSM.actions.DemoFSMAction
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.tools.VisualFSM

class SampleTests {

    @Test
    fun generateDigraph() {
        println(
            VisualFSM.generateDigraph(
                DemoFSMAction::class,
                DemoFSMTransition::class,
                DemoFSMState::class,
                DemoFSMState.Initial::class,
            )
        )
        Assertions.assertTrue(true)
    }

    @Test
    fun allStatesReachableTest() {
        val notReachableStates = VisualFSM.getUnreachableStates(
            DemoFSMAction::class,
            DemoFSMTransition::class,
            DemoFSMState::class,
            DemoFSMState.Initial::class,
        )

        Assertions.assertTrue(
            notReachableStates.isEmpty(),
            "FSM have unreachable states: ${notReachableStates.joinToString(", ")}"
        )
    }
}