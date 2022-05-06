package ru.kontur.mobile.visualfsm

/**
 * Annotates [Transition] and assign a [name] that will be used on graph
 */
@Target(AnnotationTarget.CLASS)
annotation class Edge(val name: String)