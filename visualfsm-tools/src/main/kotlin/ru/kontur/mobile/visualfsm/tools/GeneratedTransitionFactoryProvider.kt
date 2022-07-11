package ru.kontur.mobile.visualfsm.tools

import ru.kontur.mobile.visualfsm.*
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor


/** An instrument object to receive a function that returns an instance of the generated [TransitionFactory] implementation. */
object GeneratedTransitionFactoryProvider {

    /**
     * Provide a function that returns an instance of the generated [TransitionFactory] for the given [feature][Feature].
     * @return an instance of the generated [TransitionFactory]
     */
    @Suppress("UNCHECKED_CAST")
    fun <STATE : State, ACTION : Action<STATE>> BaseFeature.Companion.provideTransitionFactory(): BaseFeature<STATE, ACTION>.() -> TransitionFactory<STATE, ACTION> {
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
                "\nNot found generated TransitionFactory for ${featureClass.qualifiedName}.\n" +
                        "Code generation not configured or configured incorrectly.\n" +
                        "See the readme file for more information on set up code generation (https://github.com/Kontur-Mobile/VisualFSM/blob/main/docs/eng/Quickstart-ENG.md).\n"
            )
        }
        return kClass.primaryConstructor!!.call() as TransitionFactory<STATE, ACTION>
    }

}