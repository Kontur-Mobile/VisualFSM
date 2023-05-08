package ru.kontur.mobile.visualfsm.tools.internal

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.Transition
import kotlin.reflect.KProperty1
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType

internal object PropertyTransitionsUtils {

    fun <STATE : State> KProperty1<out Action<STATE>, *>.isTransition(): Boolean {
        val transitionType = Transition::class.createType(
            arguments = listOf(
                KTypeProjection.covariant(State::class.starProjectedType),
                KTypeProjection.covariant(State::class.starProjectedType)
            )
        )
        return this.returnType.isSubtypeOf(other = transitionType)
    }
}