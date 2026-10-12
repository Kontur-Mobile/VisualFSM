package ru.kontur.mobile.visualfsm

import kotlin.reflect.KClass

annotation class ToStates<STATE : State>(
    val toStates: Array<ToState<STATE>>,
)

annotation class ToState<STATE : State>(
    val toState: KClass<out STATE>,
    val transitionName: String,
)

infix fun <STATE : State> KClass<out STATE>.to(transitionName: String): ToState<out STATE> {
    return ToState(this, transitionName)
}