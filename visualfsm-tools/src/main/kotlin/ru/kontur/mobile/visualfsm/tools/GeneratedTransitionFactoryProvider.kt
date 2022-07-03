package ru.kontur.mobile.visualfsm.tools

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.Feature
import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.TransitionFactory
import ru.kontur.mobile.visualfsm.rxjava3.FeatureRx
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor


/** An instrument object to receive a function that returns an instance of the generated [TransitionFactory] implementation. */
object GeneratedTransitionFactoryProvider {

    /**
     * Provide a function that returns an instance of the generated [TransitionFactory] for the given [feature][Feature].
     * @return an instance of the generated [TransitionFactory]
     */
    @Suppress("UNCHECKED_CAST")
    fun <STATE : State, ACTION : Action<STATE>> Feature.Companion.provideTransitionFactory(): Feature<STATE, ACTION>.() -> TransitionFactory<STATE, ACTION> {
        return { getGeneratedTransitionFactory(this::class) }
    }

    /**
     * Provide a function that returns an instance of the generated [TransitionFactory] for the given [feature][FeatureRx].
     * @return an instance of the generated [TransitionFactory]
     */
    @Suppress("UNCHECKED_CAST")
    fun <STATE : State, ACTION : Action<STATE>> FeatureRx.Companion.provideTransitionFactory(): FeatureRx<STATE, ACTION>.() -> TransitionFactory<STATE, ACTION> {
        return { getGeneratedTransitionFactory(this::class) }
    }

    /**
     * Provide a function that returns an instance of the generated [TransitionFactory] for the given [feature][ru.kontur.mobile.visualfsm.rxjava2.FeatureRx].
     * @return an instance of the generated [TransitionFactory]
     */
    @Suppress("UNCHECKED_CAST")
    fun <STATE : State, ACTION : Action<STATE>> ru.kontur.mobile.visualfsm.rxjava2.FeatureRx.Companion.provideTransitionFactory(): ru.kontur.mobile.visualfsm.rxjava2.FeatureRx<STATE, ACTION>.() -> TransitionFactory<STATE, ACTION> {
        return { getGeneratedTransitionFactory(this::class) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <STATE : State, ACTION : Action<STATE>> getGeneratedTransitionFactory(featureClass: KClass<out Any>): TransitionFactory<STATE, ACTION> {
        val packageName = featureClass.qualifiedName?.substringBeforeLast(".", "")
        val implName = "Generated${featureClass.simpleName!!}TransitionFactory"
        val implQualifiedName = if (packageName.isNullOrBlank()) implName else "${packageName}.${implName}"
        val kClass = try {
            Class.forName(implQualifiedName).kotlin
        } catch (e: ClassNotFoundException) {
            error(
                "Not found generated TransitionFactory for ${featureClass.qualifiedName}.\n" +
                        "It seems code generation not configured or configured incorrectly.\n" +
                        "For enable code generation:\n" +
                        "  1. Use annotation processor and tools dependencies in module gradle script\n." +
                        "  2. Add generated code to source code directories.\n" +
                        "  3. Annotate the Feature class with the GenerateTransitionFactory annotation.\n" +
                        "  4. Pass the transitionFactory parameter to the Feature constructor.\n" +
                        "Please see the readme file (https://github.com/g0rd1/VisualFSM/blob/g0rd1/code-generation/docs/eng/Quickstart-ENG.md) for detailed information on set up code generation."
            )
        }
        return kClass.primaryConstructor!!.call() as TransitionFactory<STATE, ACTION>
    }

}