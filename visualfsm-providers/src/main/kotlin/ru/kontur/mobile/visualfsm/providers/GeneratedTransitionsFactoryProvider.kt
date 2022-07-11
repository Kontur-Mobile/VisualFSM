package ru.kontur.mobile.visualfsm.providers

import ru.kontur.mobile.visualfsm.*
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor


/** An instrument object to receive a function that returns an instance of the generated [TransitionsFactory] implementation. */
object GeneratedTransitionsFactoryProvider {

    /**
     * Provide a function that returns an instance of the generated [TransitionsFactory] for the given [feature][Feature].
     * @return an instance of the generated [TransitionsFactory]
     */
    @Suppress("UNCHECKED_CAST")
    fun <STATE : State, ACTION : Action<STATE>> BaseFeature.Companion.provideTransitionsFactory(): BaseFeature<STATE, ACTION>.() -> TransitionsFactory<STATE, ACTION> {
        return { getGeneratedTransitionsFactory(this::class) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <STATE : State, ACTION : Action<STATE>> getGeneratedTransitionsFactory(featureClass: KClass<out Any>): TransitionsFactory<STATE, ACTION> {
        val packageName = featureClass.qualifiedName?.substringBeforeLast(".", "")
        val implName = "Generated${featureClass.simpleName!!}TransitionsFactory"
        val implQualifiedName = if (packageName.isNullOrBlank()) implName else "${packageName}.${implName}"
        val kClass = try {
            Class.forName(implQualifiedName).kotlin
        } catch (e: ClassNotFoundException) {
            error(
                "\nNot found generated TransitionsFactory for ${featureClass.qualifiedName}.\n" +
                        "Code generation not configured or configured incorrectly.\n" +
                        "See the readme file for more information on set up code generation (https://github.com/Kontur-Mobile/VisualFSM/blob/main/docs/eng/Quickstart-ENG.md).\n"
            )
        }
        return kClass.primaryConstructor!!.call() as TransitionsFactory<STATE, ACTION>
    }

}