package ru.kontur.mobile.visualfsm

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.testFSM.TestFSMState
import ru.kontur.mobile.visualfsm.testFSM.action.TestFSMAction
import ru.kontur.mobile.visualfsm.tools.VisualFSM

class StateMachineTests {

    @Test
    fun generateDigraphTest() {
        println(
            VisualFSM.generateDigraph(
                TestFSMAction::class,
                TestFSMState::class,
                TestFSMState.A::class
            )
        )
        Assertions.assertTrue(true)
    }

}