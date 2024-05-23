package ru.kontur.mobile.visualfsm.tools.internal

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.State
import java.util.LinkedList
import kotlin.reflect.KClass

internal object GraphAnalyzer {

    /**
     * Checks all the states for reachability
     *
     * @return a list of unreachable states for a disconnected graph, and an empty list for a connected one
     */
    fun <STATE : State> getUnreachableStates(
        baseAction: KClass<out Action<STATE>>,
        baseState: KClass<STATE>,
        initialState: KClass<out STATE>,
    ): List<KClass<out STATE>> {
        return getUnreachableStatesSet(baseAction, baseState, initialState).toList()
    }

    /**
     * Checks all the states for reachability
     *
     * @return a set of unreachable states for a disconnected graph, and an empty set for a connected one
     */
    fun <STATE : State> getUnreachableStatesSet(
        baseAction: KClass<out Action<STATE>>,
        baseState: KClass<STATE>,
        initialState: KClass<out STATE>,
    ): Set<KClass<out STATE>> {
        val result = hashSetOf<KClass<out STATE>>()
        val stateToVisited = mutableMapOf<KClass<out STATE>, Boolean>()
        val queue = LinkedList<KClass<out STATE>>()

        val graph = GraphGenerator.getAdjacencyMap(
            baseAction = baseAction,
            baseState = baseState,
        )

        val stateNames = graph.keys

        stateToVisited.putAll(stateNames.map { it to false })

        queue.add(initialState)
        stateToVisited[initialState] = true

        while (queue.isNotEmpty()) {
            val node = requireNotNull(queue.poll()) { "The queue must not be empty" }

            val iterator = requireNotNull(graph[node]) { "Graph states must not be null" }.iterator()
            while (iterator.hasNext()) {
                val nextNode = iterator.next()
                val isVisitedState = requireNotNull(stateToVisited[nextNode]) { "State on $nextNode is empty" }
                if (!isVisitedState) {
                    stateToVisited[nextNode] = true
                    queue.add(nextNode)
                }
            }
        }

        stateToVisited.forEach { (state, isVisited) ->
            if (!isVisited) {
                result.add(state)
            }
        }

        return result
    }

    /**
     * Builds a list of final states
     *
     * @return a list of final states
     */
    fun <STATE : State> getFinalStates(
        baseAction: KClass<out Action<STATE>>,
        baseState: KClass<STATE>,
    ): List<KClass<out STATE>> {
        val finalStates = mutableListOf<KClass<out STATE>>()

        val graph = GraphGenerator.getAdjacencyMap(
            baseAction,
            baseState,
        )

        graph.forEach { (startState, destinationStates) ->
            if (destinationStates.isEmpty()) {
                finalStates.add(startState)
            }
        }

        return finalStates
    }
}