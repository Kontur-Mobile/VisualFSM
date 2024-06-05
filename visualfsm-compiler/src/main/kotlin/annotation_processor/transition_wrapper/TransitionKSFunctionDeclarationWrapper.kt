package annotation_processor.transition_wrapper

import annotation_processor.functions.KSClassDeclarationFunctions.isClassOrSubclassOf
import annotation_processor.functions.KSFunctionDeclarationFunctions.getCanonicalClassNameAndLink
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.asClassName
import ru.kontur.mobile.visualfsm.Edge
import kotlin.reflect.KClass

data class TransitionKSFunctionDeclarationWrapper(
    private val function: KSFunctionDeclaration,
) : TransitionWrapper {

    private val fromStateAndToState by lazy {
        val returnType = try {
            function.returnType?.resolve() ?: throw IllegalArgumentException()
        } catch (e: IllegalArgumentException) {
            error("Super class of \"${function.getCanonicalClassNameAndLink()}\" contains generic parameter with invalid class name.")
        }
        val arguments = returnType.arguments
        when (arguments.size) {
            2 -> {
                arguments.map {
                    it.type?.resolve()?.declaration as KSClassDeclaration
                }
            }

            1 -> {
                val state = arguments.first().type?.resolve()?.declaration as KSClassDeclaration
                listOf(state, state)
            }

            else -> {
                error("Error arguments.size") // TODO
            }
        }

    }

    override val edgeName: String by lazy {
        function
            .annotations
            .firstOrNull {
                it.shortName.getShortName() == Edge::class.asClassName().simpleName
            }
            ?.arguments
            ?.firstOrNull { it.name?.getShortName() == "name" }
            ?.value
            ?.toString()
            ?: transitionClassSimpleName
    }

    override val transitionClassSimpleName: String = function.simpleName.getShortName()

    override val fromState: KSClassDeclaration by lazy { fromStateAndToState[0] }

    override val toState: KSClassDeclaration by lazy { fromStateAndToState[1] }

    override fun isClassOrSubclassOf(kClass: KClass<out Any>): Boolean {
        return (function.returnType?.resolve()?.declaration as? KSClassDeclaration)?.isClassOrSubclassOf(kClass) == true
    }
}