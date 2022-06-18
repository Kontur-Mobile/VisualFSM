package ru.kontur.mobile.visualfsm.tools

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.TransitionFactory
import kotlin.reflect.full.primaryConstructor

object GeneratedTransactionFactoryProvider {

    @Suppress("UNCHECKED_CAST")
    inline fun <reified STATE : State, ACTION : Action<STATE>> provide(): TransitionFactory<STATE, ACTION> {
        val packageName: String? = STATE::class.java.packageName
        val implName = "Generated${STATE::class.java.simpleName}TransactionFactory"
        val implQualifiedName = if (packageName.isNullOrBlank()) implName else "${packageName}.${implName}"
        val kClass = Class.forName(implQualifiedName).kotlin
        return kClass.primaryConstructor!!.call() as TransitionFactory<STATE, ACTION>
    }

}