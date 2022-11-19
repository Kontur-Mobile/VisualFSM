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
            baseAction,
            useTransitionName
        ).forEach { (fromStateName, toStateName, edgeName) ->
            // Пробел перед названием action'а нужен для аккуратного отображения
            result.appendLine(
                "\"${fromStateName.simpleStateNameWithSealedName(baseState)}\" -> \"${
                    toStateName.simpleStateNameWithSealedName(baseState)
                }\" [label=\" ${edgeName}\"]"
            )
        }

        GraphAnalyzer.getUnreachableStates(
            baseAction,
            baseState,
            initialState
        ).forEach {
            result.appendLine("\"${it.simpleStateNameWithSealedName(baseState)}\" [color=\"red\"]")
        }

        result.appendLine("}\n")

        return result.toString()
    }

    private fun <STATE : State> KClass<out STATE>.simpleStateNameWithSealedName(fsmName: KClass<out STATE>): String {
        return this.qualifiedName!!.substringAfterLast("${fsmName.simpleName}.")
    }
}