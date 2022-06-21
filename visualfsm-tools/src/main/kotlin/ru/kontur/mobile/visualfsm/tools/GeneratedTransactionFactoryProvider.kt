package ru.kontur.mobile.visualfsm.tools

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.TransitionFactory
import kotlin.reflect.full.primaryConstructor

/** Tool class for getting instances of generated [TransitionFactory] implementations */
class GeneratedTransactionFactoryProvider {

    /**
     * Provide an instance of the generated [TransitionFactory] for the given typed [state][State] and [action][Action] parameters.
     * @return an instance of the generated [TransitionFactory]
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified STATE : State, ACTION : Action<STATE>> provide(): TransitionFactory<STATE, ACTION> {
        val packageName = STATE::class.qualifiedName?.split(".")?.takeIf { it.size > 1 }?.dropLast(1)?.joinToString(".")
        val implName = "Generated${STATE::class.simpleName!!}TransactionFactory"
        val implQualifiedName = if (packageName.isNullOrBlank()) implName else "${packageName}.${implName}"
        val kClass = Class.forName(implQualifiedName).kotlin
        return kClass.primaryConstructor!!.call() as TransitionFactory<STATE, ACTION>
    }

}