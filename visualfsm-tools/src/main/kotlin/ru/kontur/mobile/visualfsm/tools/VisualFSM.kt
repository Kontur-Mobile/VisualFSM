package ru.kontur.mobile.visualfsm.tools

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.Edge
import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.tools.graphviz.DotAttributes
import ru.kontur.mobile.visualfsm.tools.internal.DOTGenerator
import ru.kontur.mobile.visualfsm.tools.internal.GraphAnalyzer
import ru.kontur.mobile.visualfsm.tools.internal.GraphGenerator
import kotlin.reflect.KClass

object VisualFSM {

    /**
     * Generates a FSM DOT graph for Graphviz
     *
     * @param baseAction base [action][Action] [class][KClass]
     * @param baseState base [state][State] [class][KClass]
     * @param initialState initial [state][State] [class][KClass]
     * @param attributes DOT language attributes for graph rendering customization
     * @return a DOT graph for Graphviz
     */
    fun <STATE : State> generateDigraph(
        baseAction: KClass<out Action<STATE>>,
        baseState: KClass<STATE>,
        initialState: KClass<out STATE>,
        attributes: DotAttributes<STATE> = DotAttributes(),
    ) = DOTGenerator.generate(baseAction, baseState, initialState, true, attributes)

    /**
     * Generates a FSM DOT graph for Graphviz
     *
     * @param baseAction base [action][Action] [class][KClass]
     * @param baseState base [state][State] [class][KClass]
     * @param initialState initial [state][State] [class][KClass]
     * @param useTransitionName use [transition][Transition] [class][KClass] name or [Edge("")][Edge] annotation for edge name getting
     * @return a DOT graph for Graphviz
     */
    @Deprecated(
        message = "Deprecated, because generate edge name by action class is deprecated.",
        replaceWith = ReplaceWith("generateDigraph function without useTransitionName argument.")
    )
    fun <STATE : State> generateDigraph(
        baseAction: KClass<out Action<STATE>>,
        baseState: KClass<STATE>,
        initialState: KClass<out STATE>,
        useTransitionName: Boolean,
    ) = DOTGenerator.generate(baseAction, baseState, initialState, useTransitionName)

    /**
     * Builds an Edge list
     *
     * @param baseAction base [action][Action] [class][KClass]
     * @return a list of edges in following order [(initial state, final state, edge name), ...]
     */
    fun <STATE : State> getEdgeListGraph(
        baseAction: KClass<out Action<STATE>>,
    ) = GraphGenerator.getEdgeListGraph(baseAction, true)

    /**
     * Builds an Edge list
     *
     * @param baseAction base [action][Action] [class][KClass]
     * @param useTransitionName use [transition][Transition] class name or [Edge("")][Edge] annotation for edge name getting
     * @return a list of edges in following order [(initial state, final state, edge name), ...]
     */
    @Deprecated(
        message = "Deprecated, because generate edge name by action class is deprecated.",
        replaceWith = ReplaceWith("getEdgeListGraph function without useTransitionName argument.")
    )
    fun <STATE : State> getEdgeListGraph(
        baseAction: KClass<out Action<STATE>>,
        useTransitionName: Boolean,
    ) = GraphGenerator.getEdgeListGraph(baseAction, useTransitionName)

    /**
     * Builds an Adjacency Map of states
     *
     * @param baseAction base [action][Action] [class][KClass]
     * @param baseState base [state][State] [class][KClass]
     * @return a map of states adjacency in the following form: [(state to [state, ...]), ...]
     */
    fun <STATE : State> getAdjacencyMap(
        baseAction: KClass<out Action<STATE>>,
        baseState: KClass<STATE>,
    ) = GraphGenerator.getAdjacencyMap(baseAction, baseState)

    /**
     * Returns the "name" parameter of the [Edge][Edge] annotation if the [transition][Transition] class is annotated with [Edge][Edge],
     * otherwise the simple name of the [transition][Transition] class
     *
     * @param transitionClass [transition][Transition] [class][KClass]
     * @return edge name for [transition][Transition]
     */
    fun <STATE : State> getEdgeName(
        transitionClass: KClass<out Transition<out STATE, out STATE>>,
    ) = GraphGenerator.getEdgeName(transitionClass)

    /**
     * Checks all the states for reachability
     *
     * @param baseAction base [action][Action] [class][KClass]
     * @param baseState base [state][State] [class][KClass]
     * @param initialState initial [state][State] [class][KClass]
     * @return a list of unreachable states for a disconnected graph, and an empty list for a connected one
     */
    fun <STATE : State> getUnreachableStates(
        baseAction: KClass<out Action<STATE>>,
        baseState: KClass<STATE>,
        initialState: KClass<out STATE>,
    ) = GraphAnalyzer.getUnreachableStates(baseAction, baseState, initialState)

    /**
     * Builds a list of final states
     *
     * @param baseAction base [action][Action] [class][KClass]
     * @param baseState base [state][State] [class][KClass]
     * @return a list of final states
     */
    fun <STATE : State> getFinalStates(
        baseAction: KClass<out Action<STATE>>,
        baseState: KClass<STATE>,
    ) = GraphAnalyzer.getFinalStates(baseAction, baseState)
}