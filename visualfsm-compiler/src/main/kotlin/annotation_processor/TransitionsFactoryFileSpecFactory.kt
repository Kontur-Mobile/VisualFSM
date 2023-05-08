package annotation_processor

import annotation_processor.functions.KSClassDeclarationFunctions.getAllNestedSealedSubclasses
import annotation_processor.functions.KSClassDeclarationFunctions.getCanonicalClassNameAndLink
import annotation_processor.functions.KSClassDeclarationFunctions.isClassOrSubclassOf
import annotation_processor.functions.KSClassDeclarationFunctions.isSubclassOf
import annotation_processor.functions.KSPropertyDeclarationFunctions.getCanonicalPropertyNameAndLink
import annotation_processor.functions.KSPropertyDeclarationFunctions.getName
import annotation_processor.functions.KSPropertyDeclarationFunctions.isTypeOfClass
import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.innerArguments
import com.google.devtools.ksp.isPrivate
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.TransitionsFactory

class TransitionsFactoryFileSpecFactory {

    internal fun create(
        baseActionClassDeclaration: KSClassDeclaration,
        baseStateClassDeclaration: KSClassDeclaration,
        className: String,
    ): TypeSpec {
        val classBuilder = TypeSpec.classBuilder(className)

        classBuilder.addSuperinterface(
            TransitionsFactory::class.asClassName().parameterizedBy(
                baseStateClassDeclaration.toClassName(),
                baseActionClassDeclaration.toClassName()
            )
        )

        val actionSealedSubclasses = baseActionClassDeclaration.getAllNestedSealedSubclasses()

        if (!actionSealedSubclasses.iterator().hasNext()) {
            error("Base action class must have subclasses. The \"${baseStateClassDeclaration.getCanonicalClassNameAndLink()}\" does not meet this requirement.")
        }

        actionSealedSubclasses.forEach { actionSealedSubclass ->
            actionSealedSubclass.getDeclaredFunctions().forEach {
                if (it.modifiers.contains(Modifier.OVERRIDE) && it.simpleName.asString() == "getTransitions") {
                    error("Action must not override getTransitions function. The \"${actionSealedSubclass.getCanonicalClassNameAndLink()}\" does not meet this requirement.")
                }
            }
        }

        if (Modifier.INTERNAL in baseActionClassDeclaration.modifiers) {
            classBuilder.addModifiers(KModifier.INTERNAL)
        }

        val createFunctionCodeBuilder = StringBuilder()

        createFunctionCodeBuilder.append("return·when·(action)·{\n")
        actionSealedSubclasses.forEach { actionSubclassDeclaration ->
            val transactionImplementations = getTransitionImplementationsForAction(actionSubclassDeclaration)
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

    private fun getTransitionImplementationsForAction(actionClassDeclaration: KSClassDeclaration): List<String> {

        val transitionClasses = actionClassDeclaration.declarations.filterIsInstance<KSClassDeclaration>().filter {
            it.classKind == ClassKind.CLASS && it.isSubclassOf(Transition::class)
        }

        val transactionProperties = actionClassDeclaration.getAllProperties().filter {
            it.isTypeOfClass(Transition::class)
        }

        if (!transitionClasses.iterator().hasNext() && !transactionProperties.iterator().hasNext()) {
            error("Action must contains transitions as inner classes or as properties. The \"${actionClassDeclaration.getCanonicalClassNameAndLink()}\" does not meet this requirement.")
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

        transactionProperties.forEach { transactionProperty ->
            if (transactionProperty.isPrivate()) {
                error("Transition must not be private. The \"${transactionProperty.getCanonicalPropertyNameAndLink()}\" does not meet this requirement.")
            }
        }

        val transitionClassToSuperTypeGenericTypes = transitionClasses.associateWith { transitionClass ->
            val transitionSuperType = transitionClass.superTypes.map { it.resolve() }.first {
                val superClassDeclaration = it.declaration.closestClassDeclaration()
                superClassDeclaration != null && superClassDeclaration.isClassOrSubclassOf(Transition::class)
            }
            val transitionSuperTypeGenericTypes = transitionSuperType.innerArguments
            if (transitionSuperTypeGenericTypes.size != 2) {
                val errorMessage = "Super class of transition must have exactly two generic types (fromState and toState). " +
                        "But the super class of \"${transitionClass.getCanonicalClassNameAndLink()}\" have ${transitionSuperTypeGenericTypes.size}: ${transitionSuperTypeGenericTypes.map { it.toTypeName() }}"
                error(errorMessage)
            }
            transitionSuperTypeGenericTypes.forEach { transitionSuperTypeGenericType ->
                try {
                    transitionSuperTypeGenericType.toTypeName()
                } catch (e: IllegalArgumentException) {
                    error("Super class of \"${transitionClass.getCanonicalClassNameAndLink()}\" contains generic parameter with invalid class name.")
                }
            }
            transitionSuperTypeGenericTypes
        }

        val transitionPropertiesToGenericTypes = transactionProperties.associateWith { transactionProperty ->
            val transitionType = transactionProperty.type.resolve()

            val transitionTypeGenericTypes = transitionType.innerArguments
            if (transitionTypeGenericTypes.size != 2) {
                val errorMessage = "Property type of transition must have exactly two generic types (fromState and toState). " +
                        "But the property type of \"${transactionProperty.getCanonicalPropertyNameAndLink()}\" have ${transitionTypeGenericTypes.size}: ${transitionTypeGenericTypes.map { it.toTypeName() }}"
                error(errorMessage)
            }
            transitionTypeGenericTypes.forEach { transactionPropertyGenericTypeGenericType ->
                try {
                    transactionPropertyGenericTypeGenericType.toTypeName()
                } catch (e: IllegalArgumentException) {
                    error("Property type of \"${transactionProperty.getCanonicalPropertyNameAndLink()}\" contains generic parameter with invalid class name.")
                }
            }
            transitionTypeGenericTypes
        }

        val transitionClassImplementations = transitionClassToSuperTypeGenericTypes.map { (transitionImplementation, transitionSuperTypeGenericTypes) ->
            val (fromStateType, toStateType) = transitionSuperTypeGenericTypes
            buildString {
                append("··········action.${transitionImplementation.toClassName().simpleName}().apply·{\n")
                append("··············_fromState·=·${fromStateType.toTypeName()}::class\n")
                append("··············_toState·=·${toStateType.toTypeName()}::class\n")
                append("··········}")
            }
        }
        val transactionPropertiesImplementations = transitionPropertiesToGenericTypes.map { (transitionImplementation, transactionPropertyGenericTypes) ->
            val (fromStateType, toStateType) = transactionPropertyGenericTypes
            buildString {
                append("··········action.${transitionImplementation.getName()}.apply·{\n")
                append("··············_fromState·=·${fromStateType.toTypeName()}::class\n")
                append("··············_toState·=·${toStateType.toTypeName()}::class\n")
                append("··········}")
            }
        }

        return transitionClassImplementations + transactionPropertiesImplementations
    }
}