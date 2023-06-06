package ru.kontur.mobile.visualfsm.tools.graphviz

import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.tools.graphviz.enums.ArrowHead
import ru.kontur.mobile.visualfsm.tools.graphviz.enums.Color
import ru.kontur.mobile.visualfsm.tools.graphviz.enums.NodeShape
import kotlin.reflect.KClass

/**
 * DOT language attributes for the entire graph, nodes and edges rendering customization
 * @property graphAttributes - attributes for the entire graph
 * @property nodeAttributes - function, returns attributes for node
 * @property edgeAttributes - function, returns attributes for edge
 */
class DotAttributes<STATE : State>(
    val graphAttributes: GraphAttributes = GraphAttributes(),
    val nodeAttributes: (state: KClass<out STATE>) -> NodeAttributes = { _ -> NodeAttributes() },
    val edgeAttributes: (from: KClass<out STATE>, to: KClass<out STATE>) -> EdgeAttributes = { _, _ -> EdgeAttributes() },
)

/**
 * DOT language attributes for the entire graph rendering customization
 * @property raw - raw DOT language string for additional attributes
 */
data class GraphAttributes(
    val raw: String = "",
)

/**
 * DOT language attributes for node rendering customization
 * @property shape - node shape
 * @property color - node color
 * @property fontColor - font color
 * @property raw - raw DOT language string for additional attributes
 */
data class NodeAttributes(
    val shape: NodeShape = NodeShape.Oval,
    val color: Color = Color.Black,
    val fontColor: Color = Color.Black,
    val raw: String = "",
)

/**
 * DOT language attributes for edge rendering customization
 * @property arrowHead - arrow head type
 * @property color - node color
 * @property fontColor - font color
 * @property raw - raw DOT language string for additional attributes
 */
data class EdgeAttributes(
    val arrowHead: ArrowHead = ArrowHead.Normal,
    val color: Color = Color.Black,
    val fontColor: Color = Color.Black,
    val raw: String = "",
)