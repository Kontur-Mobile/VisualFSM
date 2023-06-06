package ru.kontur.mobile.visualfsm.tools.graphviz

import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.tools.graphviz.enums.ArrowHead
import ru.kontur.mobile.visualfsm.tools.graphviz.enums.Color
import ru.kontur.mobile.visualfsm.tools.graphviz.enums.NodeShape
import kotlin.reflect.KClass

class DotAttributes<STATE : State>(
    val nodeAttributes: (state: KClass<out STATE>) -> NodeAttributes = { _ -> NodeAttributes() },
    val edgeAttributes: (from: KClass<out STATE>, to: KClass<out STATE>) -> EdgeAttributes = { _, _ -> EdgeAttributes() },
    val graphAttributes: GraphAttributes = GraphAttributes()
)

data class NodeAttributes(
    val shape: NodeShape = NodeShape.Oval,
    val color: Color = Color.Black,
    val fontColor: Color = Color.Black,
    val raw: String = ""
)

data class EdgeAttributes(
    val arrowHead: ArrowHead = ArrowHead.Normal,
    val color: Color = Color.Black,
    val fontColor: Color = Color.Black,
    val raw: String = ""
)

data class GraphAttributes(
    val raw: String = ""
)