package ru.kontur.mobile.visualfsm

import kotlin.reflect.KClass

annotation class TestFuncAnnotation<STATE : State>(
    vararg val toStates: KClass<out STATE>,
)