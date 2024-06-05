package annotation_processor

import annotation_processor.functions.KSClassDeclarationFunctions.getAllNestedSealedSubclasses
import annotation_processor.functions.KSClassDeclarationFunctions.getCanonicalClassNameAndLink
import annotation_processor.functions.KSClassDeclarationFunctions.isClassOrSubclassOf
import annotation_processor.functions.KSClassDeclarationFunctions.isSubclassOf
import annotation_processor.functions.KSFunctionDeclarationFunctions.getCanonicalClassNameAndLink
import annotation_processor.transition_wrapper.TransitionKSClassDeclarationWrapper
import annotation_processor.transition_wrapper.TransitionKSFunctionDeclarationWrapper
import annotation_processor.transition_wrapper.TransitionWrapper
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import ru.kontur.mobile.visualfsm.DslAction
import ru.kontur.mobile.visualfsm.Transition

internal object ActionsWithTransitionsProvider {

    internal fun provide(baseActionClassDeclaration: KSClassDeclaration): Map<KSClassDeclaration, List<TransitionWrapper>> {

        val actionSealedSubclasses = baseActionClassDeclaration.getAllNestedSealedSubclasses()

        if (!actionSealedSubclasses.iterator().hasNext()) {
            error("Base action class must have subclasses. The \"${baseActionClassDeclaration.getCanonicalClassNameAndLink()}\" does not meet this requirement.")
        }

        val isDslAction = baseActionClassDeclaration.isClassOrSubclassOf(DslAction::class)

        if (!isDslAction) {
            actionSealedSubclasses.forEach { actionSealedSubclass ->
                actionSealedSubclass.getDeclaredFunctions().forEach {
                    if (it.modifiers.contains(Modifier.OVERRIDE) && it.simpleName.asString() == "getTransitions") {
                        error("Action must not override getTransitions function. The \"${actionSealedSubclass.getCanonicalClassNameAndLink()}\" does not meet this requirement.")
                    }
                }
            }
        }


        return actionSealedSubclasses.associateWith { actionSealedSubclass ->
            getTransitions(actionClassDeclaration = actionSealedSubclass)
        }
    }

    private fun getTransitions(actionClassDeclaration: KSClassDeclaration): List<TransitionWrapper> {
        val isDslAction = actionClassDeclaration.isClassOrSubclassOf(DslAction::class)

        return if (isDslAction) {
            val transitions = actionClassDeclaration.getDeclaredFunctions()
                .filter { function ->
                    val isTransition = try {
                        val returnTypeDeclaration = (function.returnType?.resolve()?.declaration as? KSClassDeclaration)
                        returnTypeDeclaration?.isClassOrSubclassOf(Transition::class) == true
                    } catch (e: Exception) {
                        error("\"${function.getCanonicalClassNameAndLink()}\" returns a value with an invalid class name.")
                    }
                    isTransition
                }

            if (!transitions.iterator().hasNext()) {
                error("Action must contains transitions as function. The \"${actionClassDeclaration.getCanonicalClassNameAndLink()}\" does not meet this requirement.")
            }

            val wrappedTransitions = transitions.map { transitionFunction ->
                if (Modifier.PRIVATE in transitionFunction.modifiers) {
                    error("Transition must not have \"private\" modifier. The \"${transitionFunction.getCanonicalClassNameAndLink()}\" does not meet this requirement.")
                }

                if (Modifier.PROTECTED in transitionFunction.modifiers) {
                    error("Transition must not have \"protected\" modifier. The \"${transitionFunction.getCanonicalClassNameAndLink()}\" does not meet this requirement.")
                }

                if (Modifier.SUSPEND in transitionFunction.modifiers) {
                    error("Transition must not have \"suspend\" modifier. The \"${transitionFunction.getCanonicalClassNameAndLink()}\" does not meet this requirement.")
                }

                if (transitionFunction.parameters.isNotEmpty()) {
                    error("Transition must not have constructor parameters. The \"${transitionFunction.getCanonicalClassNameAndLink()}\" does not meet this requirement.")
                }

                TransitionKSFunctionDeclarationWrapper(transitionFunction)
            }

            wrappedTransitions.toList()
        } else {
            val transitionClasses = actionClassDeclaration.declarations.filterIsInstance<KSClassDeclaration>().filter {
                it.classKind == ClassKind.CLASS && it.isSubclassOf(Transition::class)
            }

            if (!transitionClasses.iterator().hasNext()) {
                error("Action must contains transitions as inner classes. The \"${actionClassDeclaration.getCanonicalClassNameAndLink()}\" does not meet this requirement.")
            }

            val wrappedTransitions = transitionClasses.map { transitionClass ->
                if (!transitionClass.modifiers.contains(Modifier.INNER)) {
                    error("Transition must have \"inner\" modifier. The \"${transitionClass.getCanonicalClassNameAndLink()}\" does not meet this requirement.")
                }
                if (Modifier.ABSTRACT in transitionClass.modifiers) {
                    error("Transition must not have \"abstract\" modifier. The \"${transitionClass.getCanonicalClassNameAndLink()}\" does not meet this requirement.")
                }
                if (transitionClass.primaryConstructor!!.parameters.isNotEmpty()) {
                    error("Transition must not have constructor parameters. The \"${transitionClass.getCanonicalClassNameAndLink()}\" does not meet this requirement.")
                }

                TransitionKSClassDeclarationWrapper(transitionClass)
            }

            wrappedTransitions.toList()
        }
    }
}