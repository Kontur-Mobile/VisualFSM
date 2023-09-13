package annotation_processor

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName
import ru.kontur.mobile.visualfsm.TransitionsFactory

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
                        .addMember("%S", "REDUNDANT_ELSE_IN_WHEN")
                        .build()
                )
                .addParameter("action", baseActionClassDeclaration.toClassName())
                .addStatement(createFunctionCodeBuilder.toString())
                .build()
        )

        return classBuilder.build()
    }

    private fun getTransitionImplementationsForAction(transitions: List<TransitionKSClassDeclarationWrapper>): List<String> {

        val transitionImplementations = transitions.map { transition ->
            val implementationBuilder = StringBuilder()
            implementationBuilder.append("··········action.${transition.transitionClassDeclaration.toClassName().simpleName}().apply·{\n")
            implementationBuilder.append("··············_fromState·=·${transition.fromState.qualifiedName!!.asString()}::class\n")
            implementationBuilder.append("··············_toState·=·${transition.toState.qualifiedName!!.asString()}::class\n")
            implementationBuilder.append("··········}")
            implementationBuilder.toString()
        }

        return transitionImplementations
    }
}