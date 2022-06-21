package ru.kontur.mobile.visualfsm.tools

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.TransitionFactory
import kotlin.reflect.full.primaryConstructor


class GeneratedTransactionFactoryProvider {

    @Suppress("UNCHECKED_CAST")
    inline fun <reified STATE : State, ACTION : Action<STATE>> provide(): TransitionFactory<STATE, ACTION> {
        val packageName = STATE::class.qualifiedName?.split(".")?.dropLast(1)?.joinToString(".")
        val implName = "Generated${STATE::class.simpleName!!}TransactionFactory"
        val implQualifiedName = if (packageName.isNullOrBlank()) implName else "${packageName}.${implName}"
        val kClass = Class.forName(implQualifiedName).kotlin
        return kClass.primaryConstructor!!.call() as TransitionFactory<STATE, ACTION>
    }

}