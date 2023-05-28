package annotation_processor

import annotation_processor.functions.KSClassDeclarationFunctions.getAllNestedSealedSubclasses
import annotation_processor.functions.KSClassDeclarationFunctions.getCanonicalClassNameAndLink
import annotation_processor.functions.KSClassDeclarationFunctions.isSubclassOf
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import ru.kontur.mobile.visualfsm.Transition

internal object ActionsWithTransitionsProvider {

    internal fun provide(baseActionClassDeclaration: KSClassDeclaration): Map<KSClassDeclaration, List<TransitionKSClassDeclarationWrapper>> {

        val actionSealedSubclasses = baseActionClassDeclaration.getAllNestedSealedSubclasses()

        if (!actionSealedSubclasses.iterator().hasNext()) {
            error("Base action class must have subclasses. The \"${baseActionClassDeclaration.getCanonicalClassNameAndLink()}\" does not meet this requirement.")
        }

        actionSealedSubclasses.forEach { actionSealedSubclass ->
            actionSealedSubclass.getDeclaredFunctions().forEach {
                if (it.modifiers.contains(Modifier.OVERRIDE) && it.simpleName.asString() == "getTransitions") {
                    error("Action must not override getTransitions function. The \"${actionSealedSubclass.getCanonicalClassNameAndLink()}\" does not meet this requirement.")
                }
            }
        }

        return actionSealedSubclasses.associateWith { actionSealedSubclass ->
            getTransitions(actionSealedSubclass)
        }
    }

    private fun getTransitions(actionClassDeclaration: KSClassDeclaration): List<TransitionKSClassDeclarationWrapper> {

        val transitionClasses = actionClassDeclaration.declarations.filterIsInstance<KSClassDeclaration>().filter {
            it.classKind == ClassKind.CLASS && it.isSubclassOf(Transition::class)
        }

        if (!transitionClasses.iterator().hasNext()) {
            error("Action must contains transitions as inner classes. The \"${actionClassDeclaration.getCanonicalClassNameAndLink()}\" does not meet this requirement.")
        }

        transitionClasses.forEach { transitionClass ->
            if (!transitionClass.modifiers.contains(Modifier.INNER)) {
                error("Transition must have \"inner\" modifier. The \"${transitionClass.getCanonicalClassNameAndLink()}\" does not meet this requirement.")
            }
            if (Modifier.ABSTRACT in transitionClass.modifiers) {
                error("Transition must not have \"abstract\" modifier. The \"${transitionClass.getCanonicalClassNameAndLink()}\" does not meet this requirement.")
            }
            if (transitionClass.primaryConstructor!!.parameters.isNotEmpty()) {
                error("Transition must not have constructor parameters. The \"${transitionClass.getCanonicalClassNameAndLink()}\" does not meet this requirement.")
            }
        }

        return transitionClasses.toList().map { TransitionKSClassDeclarationWrapper(it) }
    }

}