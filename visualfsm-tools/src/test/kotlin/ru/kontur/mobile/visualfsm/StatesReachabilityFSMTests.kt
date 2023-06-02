package ru.kontur.mobile.visualfsm

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability.AllStatesReachabilityFSMState
import ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability.actions.AllStatesReachabilityFSMAction
import ru.kontur.mobile.visualfsm.tools.VisualFSM
import ru.kontur.mobile.visualfsm.tools.data.*
import kotlin.reflect.full.isSubclassOf

class StatesReachabilityFSMTests {
    @Test
    fun generateDigraph() {
        println(
            VisualFSM.generateDigraph(
                AllStatesReachabilityFSMAction::class,
                AllStatesReachabilityFSMState::class,
                AllStatesReachabilityFSMState.Initial::class,
                attributes = DotAttributes(
                    nodeAttributes = { state ->
                        when {
                            state.isSubclassOf(AllStatesReachabilityFSMState.AsyncWorkState::class) -> {
                                NodeAttributes(
                                    shape = NodeShape.Oval,
                                    color = Color.Blue
                                )
                            }

                            else -> NodeAttributes(shape = NodeShape.Box)
                        }
                    },

                    edgeAttributes = { from, to ->
                        when (to) {
                            is AllStatesReachabilityFSMState.AsyncWorkState -> EdgeAttributes(
                                color = Color.Blue
                            )

                            else -> EdgeAttributes()
                        }
                    }
                )
            )
        )
        assertTrue(true)
    }

    @Test
    fun allStatesReachableTest() {
        val notReachableStates = VisualFSM.getUnreachableStates(
            AllStatesReachabilityFSMAction::class,
            AllStatesReachabilityFSMState::class,
            AllStatesReachabilityFSMState.Initial::class,
        )

        assertTrue(
            notReachableStates.isEmpty(),
            "FSM have unreachable states: ${notReachableStates.joinToString(", ")}"
        )
    }
}