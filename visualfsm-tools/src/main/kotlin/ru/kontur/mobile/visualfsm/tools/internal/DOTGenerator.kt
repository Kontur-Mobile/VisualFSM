package ru.kontur.mobile.visualfsm.tools.internal

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.tools.graphviz.enums.Color
import ru.kontur.mobile.visualfsm.tools.graphviz.DotAttributes
import ru.kontur.mobile.visualfsm.tools.graphviz.enums.ArrowHead
import ru.kontur.mobile.visualfsm.tools.graphviz.enums.NodeShape
import kotlin.reflect.KClass

internal object DOTGenerator {

    /**
     * Generates a FSM DOT graph for Graphviz
     *
     * @return a DOT graph for Graphviz
     */
    fun <STATE : State> generate(
        baseAction: KClass<out Action<STATE>>,
        baseState: KClass<STATE>,
        initialState: KClass<out STATE>,
        useTransitionName: Boolean = true,
        attributes: DotAttributes<STATE> = DotAttributes(),
    ): String {
        val result = StringBuilder()

        result.appendLine("digraph ${baseState.simpleName}Transitions {")

        if (attributes.graphAttributes.raw.isNotBlank()) {
            result.append("${attributes.graphAttributes.raw}\n")
        }

        result.append(getStatesWithAttributes(baseAction, baseState, initialState, attributes))

        result.append(getTransitionsWithAttributes(baseAction, baseState, useTransitionName, attributes))

        result.appendLine("}")

        return result.toString()
    }

    private fun <STATE : State> getStatesWithAttributes(
        baseAction: KClass<out Action<STATE>>,
        baseState: KClass<STATE>,
        initialState: KClass<out STATE>,
        attributes: DotAttributes<STATE> = DotAttributes(),
    ): String {
        val result = StringBuilder()

        val stateKClassSetWithoutInitial = GraphGenerator.getStateKClasses(baseState).minus(initialState)
        val unreachableStatesSet = GraphAnalyzer.getUnreachableStatesSet(
            baseAction,
            baseState,
            initialState
        )

        result.append("\"${initialState.simpleStateNameWithSealedName(baseState)}\"")
        result.append(
            " [${
                getAttributesForNode(
                    attributes,
                    initialState,
                    unreachableStatesSet
                )
            }]\n"
        )

        stateKClassSetWithoutInitial.forEach { stateKClass ->
            result.append("\"${stateKClass.simpleStateNameWithSealedName(baseState)}\"")
            result.append(
                " [${
                    getAttributesForNode(
                        attributes,
                        stateKClass,
                        unreachableStatesSet
                    )
                }]\n"
            )
        }

        return result.toString()
    }

    private fun <STATE : State> getTransitionsWithAttributes(
        baseAction: KClass<out Action<STATE>>,
        baseState: KClass<STATE>,
        useTransitionName: Boolean,
        attributes: DotAttributes<STATE> = DotAttributes(),
    ): String {
        val result = StringBuilder()

        GraphGenerator.getEdgeListGraph(
            baseAction,
            useTransitionName
        ).forEach { (fromStateName, toStateName, edgeName) ->
            // A space before and after edgeName is needed to improve rendering
            result.appendLine(
                "\"${fromStateName.simpleStateNameWithSealedName(baseState)}\" -> \"${
                    toStateName.simpleStateNameWithSealedName(baseState)
                }\" [label=\" ${edgeName} \"${getAttributesForEdge(attributes, fromStateName, toStateName)}]"
            )
        }

        return result.toString()
    }

    private fun <STATE : State> getAttributesForNode(
        attributes: DotAttributes<STATE>,
        state: KClass<out STATE>,
        unreachableStatesSet: Set<KClass<out STATE>>
    ): String {
        val nodeAttributes = attributes.nodeAttributes(state)
        val attributesBuilder = StringBuilder()

        val color = if (unreachableStatesSet.contains(state)) {
            Color.Red
        } else {
            nodeAttributes.color
        }

        if (color != Color.Black) {
            attributesBuilder.append(" color=${color.name.lowercase()}")
        }

        if (nodeAttributes.fontColor != Color.Black) {
            attributesBuilder.append(" fontcolor=${nodeAttributes.fontColor.name.lowercase()}")
        }

        if (nodeAttributes.shape != NodeShape.Oval) {
            attributesBuilder.append(" shape=${nodeAttributes.shape.dotString}")
        }

        if (nodeAttributes.raw.isNotBlank()) {
            attributesBuilder.append(" ${nodeAttributes.raw}")
        }

        return attributesBuilder.toString()
    }

    private fun <STATE : State> getAttributesForEdge(
        attributes: DotAttributes<STATE>,
        fromState: KClass<out STATE>,
        toState: KClass<out STATE>,
    ): String {
        val edgeAttributes = attributes.edgeAttributes(fromState, toState)
        val attributesBuilder = StringBuilder()

        if (edgeAttributes.color != Color.Black) {
            attributesBuilder.append(" color=${edgeAttributes.color.name.lowercase()}")
        }

        if (edgeAttributes.fontColor != Color.Black) {
            attributesBuilder.append(" fontcolor=${edgeAttributes.fontColor.name.lowercase()}")
        }

        if (edgeAttributes.arrowHead != ArrowHead.Normal) {
            attributesBuilder.append(" arrowhead=${edgeAttributes.arrowHead.dotString}")
        }

        if (edgeAttributes.raw.isNotBlank()) {
            attributesBuilder.append(" ${edgeAttributes.raw}")
        }

        return attributesBuilder.toString()
    }

    private fun <STATE : State> KClass<out STATE>.simpleStateNameWithSealedName(fsmName: KClass<out STATE>): String {
        return this.qualifiedName!!.substringAfterLast("${fsmName.simpleName}.")
    }
}