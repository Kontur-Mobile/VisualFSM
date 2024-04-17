package ru.kontur.mobile.visualfsm.tools.internal

import ru.kontur.mobile.visualfsm.State
import kotlin.reflect.KClass

internal object KClassFunctions {
    internal fun <STATE : State> KClass<STATE>.getAllNestedClasses(): List<KClass<STATE>> {
        val filteredClasses = nestedClasses.filterIsInstance<KClass<STATE>>()
        if (filteredClasses.isEmpty()) {
            return if (this.isCompanion) {
                listOf()
            } else {
                listOf(this)
            }
        }
        return filteredClasses.flatMap { nestedClass ->
            nestedClass.getAllNestedClasses()
        }
    }
}