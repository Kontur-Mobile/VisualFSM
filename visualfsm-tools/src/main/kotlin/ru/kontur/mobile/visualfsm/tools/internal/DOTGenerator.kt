package ru.kontur.mobile.visualfsm.tools.internal

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.State
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
    ): String {
        val result = StringBuilder()

        result.appendLine("\ndigraph ${baseState.simpleName}Transitions {")

        result.appendLine("\"${initialState.qualifiedName!!.substringAfterLast("${baseState.simpleName}.")}\"")

        GraphGenerator.getEdgeListGraph(
            baseAction = baseAction,
            useTransitionName = useTransitionName
        ).forEach { (fromState, toState, edgeName) ->
            val fromStateName = fromState.simpleStateNameWithSealedName(baseState)
            val toStateName = toState.simpleStateNameWithSealedName(baseState)
            // Пробел перед названием action'а нужен для аккуратного отображения
            result.appendLine("\"$fromStateName\" -> \"$toStateName\" [label=\" ${edgeName}\"]")
        }

        GraphAnalyzer.getUnreachableStates(
            baseAction = baseAction,
            baseState = baseState,
            initialState = initialState
        ).forEach { state ->
            val stateName = state.simpleStateNameWithSealedName(baseState)
            result.appendLine("\"$stateName\" [color=\"red\"]")
        }

        result.appendLine("}\n")

        return result.toString()
    }

    private fun <STATE : State> KClass<out STATE>.simpleStateNameWithSealedName(fsmName: KClass<out STATE>): String {
        return this.qualifiedName!!.substringAfterLast("${fsmName.simpleName}.")
    }
}