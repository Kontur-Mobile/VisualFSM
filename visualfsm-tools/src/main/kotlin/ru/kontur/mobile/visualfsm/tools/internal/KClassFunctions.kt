package ru.kontur.mobile.visualfsm.tools.internal

import ru.kontur.mobile.visualfsm.State
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

internal object KClassFunctions {
    internal fun <STATE : State> KClass<STATE>.getAllNestedStateClasses(): List<KClass<STATE>> {
        val filteredClasses = nestedClasses.filterIsInstance<KClass<STATE>>().filter { it.isSubclassOf(State::class) }
        if (filteredClasses.isEmpty()) {
            return if (this.isCompanion) {
                listOf()
            } else {
                listOf(this)
            }
        }
        return filteredClasses.flatMap { nestedClass ->
            nestedClass.getAllNestedStateClasses()
        }
    }
}