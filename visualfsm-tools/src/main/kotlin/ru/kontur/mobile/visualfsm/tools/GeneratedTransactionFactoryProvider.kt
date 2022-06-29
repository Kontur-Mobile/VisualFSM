package ru.kontur.mobile.visualfsm.tools

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.Feature
import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.TransitionFactory
import ru.kontur.mobile.visualfsm.rxjava3.FeatureRx
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor


/** Tool object for getting instances of generated [TransitionFactory] implementations */
object GeneratedTransactionFactoryProvider {
    /**
     * Provide an instance of the generated [TransitionFactory] for the given typed [state][State] and [action][Action] parameters.
     * @return an instance of the generated [TransitionFactory]
     */
    @Suppress("UNCHECKED_CAST")
    fun <STATE : State, ACTION : Action<STATE>, FEATURE : Feature<STATE, ACTION>> FEATURE.provide(): TransitionFactory<STATE, ACTION> {
        return getGeneratedTransitionFactory(this::class)
    }

    /**
     * Provide an instance of the generated [TransitionFactory] for the given typed [state][State] and [action][Action] parameters.
     * @return an instance of the generated [TransitionFactory]
     */
    @Suppress("UNCHECKED_CAST")
    fun <STATE : State, ACTION : Action<STATE>, FEATURE : FeatureRx<STATE, ACTION>> FEATURE.provide(): TransitionFactory<STATE, ACTION> {
        return getGeneratedTransitionFactory(this::class)
    }

    /**
     * Provide an instance of the generated [TransitionFactory] for the given typed [state][State] and [action][Action] parameters.
     * @return an instance of the generated [TransitionFactory]
     */
    @Suppress("UNCHECKED_CAST")
    fun <STATE : State, ACTION : Action<STATE>, FEATURE : ru.kontur.mobile.visualfsm.rxjava2.FeatureRx<STATE, ACTION>> FEATURE.provide(): TransitionFactory<STATE, ACTION> {
        return getGeneratedTransitionFactory(this::class)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <STATE : State, ACTION : Action<STATE>> getGeneratedTransitionFactory(featureClass: KClass<out Any>): TransitionFactory<STATE, ACTION> {
        val packageName = featureClass.qualifiedName?.substringBeforeLast(".", "")
        val implName = "Generated${featureClass.simpleName!!}TransactionFactory"
        val implQualifiedName = if (packageName.isNullOrBlank()) implName else "${packageName}.${implName}"
        val kClass = Class.forName(implQualifiedName).kotlin
        return kClass.primaryConstructor!!.call() as TransitionFactory<STATE, ACTION>
    }

}