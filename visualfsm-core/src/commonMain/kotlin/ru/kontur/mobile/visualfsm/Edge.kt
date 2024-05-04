package ru.kontur.mobile.visualfsm

/**
 * Annotates [Transition] and assign a [name] that will be used on graph
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Edge(val name: String)