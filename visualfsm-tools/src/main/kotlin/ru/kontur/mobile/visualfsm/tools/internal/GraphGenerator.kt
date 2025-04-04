package ru.kontur.mobile.visualfsm.tools.internal

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.Edge
import ru.kontur.mobile.visualfsm.SelfTransition
import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.tools.internal.KClassFunctions.getAllNestedStateClasses
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf

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
            val transactions = actionClass.nestedClasses
                .filter { it.allSuperclasses.contains(Transition::class) }
                .map { it as KClass<Transition<STATE, STATE>> }

            transactions.forEach { transitionKClass ->
                val genericFromState = transitionKClass.supertypes.first().arguments
                    .first().type?.classifier as KClass<out STATE>
                val genericToState = transitionKClass.supertypes.first().arguments
                    .last().type?.classifier as KClass<out STATE>

                val fromStates = genericFromState.getAllNestedStateClasses()
                val toStates = genericToState.getAllNestedStateClasses()

                val edgeName = if (useTransitionName) {
                    getEdgeName(transitionKClass)
                } else {
                    getEdgeNameByActionName(transitionKClass, actionClass)
                }
                val isSelfTransition = transitionKClass.isSubclassOf(SelfTransition::class)
                fromStates.forEach { fromState ->
                    val filteredToStates = if (isSelfTransition) {
                        toStates.filter { it == fromState }
                    } else {
                        toStates
                    }
                    filteredToStates.forEach { toState ->
                        edgeList.add(Triple(fromState, toState, edgeName))
                    }
                }
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
        val stateClassSet = getStateKClasses(baseState)
        val actions = baseAction.sealedSubclasses
        val graph = mutableMapOf<KClass<out STATE>, MutableList<KClass<out STATE>>>()

        graph.putAll(stateClassSet.map { it to LinkedList() })

        actions.forEach { actionClass: KClass<out Action<STATE>> ->
            val transactions = actionClass.nestedClasses
                .filter { it.allSuperclasses.contains(Transition::class) }
                .map { it as KClass<Transition<STATE, STATE>> }

            transactions.forEach { transitionKClass ->
                val fromStateGeneric = transitionKClass.supertypes.first().arguments
                    .first().type!!.classifier as KClass<out STATE>
                val toStateGeneric = transitionKClass.supertypes.first().arguments
                    .last().type!!.classifier as KClass<out STATE>
                val fromStates = fromStateGeneric.getAllNestedStateClasses()
                val toStates = toStateGeneric.getAllNestedStateClasses()
                val isSelfTransition = transitionKClass.isSubclassOf(SelfTransition::class)
                fromStates.forEach { fromState ->
                    val filteredToStates = if (isSelfTransition) {
                        toStates.filter { it == fromState }
                    } else {
                        toStates
                    }
                    filteredToStates.forEach { toState ->
                        graph[fromState]?.add(toState)
                    }
                }
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
    fun <STATE : State> getEdgeName(transitionKClass: KClass<out Transition<STATE, STATE>>): String {
        return transitionKClass.findAnnotation<Edge>()?.name ?: transitionKClass.simpleName!!
    }

    /**
     * Returns the set of state classes
     *
     * @param baseState base [state][State] class
     * @return the set of state classes
     */
    fun <STATE : State> getStateKClasses(baseState: KClass<out STATE>): Set<KClass<out STATE>> {
        val stateKClassSet = TreeSet<KClass<out STATE>> { kClass1, kClass2 ->
            kClass1.qualifiedName!!.compareTo(
                kClass2.qualifiedName!!
            )
        }
        populateStateKClassSet(stateKClassSet, baseState)
        return stateKClassSet
    }

    /**
     * Recursively fills a set with graph states. Every [State] class might not be a state,
     * they could just combine and have those [State] classes as inheritors
     */
    private fun <STATE : State> populateStateKClassSet(
        stateNames: TreeSet<KClass<out STATE>>,
        stateClass: KClass<out STATE>,
    ) {
        stateClass.sealedSubclasses.forEach { sealedSubclass ->
            if (sealedSubclass.sealedSubclasses.isEmpty()) {
                stateNames.add(sealedSubclass)
            } else {
                populateStateKClassSet(stateNames, sealedSubclass)
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
        transitionKClass: KClass<out Transition<STATE, STATE>>,
        actionClass: KClass<out Action<STATE>>,
    ): String {
        return transitionKClass.findAnnotation<Edge>()?.name ?: actionClass.simpleName!!
    }
}