package ru.kontur.mobile.visualfsm.tools.internal

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.Edge
import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.tools.internal.PropertyTransitionsUtils.isTransition
import java.util.LinkedList
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

internal object GraphGenerator {

    /**
     * Builds an Edge list
     *
     * @return a list of edges in following order [(initial state, final state, edge name), ...]
     */
    @Suppress("UNCHECKED_CAST")
    fun <STATE : State> getEdgeListGraph(
        baseAction: KClass<out Action<STATE>>,
        useTransitionName: Boolean,
    ): List<Triple<KClass<out STATE>, KClass<out STATE>, String>> {
        val edgeList = mutableListOf<Triple<KClass<out STATE>, KClass<out STATE>, String>>()

        val actions = baseAction.sealedSubclasses

        actions.forEach { actionClass: KClass<out Action<STATE>> ->
            val transactionsClasses =
                actionClass.nestedClasses
                    .filter { it.allSuperclasses.contains(Transition::class) }
                    .map { it as KClass<Transition<out STATE, out STATE>> }

            val transactionsProperties =
                actionClass.memberProperties
                    .filter { it.isTransition() }
                    .map { it as KProperty1<Action<STATE>, Transition<out STATE, out STATE>> }

            transactionsClasses.forEach { transitionKClass ->
                val fromState = transitionKClass.supertypes.first().arguments
                    .first().type?.classifier as KClass<out STATE>
                val toState = transitionKClass.supertypes.first().arguments
                    .last().type?.classifier as KClass<out STATE>

                val edgeName = if (useTransitionName) {
                    getEdgeName(transitionKClass)
                } else {
                    getEdgeNameByActionName(transitionKClass, actionClass)
                }

                edgeList.add(Triple(fromState, toState, edgeName))
            }
            transactionsProperties.forEach { transactionKProperty ->
                val fromState = transactionKProperty.returnType.arguments
                    .first().type?.classifier as KClass<out STATE>
                val toState = transactionKProperty.returnType.arguments
                    .last().type?.classifier as KClass<out STATE>

                val edgeName = if (useTransitionName) {
                    getEdgeName(transactionKProperty)
                } else {
                    getEdgeNameByActionName(transactionKProperty, actionClass)
                }

                edgeList.add(Triple(fromState, toState, edgeName))
            }
        }

        return edgeList
    }

    /**
     * Builds an Adjacency Map of states
     *
     * @return a map of states adjacency in the following form: [(state to [state, ...]), ...]
     */
    @Suppress("UNCHECKED_CAST")
    fun <STATE : State> getAdjacencyMap(
        baseAction: KClass<out Action<STATE>>,
        baseState: KClass<STATE>,
    ): Map<KClass<out STATE>, List<KClass<out STATE>>> {
        val stateNames = HashSet<KClass<out STATE>>()
        val actions = baseAction.sealedSubclasses
        val graph = mutableMapOf<KClass<out STATE>, MutableList<KClass<out STATE>>>()

        populateStateNamesSet(stateNames, baseState)

        graph.putAll(stateNames.map { it to LinkedList() })

        actions.forEach { actionClass: KClass<out Action<STATE>> ->
            val transactionClasses =
                actionClass.nestedClasses.filter { it.allSuperclasses.contains(Transition::class) }

            val transactionProperties =
                actionClass.memberProperties.filter { it.isTransition() }

            transactionClasses.forEach { transitionKClass ->
                val fromState = transitionKClass.supertypes.first().arguments
                    .first().type!!.classifier as KClass<out STATE>
                val toState = transitionKClass.supertypes.first().arguments
                    .last().type!!.classifier as KClass<out STATE>

                graph[fromState]?.add(toState)
            }

            transactionProperties.forEach { transactionK1Property ->
                val fromState = transactionK1Property.returnType.arguments
                    .first().type!!.classifier as KClass<out STATE>
                val toState = transactionK1Property.returnType.arguments
                    .last().type!!.classifier as KClass<out STATE>

                graph[fromState]?.add(toState)
            }
        }

        return graph
    }

    /**
     * Returns the "name" parameter of the [Edge][Edge] annotation if the [transition][Transition] class is annotated with [Edge][Edge],
     * otherwise the simple name of the [transition][Transition] class
     *
     * @param transitionKClass [transition][Transition] class
     * @return edge name for [transition][Transition]
     */
    fun <STATE : State> getEdgeName(transitionKClass: KClass<out Transition<out STATE, out STATE>>): String {
        return transitionKClass.findAnnotation<Edge>()?.name ?: transitionKClass.simpleName!!
    }

    fun <STATE : State> getEdgeName(transitionKProperty: KProperty1<Action<STATE>, Transition<out STATE, out STATE>>): String {
        return transitionKProperty.findAnnotation<Edge>()?.name ?: transitionKProperty.name.toPascalCase()
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
            if (sealedSubclass.sealedSubclasses.isEmpty()) {
                stateNames.add(sealedSubclass)
            } else {
                populateStateNamesSet(stateNames, sealedSubclass)
            }
        }
    }

    /**
     * Returns the "name" parameter of the [Edge][Edge] annotation if the [transition][Transition] class is annotated with [Edge][Edge],
     * otherwise the simple name of the [action][Action] class
     *
     * @param transitionKClass [transition][Transition] class
     * @param actionClass [action][Action] class that contains the [transition][Transition] class
     * @return edge name for [transition][Transition]
     */
    private fun <STATE : State> getEdgeNameByActionName(
        transitionKClass: KClass<out Transition<out STATE, out STATE>>,
        actionClass: KClass<out Action<STATE>>,
    ): String {
        return transitionKClass.findAnnotation<Edge>()?.name ?: actionClass.simpleName!!
    }

    private fun <STATE : State> getEdgeNameByActionName(
        transitionKProperty: KProperty1<Action<STATE>, Transition<out STATE, out STATE>>,
        actionClass: KClass<out Action<STATE>>,
    ): String {
        return transitionKProperty.findAnnotation<Edge>()?.name ?: actionClass.simpleName!!.toPascalCase()
    }

    private fun String.toPascalCase(): String {
        return replaceFirstChar { it.uppercase() }
    }
}