package annotation_processor

import annotation_processor.functions.KSClassDeclarationFunctions.simpleStateNameWithSealedName
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import ru.kontur.mobile.visualfsm.Edge

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
            val fromStateName = transitionWrapper.fromState.simpleStateNameWithSealedName(baseStateClassDeclaration)
            val toStateName = transitionWrapper.toState.simpleStateNameWithSealedName(baseStateClassDeclaration)
            result.add("$edgeName,$fromStateName,$toStateName")
        }
        return result.joinToString("\n")
    }
}