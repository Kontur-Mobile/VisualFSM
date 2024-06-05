package annotation_processor

import annotation_processor.functions.KSClassDeclarationFunctions.getAllNestedSealedSubclasses
import annotation_processor.functions.KSClassDeclarationFunctions.simpleStateNameWithSealedName
import annotation_processor.transition_wrapper.TransitionWrapper
import com.google.devtools.ksp.symbol.KSClassDeclaration
import ru.kontur.mobile.visualfsm.SelfTransition

object AllTransitionsListProvider {

    fun provide(
        baseStateClassDeclaration: KSClassDeclaration,
        transitionWrappers: List<TransitionWrapper>,
    ): String {
        val result = mutableListOf<String>()
        transitionWrappers.forEach { transitionWrapper ->
            val edgeName = transitionWrapper.edgeName
            val fromStates = transitionWrapper.fromState.getAllNestedSealedSubclasses().ifEmpty {
                sequenceOf(transitionWrapper.fromState)
            }
            val toStates = transitionWrapper.toState.getAllNestedSealedSubclasses().ifEmpty {
                sequenceOf(transitionWrapper.toState)
            }
            val isSelfTransition = transitionWrapper.isClassOrSubclassOf(SelfTransition::class)
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