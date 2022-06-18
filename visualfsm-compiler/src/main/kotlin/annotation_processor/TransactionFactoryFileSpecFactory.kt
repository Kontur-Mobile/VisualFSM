package annotation_processor

import annotation_processor.functions.KSClassDeclarationFunctions.getAllNestedSealedSubclasses
import annotation_processor.functions.KSClassDeclarationFunctions.isClassOrSubclassOf
import annotation_processor.functions.KSClassDeclarationFunctions.isSubclassOf
import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.innerArguments
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.TransitionFactory

class TransactionFactoryFileSpecFactory {

    internal fun create(
        baseActionClassDeclaration: KSClassDeclaration,
        baseStateClassDeclaration: KSClassDeclaration,
        className: String,
    ): TypeSpecResult {
        val classBuilder = TypeSpec.classBuilder(className)

        classBuilder.addSuperinterface(
            TransitionFactory::class.asClassName().parameterizedBy(
                baseStateClassDeclaration.toClassName(),
                baseActionClassDeclaration.asStarProjectedType().toTypeName()
            )
        )

        val actionSealedSubclasses = baseActionClassDeclaration.getAllNestedSealedSubclasses()

        if (!actionSealedSubclasses.iterator().hasNext()) {
            return TypeSpecResult.Error("Base action class must have subclasses")
        }

        val createFunctionCodeBuilder = StringBuilder()

        createFunctionCodeBuilder.append("return·when·(action)·{\n")
        actionSealedSubclasses.forEach { actionSubclassDeclaration ->
            val transactionImplementations =
                when (val getTransitionImplementationsResult = getTransitionImplementationsForAction(actionSubclassDeclaration)) {
                    is TransitionImplementationsResult.Error -> return TypeSpecResult.Error(getTransitionImplementationsResult.message)
                    is TransitionImplementationsResult.Success -> getTransitionImplementationsResult.result
                }
            createFunctionCodeBuilder.append("····is·${actionSubclassDeclaration.toClassName()}·->·listOf(\n")
            transactionImplementations.forEach {
                createFunctionCodeBuilder.append("${it},\n")
            }
            createFunctionCodeBuilder.append("····)\n")
        }
        createFunctionCodeBuilder.append("····else·->·throw·IllegalStateException(\"All·sealed·subclasses·of·base·Action·must·be·handled·in·when\")\n")
        createFunctionCodeBuilder.append("}\n")
        classBuilder.addFunction(
            FunSpec.builder("create")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("action", baseActionClassDeclaration.toClassName())
                .addStatement(createFunctionCodeBuilder.toString())
                .build()
        )

        return TypeSpecResult.Success(classBuilder.build())
    }

    private fun getTransitionImplementationsForAction(actionClassDeclaration: KSClassDeclaration): TransitionImplementationsResult {

        val transitionClasses = actionClassDeclaration.declarations.filterIsInstance<KSClassDeclaration>().filter {
            it.classKind == ClassKind.CLASS && it.modifiers.contains(Modifier.INNER) && it.isSubclassOf(Transition::class)
        }

        if (!transitionClasses.iterator().hasNext()) {
            return TransitionImplementationsResult.Error("Action must contains transitions as inner classes. The \"${actionClassDeclaration.toClassName().canonicalName}\" does not meet this requirement.")
        }

        transitionClasses.forEach { transitionClass ->
            if (Modifier.ABSTRACT in transitionClass.modifiers) {
                return TransitionImplementationsResult.Error("All action transitions must not have \"abstract\" modifier. The \"${transitionClass.toClassName().canonicalName}\" does not meet this requirement.")
            }
        }

        val transitionClassToSuperTypeGenericTypes = mutableMapOf<KSClassDeclaration, List<KSTypeArgument>>()

        transitionClasses.forEach { transitionImplementation ->
            if (transitionImplementation.primaryConstructor!!.parameters.isNotEmpty()) {
                return TransitionImplementationsResult.Error("Transition must not have constructor parameters. The \"${transitionImplementation.toClassName().canonicalName}\" does not meet this requirement.")
            }
            val transitionSuperType = transitionImplementation.superTypes.map { it.resolve() }.first {
                val superClassDeclaration = it.declaration.closestClassDeclaration()
                superClassDeclaration != null && superClassDeclaration.isClassOrSubclassOf(Transition::class)
            }
            val transitionSuperTypeGenericTypes = transitionSuperType.innerArguments
            if (transitionSuperTypeGenericTypes.size != 2) {
                val errorMessage = "Super class of transition must have exactly two generic types (fromState and toState).\n" +
                        "But the super class of \"${transitionImplementation.toClassName().canonicalName}\" have ${transitionSuperTypeGenericTypes.size}: ${transitionSuperTypeGenericTypes.map { it.toTypeName() }}"
                return TransitionImplementationsResult.Error(errorMessage)
            }
            transitionClassToSuperTypeGenericTypes[transitionImplementation] = transitionSuperTypeGenericTypes
        }

        val transitionImplementations = transitionClassToSuperTypeGenericTypes.map { (transitionImplementation, transitionSuperTypeGenericTypes) ->
            val (fromStateType, toStateType) = transitionSuperTypeGenericTypes
            val implementationBuilder = StringBuilder()
            implementationBuilder.append("········action.${transitionImplementation.toClassName().simpleName}().apply·{\n")
            implementationBuilder.append("············fromState·=·${fromStateType.toTypeName()}::class\n")
            implementationBuilder.append("············toState·=·${toStateType.toTypeName()}::class\n")
            implementationBuilder.append("········}")
            implementationBuilder.toString()
        }

        return TransitionImplementationsResult.Success(transitionImplementations)
    }

    private sealed class TransitionImplementationsResult {
        data class Error(val message: String) : TransitionImplementationsResult()
        data class Success(val result: List<String>) : TransitionImplementationsResult()
    }

}