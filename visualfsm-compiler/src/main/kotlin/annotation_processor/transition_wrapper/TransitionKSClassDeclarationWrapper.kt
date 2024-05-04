package annotation_processor.transition_wrapper

import annotation_processor.functions.KSClassDeclarationFunctions.getCanonicalClassNameAndLink
import annotation_processor.functions.KSClassDeclarationFunctions.isClassOrSubclassOf
import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.innerArguments
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import ru.kontur.mobile.visualfsm.Edge
import ru.kontur.mobile.visualfsm.SelfTransition
import ru.kontur.mobile.visualfsm.Transition
import kotlin.reflect.KClass

data class TransitionKSClassDeclarationWrapper(
    private val transitionClassDeclaration: KSClassDeclaration,
) : TransitionWrapper {

    private val fromStateAndToState by lazy {
        val transitionSuperType = transitionClassDeclaration.superTypes.map { it.resolve() }.first {
            val superClassDeclaration = it.declaration.closestClassDeclaration()
            superClassDeclaration != null && superClassDeclaration.isClassOrSubclassOf(Transition::class)
        }
        val transitionSuperTypeGenericTypes = transitionSuperType.innerArguments
        val classDeclaration = transitionSuperType.declaration.closestClassDeclaration()
        val isSelfTransition = classDeclaration?.isClassOrSubclassOf(SelfTransition::class) == true
        when {
            isSelfTransition && transitionSuperTypeGenericTypes.size != 1 -> {
                val errorMessage = "Super class of self transition must have exactly one generic type. " +
                    "But the super class of \"${transitionClassDeclaration.getCanonicalClassNameAndLink()}\" have ${transitionSuperTypeGenericTypes.size}: ${transitionSuperTypeGenericTypes.map { it.toTypeName() }}"
                error(errorMessage)
            }

            !isSelfTransition && transitionSuperTypeGenericTypes.size != 2 -> {
                val errorMessage = "Super class of transition must have exactly two generic types (fromState and toState). " +
                    "But the super class of \"${transitionClassDeclaration.getCanonicalClassNameAndLink()}\" have ${transitionSuperTypeGenericTypes.size}: ${transitionSuperTypeGenericTypes.map { it.toTypeName() }}"
                error(errorMessage)
            }
        }

        transitionSuperTypeGenericTypes.forEach { transitionSuperTypeGenericType ->
            try {
                transitionSuperTypeGenericType.toTypeName()
            } catch (e: IllegalArgumentException) {
                error("Super class of \"${transitionClassDeclaration.getCanonicalClassNameAndLink()}\" contains generic parameter with invalid class name.")
            }
        }
        if (isSelfTransition) {
            transitionSuperTypeGenericTypes + transitionSuperTypeGenericTypes
        } else {
            transitionSuperTypeGenericTypes
        }
    }
    override val edgeName: String by lazy {
        transitionClassDeclaration
            .annotations
            .firstOrNull { it.shortName.getShortName() == Edge::class.asClassName().simpleName }
            ?.arguments
            ?.firstOrNull { it.name?.getShortName() == "name" }
            ?.value
            ?.toString()
            ?: transitionClassSimpleName
    }

    override val fromState: KSClassDeclaration by lazy { fromStateAndToState[0].type!!.resolve().declaration as KSClassDeclaration }

    override val toState: KSClassDeclaration by lazy { fromStateAndToState[1].type!!.resolve().declaration as KSClassDeclaration }

    override val transitionClassSimpleName: String = transitionClassDeclaration.toClassName().simpleName

    override fun isClassOrSubclassOf(kClass: KClass<out Any>): Boolean {
        return transitionClassDeclaration.isClassOrSubclassOf(kClass)
    }
}