package annotation_processor

import annotation_processor.functions.KSClassDeclarationFunctions.getCanonicalClassNameAndLink
import annotation_processor.functions.KSClassDeclarationFunctions.isClassOrSubclassOf
import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.innerArguments
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ksp.toTypeName
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