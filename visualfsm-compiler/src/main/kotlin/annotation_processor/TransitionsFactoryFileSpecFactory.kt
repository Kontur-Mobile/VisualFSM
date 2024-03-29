package annotation_processor

import annotation_processor.functions.KSClassDeclarationFunctions.getAllNestedSealedSubclasses
import annotation_processor.functions.KSClassDeclarationFunctions.isSubclassOf
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import ru.kontur.mobile.visualfsm.ManyToManySealedTransition
import ru.kontur.mobile.visualfsm.OneToOneSealedTransition
import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.TransitionsFactory
import kotlin.reflect.KClass

internal class TransitionsFactoryFileSpecFactory {

    internal fun create(
        baseActionClassDeclaration: KSClassDeclaration,
        baseStateClassDeclaration: KSClassDeclaration,
        className: String,
        actionsWithTransitions: Map<KSClassDeclaration, List<TransitionKSClassDeclarationWrapper>>,
    ): TypeSpec {
        val classBuilder = TypeSpec.classBuilder(className)

        classBuilder.addSuperinterface(
            TransitionsFactory::class.asClassName().parameterizedBy(
                baseStateClassDeclaration.toClassName(),
                baseActionClassDeclaration.toClassName()
            )
        )

        if (Modifier.INTERNAL in baseActionClassDeclaration.modifiers) {
            classBuilder.addModifiers(KModifier.INTERNAL)
        }

        val createFunctionCodeBuilder = StringBuilder()

        createFunctionCodeBuilder.append("return·when·(action)·{\n")
        actionsWithTransitions.forEach { (actionSubclassDeclaration, transitions) ->
            val transactionImplementations = getTransitionImplementationsForAction(transitions)
            createFunctionCodeBuilder.append("······is·${actionSubclassDeclaration.toClassName()}·->·listOf(\n")
            transactionImplementations.forEach {
                createFunctionCodeBuilder.append("${it},\n")
            }
            createFunctionCodeBuilder.append("······)\n\n")
        }
        createFunctionCodeBuilder.append("······else·->·error(\"Code·generation·error.·Not·all·actions·were·processed·in·the·when·block.\")\n")
        createFunctionCodeBuilder.append("··}")
        classBuilder.addFunction(
            FunSpec.builder("create")
                .addModifiers(KModifier.OVERRIDE)
                .addAnnotation(
                    AnnotationSpec
                        .builder(Suppress::class)
                        .addMember("%S,%S", "REDUNDANT_ELSE_IN_WHEN", "UNCHECKED_CAST")
                        .build()
                )
                .addParameter("action", baseActionClassDeclaration.toClassName())
                .returns(
                    List::class.asClassName().parameterizedBy(
                        Transition::class.asClassName().parameterizedBy(
                            WildcardTypeName.producerOf(
                                baseStateClassDeclaration.toClassName()
                            ),
                            WildcardTypeName.producerOf(
                                baseStateClassDeclaration.toClassName()
                            )
                        )
                    )
                )
                .addStatement(createFunctionCodeBuilder.toString())
                .build()
        )
        return classBuilder.build()
    }

    private fun getTransitionImplementationsForAction(
        transitions: List<TransitionKSClassDeclarationWrapper>,
    ): List<String> {
        val transitionImplementations = mutableListOf<String>()
        transitions.forEach { transition ->
            when {
                transition.transitionClassDeclaration.isSubclassOf(OneToOneSealedTransition::class) -> {
                    val fromStates = transition.fromState.getAllNestedSealedSubclasses().ifEmpty { sequenceOf(transition.fromState) }
                    val toStates = transition.toState.getAllNestedSealedSubclasses().ifEmpty { sequenceOf(transition.toState) }
                    fromStates.forEach { fromStateNestedClass ->
                        val fromStateCastString = if (fromStateNestedClass.qualifiedName == transition.fromState.qualifiedName) {
                            ""
                        } else {
                            "·as·${KClass::class.asClassName()}<${transition.fromState.qualifiedName!!.asString()}>"
                        }
                        toStates.filter { fromStateNestedClass == it }.forEach { toStateNestedClass ->
                            val toStateCastString = if (toStateNestedClass.qualifiedName == transition.toState.qualifiedName) {
                                ""
                            } else {
                                "·as·${KClass::class.asClassName()}<${transition.toState.qualifiedName!!.asString()}>"
                            }
                            transitionImplementations.add(
                                getApplyActionString(
                                    transitionClassDeclaration = transition.transitionClassDeclaration,
                                    fromState = fromStateNestedClass,
                                    toState = toStateNestedClass,
                                    fromCastString = fromStateCastString,
                                    toCastString = toStateCastString
                                )
                            )
                        }
                    }
                }

                transition.transitionClassDeclaration.isSubclassOf(ManyToManySealedTransition::class) -> {
                    val fromStates = transition.fromState.getAllNestedSealedSubclasses().ifEmpty { sequenceOf(transition.fromState) }
                    val toStates = transition.toState.getAllNestedSealedSubclasses().ifEmpty { sequenceOf(transition.toState) }
                    fromStates.forEach { fromStateNestedClass ->
                        val fromStateCastString = if (fromStateNestedClass.qualifiedName == transition.fromState.qualifiedName) {
                            ""
                        } else {
                            "·as·${KClass::class.asClassName()}<${transition.fromState.qualifiedName!!.asString()}>"
                        }
                        toStates.forEach { toStateNestedClass ->
                            val toStateCastString = if (toStateNestedClass.qualifiedName == transition.toState.qualifiedName) {
                                ""
                            } else {
                                "·as·${KClass::class.asClassName()}<${transition.toState.qualifiedName!!.asString()}>"
                            }
                            transitionImplementations.add(
                                getApplyActionString(
                                    transitionClassDeclaration = transition.transitionClassDeclaration,
                                    fromState = fromStateNestedClass,
                                    toState = toStateNestedClass,
                                    fromCastString = fromStateCastString,
                                    toCastString = toStateCastString
                                )
                            )
                        }
                    }
                }

                else -> {
                    transitionImplementations.add(
                        getApplyActionString(
                            transitionClassDeclaration = transition.transitionClassDeclaration,
                            fromState = transition.fromState,
                            toState = transition.toState
                        )
                    )
                }
            }
        }

        return transitionImplementations
    }

    private fun getApplyActionString(
        transitionClassDeclaration: KSClassDeclaration,
        fromState: KSDeclaration,
        toState: KSDeclaration,
        fromCastString: String = "",
        toCastString: String = "",
    ): String {
        return buildString {
            append("··········action.${transitionClassDeclaration.toClassName().simpleName}().apply·{\n")
            append("··············_fromState·=·${fromState.qualifiedName!!.asString()}::class$fromCastString\n")
            append("··············_toState·=·${toState.qualifiedName!!.asString()}::class$toCastString\n")
            append("··········}")
        }
    }
}