package ru.kontur.mobile.visualfsm

import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class TransitionsFunction<T : State>(
    vararg val toStates: KClass<out T>,
)