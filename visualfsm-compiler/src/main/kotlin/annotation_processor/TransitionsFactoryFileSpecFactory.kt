package annotation_processor

import annotation_processor.functions.KSClassDeclarationFunctions.getAllNestedSealedSubclasses
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
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

    private fun getTransitionImplementationsForAction(transitions: List<TransitionKSClassDeclarationWrapper>): List<String> {
        val transitionImplementations = mutableListOf<String>()
        transitions.forEach { transition ->
            val fromStates = transition.fromState.getAllNestedSealedSubclasses().ifEmpty { sequenceOf(transition.fromState) }
            fromStates.forEach { fromStateNestedClass ->
                val castString = if (fromStateNestedClass.qualifiedName == transition.fromState.qualifiedName) {
                    ""
                } else {
                    "·as·${KClass::class.asClassName()}<${transition.fromState.qualifiedName!!.asString()}>"
                }
                transitionImplementations.add(
                    buildString {
                        append("··········action.${transition.transitionClassDeclaration.toClassName().simpleName}().apply·{\n")
                        append("··············_fromState·=·${fromStateNestedClass.qualifiedName!!.asString()}::class$castString\n")
                        append("··············_toState·=·${transition.toState.qualifiedName!!.asString()}::class\n")
                        append("··········}")
                    }
                )
            }
        }

        return transitionImplementations
    }
}