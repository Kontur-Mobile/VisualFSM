package ru.kontur.mobile.visualfsm

import kotlin.reflect.KClass

interface Transition2<FROM : State, TO : State> {
    val fromState: KClass<FROM>
    fun transform(state: FROM): TO

    companion object {
        fun <FROM : State, TO : State> getInstance(
            fromState: KClass<FROM>,
            transform: (FROM) -> TO,
        ): Transition2<FROM, TO> {
            return object : Transition2<FROM, TO> {
                override val fromState: KClass<FROM> = fromState
                override fun transform(state: FROM): TO = transform(state)
            }
        }
    }
}