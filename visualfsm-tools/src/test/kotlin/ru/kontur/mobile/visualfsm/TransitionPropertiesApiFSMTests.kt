package ru.kontur.mobile.visualfsm

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.testFSMs.transitionPropertiesApi.TransitionPropertiesApiFSMState
import ru.kontur.mobile.visualfsm.testFSMs.transitionPropertiesApi.actions.TransitionPropertiesApiFSMAction
import ru.kontur.mobile.visualfsm.tools.VisualFSM

class TransitionPropertiesApiFSMTests {
    @Test
    fun generateDigraph() {
        println(
            VisualFSM.generateDigraph(
                TransitionPropertiesApiFSMAction::class,
                TransitionPropertiesApiFSMState::class,
                TransitionPropertiesApiFSMState.Initial::class,
            )
        )
        Assertions.assertTrue(true)
    }

    @Test
    fun allStatesReachableTest() {
        val notReachableStates = VisualFSM.getUnreachableStates(
            TransitionPropertiesApiFSMAction::class,
            TransitionPropertiesApiFSMState::class,
            TransitionPropertiesApiFSMState.Initial::class,
        )

        Assertions.assertTrue(
            notReachableStates.size == 1,
            "FSM hah zero states or more than one state: ${notReachableStates.joinToString(", ")}"
        )

        Assertions.assertTrue(
            notReachableStates.first() == TransitionPropertiesApiFSMState.ExtraState::class,
            "Unreachable state should be 'ExtraState'"
        )
    }
}