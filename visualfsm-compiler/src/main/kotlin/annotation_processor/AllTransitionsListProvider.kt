package annotation_processor

import annotation_processor.functions.KSClassDeclarationFunctions.getAllNestedSealedSubclasses
import annotation_processor.functions.KSClassDeclarationFunctions.isClassOrSubclassOf
import annotation_processor.functions.KSClassDeclarationFunctions.simpleStateNameWithSealedName
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import ru.kontur.mobile.visualfsm.Edge
import ru.kontur.mobile.visualfsm.SelfTransition

object AllTransitionsListProvider {

    fun provide(
        baseStateClassDeclaration: KSClassDeclaration,
        transitionWrappers: List<TransitionKSClassDeclarationWrapper>,
    ): String {
        val result = mutableListOf<String>()
        transitionWrappers.forEach { transitionWrapper ->
            val edgeName = transitionWrapper.transitionClassDeclaration
                .annotations
                .firstOrNull { it.shortName.getShortName() == Edge::class.asClassName().simpleName }
                ?.arguments
                ?.firstOrNull { it.name?.getShortName() == "name" }
                ?.value
                ?.toString()
                ?: transitionWrapper.transitionClassDeclaration.toClassName().simpleName
            val fromStates = transitionWrapper.fromState.getAllNestedSealedSubclasses().ifEmpty {
                sequenceOf(transitionWrapper.fromState)
            }
            val toStates = transitionWrapper.toState.getAllNestedSealedSubclasses().ifEmpty {
                sequenceOf(transitionWrapper.toState)
            }
            val transitionClassDeclaration = transitionWrapper.transitionClassDeclaration
            val isSelfTransition = transitionClassDeclaration.isClassOrSubclassOf(SelfTransition::class)
            fromStates.forEach { fromStateClass ->
                val fromStateName = fromStateClass.simpleStateNameWithSealedName(baseStateClassDeclaration)
                val filteredToStates = if (isSelfTransition) {
                    toStates.filter { it == fromStateClass }
                } else {
                    toStates
                }
                filteredToStates.forEach { toStateClass ->
                    val toStateName = toStateClass.simpleStateNameWithSealedName(baseStateClassDeclaration)
                    result.add("$edgeName,$fromStateName,$toStateName")
                }
            }
        }
        return result.joinToString("\n")
    }
}