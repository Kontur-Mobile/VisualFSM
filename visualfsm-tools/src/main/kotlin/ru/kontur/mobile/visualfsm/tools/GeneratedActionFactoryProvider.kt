package ru.kontur.mobile.visualfsm.tools

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.GeneratedActionFactory
import kotlin.reflect.full.primaryConstructor

object GeneratedActionFactoryProvider {

    @Suppress("UNCHECKED_CAST")
    inline fun <reified ACTION : Action<*>> provide(): GeneratedActionFactory<ACTION> {
        val packageName: String? = ACTION::class.java.packageName
        val implName = "Generated${ACTION::class.simpleName}Factory"
        val implQualifiedName = if (packageName.isNullOrBlank()) {
            implName
        } else {
            "${packageName}.${implName}"
        }
        val kClass = Class.forName(implQualifiedName).kotlin
        return kClass.primaryConstructor!!.call() as GeneratedActionFactory<ACTION>
    }

}