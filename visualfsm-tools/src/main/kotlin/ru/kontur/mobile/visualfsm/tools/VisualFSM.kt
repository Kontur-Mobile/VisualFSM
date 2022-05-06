package ru.kontur.mobile.visualfsm.tools

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.Edge
import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.Transition
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.findAnnotation

object VisualFSM {

    /**
     * Generates a FSM DOT graph for Graphviz
     *
     * @return a DOT graph for Graphviz
     */
    fun <STATE : State> generateDigraph(
        baseActionClass: KClass<out Action<STATE>>,
        baseTransitionClass: KClass<out Transition<out STATE, out STATE>>,
        baseState: KClass<STATE>,
        initialState: KClass<out STATE>,
        useTransitionName: Boolean = true,
    ): String {
        val result = StringBuilder()

        result.appendLine("\ndigraph ${baseState.simpleName}Transitions {")

        result.appendLine("\"${initialState.qualifiedName!!.substringAfterLast("${baseState.simpleName}.")}\"")

        getEdgeListGraph(
            baseActionClass,
            baseTransitionClass,
            useTransitionName
        ).forEach { (fromStateName, toStateName, edgeName) ->
            // Пробел перед названием action'а нужен для аккуратного отображения
            result.appendLine(
                "\"${fromStateName.simpleStateNameWithSealedName(baseState)}\" -> \"${
                    toStateName.simpleStateNameWithSealedName(baseState)
                }\" [label=\" ${edgeName}\"]"
            )
        }

        getUnreachableStates(
            baseActionClass,
            baseTransitionClass,
            baseState,
            initialState
        ).forEach {
            result.appendLine("\"${it.simpleStateNameWithSealedName(baseState)}\" [color=\"red\"]")
        }

        result.appendLine("}\n")

        return result.toString()
    }

    /**
     * Builds an Edge list
     *
     * @return a list of edges in following order [(initial state, final state, edge name), ...]
     */
    @Suppress("UNCHECKED_CAST")
    fun <STATE : State> getEdgeListGraph(
        baseActionClass: KClass<out Action<STATE>>,
        baseTransitionClass: KClass<out Transition<out STATE, out STATE>>,
        useTransitionName: Boolean,
    ): List<Triple<KClass<out STATE>, KClass<out STATE>, String>> {
        val edgeList = mutableListOf<Triple<KClass<out STATE>, KClass<out STATE>, String>>()

        val actions = baseActionClass.sealedSubclasses

        actions.forEach { actionClass: KClass<out Action<STATE>> ->
            val transactions =
                actionClass.nestedClasses.filter { it.allSuperclasses.contains(baseTransitionClass) }

            transactions.forEach { transitionKClass ->
                val fromState = transitionKClass.supertypes.first().arguments
                    .first().type?.classifier as KClass<out STATE>
                val toState = transitionKClass.supertypes.first().arguments
                    .last().type?.classifier as KClass<out STATE>

                val nameFromEdgeAnnotation = transitionKClass.findAnnotation<Edge>()?.name

                val edgeName = when {
                    nameFromEdgeAnnotation != null -> nameFromEdgeAnnotation
                    useTransitionName -> transitionKClass.simpleName
                    else -> actionClass.simpleName
                } ?: throw IllegalStateException("Edge must have name")

                edgeList.add(
                    Triple(
                        fromState,
                        toState,
                        edgeName
                    )
                )
            }
        }

        return edgeList
    }

    /**
     * Checks all the states for reachability
     *
     * @return a list of unreachable states for a disconnected graph, and an empty list for a connected one
     */
    fun <STATE : State> getUnreachableStates(
        baseActionClass: KClass<out Action<STATE>>,
        baseTransitionClass: KClass<out Transition<out STATE, out STATE>>,
        baseState: KClass<STATE>,
        initialState: KClass<out STATE>,
    ): List<KClass<out STATE>> {
        val result = mutableListOf<KClass<out STATE>>()
        val stateToVisited = mutableMapOf<KClass<out STATE>, Boolean>()
        val queue = LinkedList<KClass<out STATE>>()

        val graph = getAdjacencyMap(
            baseActionClass,
            baseTransitionClass,
            baseState,
        )

        val stateNames = graph.keys

        stateToVisited.putAll(stateNames.map { it to false })

        queue.add(initialState)
        stateToVisited[initialState] = true

        while (queue.isNotEmpty()) {
            val node = queue.poll()!!

            val iterator = graph[node]!!.iterator()
            while (iterator.hasNext()) {
                val nextNode = iterator.next()
                if (!stateToVisited[nextNode]!!) {
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
        baseActionClass: KClass<out Action<STATE>>,
        baseTransitionClass: KClass<out Transition<out STATE, out STATE>>,
        baseState: KClass<STATE>,
    ): List<KClass<out STATE>> {
        val finalStates = mutableListOf<KClass<out STATE>>()

        val graph = getAdjacencyMap(
            baseActionClass,
            baseTransitionClass,
            baseState,
        )

        graph.forEach { (startState, destinationStates) ->
            if (destinationStates.isEmpty()) {
                finalStates.add(startState)
            }
        }

        return finalStates
    }

    /**
     * Builds an Adjacency Map of states
     *
     * @return a map of states adjacency in the following form: [(state to [state, ...]), ...]
     */
    private fun <STATE : State> getAdjacencyMap(
        baseActionClass: KClass<out Action<STATE>>,
        baseTransitionClass: KClass<out Transition<out STATE, out STATE>>,
        baseState: KClass<STATE>,
    ): Map<KClass<out STATE>, List<KClass<out STATE>>> {
        val stateNames = HashSet<KClass<out STATE>>()
        val actions = baseActionClass.sealedSubclasses
        val graph = mutableMapOf<KClass<out STATE>, MutableList<KClass<out STATE>>>()

        populateStateNamesSet(stateNames, baseState)

        graph.putAll(stateNames.map { it to LinkedList() })

        actions.forEach { actionClass: KClass<out Action<*>> ->
            val transactions =
                actionClass.nestedClasses.filter { it.allSuperclasses.contains(baseTransitionClass) }

            transactions.forEach { transitionKClass ->
                val fromState = transitionKClass.supertypes.first().arguments
                    .first().type!!.classifier as KClass<out STATE>
                val toState = transitionKClass.supertypes.first().arguments
                    .last().type!!.classifier as KClass<out STATE>

                graph[fromState]?.add(toState)
            }
        }

        return graph
    }

    /**
     * Recursively fills a set with graph states. Every [State] class might not be a state,
     * they could just combine and have those [State] classes as inheritors
     */
    private fun <STATE : State> populateStateNamesSet(
        stateNames: HashSet<KClass<out STATE>>,
        stateClass: KClass<out STATE>,
    ) {
        stateClass.sealedSubclasses.forEach { sealedSubclass ->
            if (sealedSubclass.nestedClasses.isEmpty()) {
                stateNames.add(sealedSubclass)
            } else {
                populateStateNamesSet(stateNames, sealedSubclass)
            }
        }
    }

    private fun <STATE : State> KClass<out STATE>.simpleStateNameWithSealedName(fsmName: KClass<out STATE>): String {
        return this.qualifiedName!!.substringAfterLast("${fsmName.simpleName}.")
    }
}