package ru.kontur.mobile.visualfsm

/**
 * Annotates [Transition] and assign a [name] that will be used on graph
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class Edge(val name: String)