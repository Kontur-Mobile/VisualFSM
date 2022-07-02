package ru.kontur.mobile.visualfsm.tools

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.Feature
import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.TransitionFactory
import ru.kontur.mobile.visualfsm.rxjava3.FeatureRx
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor


/** An instrument object to receive a function that returns an instance of the generated [TransitionFactory] implementation. */
object GeneratedTransactionFactoryFunctionProvider {

    /**
     * Provide a function that returns an instance of the generated [TransitionFactory] for the given [feature][Feature].
     * @return an instance of the generated [TransitionFactory]
     */
    @Suppress("UNCHECKED_CAST")
    fun <STATE : State, ACTION : Action<STATE>> Feature.Companion.provideTransactionFactoryFunction(): Feature<STATE, ACTION>.() -> TransitionFactory<STATE, ACTION> {
        return { getGeneratedTransitionFactory(this::class) }
    }

    /**
     * Provide a function that returns an instance of the generated [TransitionFactory] for the given [feature][FeatureRx].
     * @return an instance of the generated [TransitionFactory]
     */
    @Suppress("UNCHECKED_CAST")
    fun <STATE : State, ACTION : Action<STATE>> FeatureRx.Companion.provideTransactionFactoryFunction(): FeatureRx<STATE, ACTION>.() -> TransitionFactory<STATE, ACTION> {
        return { getGeneratedTransitionFactory(this::class) }
    }

    /**
     * Provide a function that returns an instance of the generated [TransitionFactory] for the given [feature][ru.kontur.mobile.visualfsm.rxjava2.FeatureRx].
     * @return an instance of the generated [TransitionFactory]
     */
    @Suppress("UNCHECKED_CAST")
    fun <STATE : State, ACTION : Action<STATE>> ru.kontur.mobile.visualfsm.rxjava2.FeatureRx.Companion.provideTransactionFactoryFunction(): ru.kontur.mobile.visualfsm.rxjava2.FeatureRx<STATE, ACTION>.() -> TransitionFactory<STATE, ACTION> {
        return { getGeneratedTransitionFactory(this::class) }
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