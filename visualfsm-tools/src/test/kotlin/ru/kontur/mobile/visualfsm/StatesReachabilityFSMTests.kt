package ru.kontur.mobile.visualfsm

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability.AllStatesReachabilityFSMState
import ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability.actions.AllStatesReachabilityFSMAction
import ru.kontur.mobile.visualfsm.tools.VisualFSM
import ru.kontur.mobile.visualfsm.tools.graphviz.*
import ru.kontur.mobile.visualfsm.tools.graphviz.enums.ArrowHead
import ru.kontur.mobile.visualfsm.tools.graphviz.enums.Color
import ru.kontur.mobile.visualfsm.tools.graphviz.enums.NodeShape
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
                                    color = Color.Blue,
                                    fontColor = Color.Blue
                                )
                            }

                            else -> NodeAttributes(shape = NodeShape.Box)
                        }
                    },

                    edgeAttributes = { from, to ->
                        when {
                            to.isSubclassOf(AllStatesReachabilityFSMState.AsyncWorkState::class) -> EdgeAttributes(
                                arrowHead = ArrowHead.Vee,
                                color = Color.Blue,
                                fontColor = Color.Blue,
                            )

                            from.isSubclassOf(AllStatesReachabilityFSMState.AsyncWorkState::class) -> EdgeAttributes(
                                arrowHead = ArrowHead.Normal,
                                color = Color.DarkGreen,
                                fontColor = Color.DarkGreen,
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