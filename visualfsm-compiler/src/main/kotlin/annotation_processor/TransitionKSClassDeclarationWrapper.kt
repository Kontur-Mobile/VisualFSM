package annotation_processor

import annotation_processor.functions.KSClassDeclarationFunctions.getCanonicalClassNameAndLink
import annotation_processor.functions.KSClassDeclarationFunctions.isClassOrSubclassOf
import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.innerArguments
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.ksp.toTypeName
import ru.kontur.mobile.visualfsm.ManyToManySealedTransition
import ru.kontur.mobile.visualfsm.OneToOneSealedTransition
import ru.kontur.mobile.visualfsm.Transition

data class TransitionKSClassDeclarationWrapper(val transitionClassDeclaration: KSClassDeclaration) {

    private val fromStateAndToState by lazy {
        val transitionSuperType = transitionClassDeclaration.superTypes.map { it.resolve() }.first {
            val superClassDeclaration = it.declaration.closestClassDeclaration()
            superClassDeclaration != null && superClassDeclaration.isClassOrSubclassOf(Transition::class)
        }
        val transitionSuperTypeGenericTypes = transitionSuperType.innerArguments
        if (transitionSuperTypeGenericTypes.size != 2) {
            val errorMessage = "Super class of transition must have exactly two generic types (fromState and toState). " +
                "But the super class of \"${transitionClassDeclaration.getCanonicalClassNameAndLink()}\" have ${transitionSuperTypeGenericTypes.size}: ${transitionSuperTypeGenericTypes.map { it.toTypeName() }}"
            error(errorMessage)
        }
        val classDeclaration = transitionSuperType.declaration.closestClassDeclaration()
        val sealedGenericCount = transitionSuperTypeGenericTypes.count() { type ->
            val modifiers = type.type?.resolve()?.declaration?.modifiers ?: emptySet()
            Modifier.SEALED in modifiers
        }
        when {
            classDeclaration?.isClassOrSubclassOf(OneToOneSealedTransition::class) == true -> {
                if (sealedGenericCount < 2) {
                    val errorMessage = "When using ${OneToOneSealedTransition::class.simpleName}, you must pass 2 sealed classes to generic types. " +
                        "The \"${transitionClassDeclaration.getCanonicalClassNameAndLink()}\" does not meet this requirement."
                    error(errorMessage)
                }
            }

            classDeclaration?.isClassOrSubclassOf(ManyToManySealedTransition::class) == true -> {
                if (sealedGenericCount == 0) {
                    val errorMessage =
                        "When using ${ManyToManySealedTransition::class.simpleName}, at least one sealed class is required in a generic types. " +
                            "The \"${transitionClassDeclaration.getCanonicalClassNameAndLink()}\" does not meet this requirement."
                    error(errorMessage)
                }
            }

            else -> {
                if (sealedGenericCount > 0) {
                    val errorMessage =
                        "Transition does not process the sealed class in generic. Use ${ManyToManySealedTransition::class.simpleName} or ${OneToOneSealedTransition::class.simpleName}. " +
                            "The \"${transitionClassDeclaration.getCanonicalClassNameAndLink()}\" does not meet this requirement."
                    error(errorMessage)
                }
            }
        }

        transitionSuperTypeGenericTypes.forEach { transitionSuperTypeGenericType ->
            try {
                transitionSuperTypeGenericType.toTypeName()
            } catch (e: IllegalArgumentException) {
                error("Super class of \"${transitionClassDeclaration.getCanonicalClassNameAndLink()}\" contains generic parameter with invalid class name.")
            }
        }
        transitionSuperTypeGenericTypes
    }

    val fromState by lazy { fromStateAndToState[0].type!!.resolve().declaration }

    val toState by lazy { fromStateAndToState[1].type!!.resolve().declaration }
}