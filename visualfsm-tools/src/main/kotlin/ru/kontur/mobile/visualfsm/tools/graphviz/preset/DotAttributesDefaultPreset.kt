package ru.kontur.mobile.visualfsm.tools.graphviz.preset

import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.tools.graphviz.DotAttributes
import ru.kontur.mobile.visualfsm.tools.graphviz.EdgeAttributes
import ru.kontur.mobile.visualfsm.tools.graphviz.NodeAttributes
import ru.kontur.mobile.visualfsm.tools.graphviz.enums.ArrowHead
import ru.kontur.mobile.visualfsm.tools.graphviz.enums.Color
import ru.kontur.mobile.visualfsm.tools.graphviz.enums.NodeShape
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

/**
 * DOT language attributes preset, use this or create your own preset for your project
 * Displays static states as a black square,
 * states that are handled in AsyncWorker as a blue oval,
 * transitions to states for handling in AsyncWorker as blue arrows with ONormal head,
 * transitions that return the result of AsyncWorker task as dark green arrows with Normal head
 * @param asyncWorkState - base KClass for states handled in AsyncWorker
 */
class DotAttributesDefaultPreset<STATE : State>(
    asyncWorkState: KClass<out STATE>,
) : DotAttributes<STATE>(
    nodeAttributes = { state ->
        when {
            state.isSubclassOf(asyncWorkState) -> {
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
            to.isSubclassOf(asyncWorkState) -> EdgeAttributes(
                arrowHead = ArrowHead.ONormal,
                color = Color.Blue,
                fontColor = Color.Blue,
            )

            from.isSubclassOf(asyncWorkState) -> EdgeAttributes(
                arrowHead = ArrowHead.Normal,
                color = Color.DarkGreen,
                fontColor = Color.DarkGreen,
            )

            else -> EdgeAttributes(
                arrowHead = ArrowHead.Vee,
            )
        }
    }
)