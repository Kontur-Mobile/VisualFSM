package ru.kontur.mobile.visualfsm

import kotlin.reflect.KClass

abstract class Transition<FROM : State, TO : State>(
    open val fromState: KClass<FROM>,
    open val toState: KClass<TO>,
) {
    open fun predicate(state: FROM): Boolean {
        return true
    }

    abstract fun transform(state: FROM): TO
}