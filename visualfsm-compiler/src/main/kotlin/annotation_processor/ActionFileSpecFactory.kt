package annotation_processor

import annotation_processor.functions.KSClassDeclarationFunctions.isClassOrSubclassOf
import annotation_processor.functions.KSClassDeclarationFunctions.isSubclassOf
import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.innerArguments
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import ru.kontur.mobile.visualfsm.Transition

internal class ActionFileSpecFactory {

    internal fun create(actionClassDeclaration: KSClassDeclaration, className: String): TypeSpecResult {
        return getTypeSpec(className, actionClassDeclaration)
    }

    @OptIn(KotlinPoetKspPreview::class)
    private fun getTypeSpec(className: String, actionClassDeclaration: KSClassDeclaration): TypeSpecResult {

        val classBuilder = TypeSpec.classBuilder(className)

        if (setOf(Modifier.OPEN, Modifier.ABSTRACT).none { it in actionClassDeclaration.modifiers }) {
            return TypeSpecResult.Error("Action must be abstract or open. The \"${actionClassDeclaration.toClassName().canonicalName}\" does not meet this requirement.")
        }

        classBuilder.superclass(actionClassDeclaration.asType(listOf()).toTypeName())

        val constructor = actionClassDeclaration.primaryConstructor!!

        constructor.parameters.forEach {
            classBuilder.addSuperclassConstructorParameter("%N", it.name!!.asString())
        }

        classBuilder.primaryConstructor(FunSpec.constructorBuilder().apply {
            constructor.parameters.forEach { constructorParameter ->
                addParameter(
                    ParameterSpec.Companion.builder(
                        constructorParameter.name!!.asString(),
                        constructorParameter.type.toTypeName(),
                    ).build()
                )
            }
        }.build())

        val transitionClasses = actionClassDeclaration.declarations.filterIsInstance<KSClassDeclaration>().filter {
            it.classKind == ClassKind.CLASS && it.isSubclassOf(Transition::class)
        }

        if (!transitionClasses.iterator().hasNext()) {
            return TypeSpecResult.Error("Action must contains transitions as inner or nested classes. The \"${actionClassDeclaration.toClassName().canonicalName}\" does not meet this requirement.")
        }

        transitionClasses.forEach { transitionClass ->
            if (Modifier.ABSTRACT in transitionClass.modifiers) {
                return TypeSpecResult.Error("All action transitions must not have \"abstract\" modifier. The \"${transitionClass.toClassName().canonicalName}\" does not meet this requirement.")
            }
        }

        val transitionClassToSuperTypeGenericTypes = mutableMapOf<KSClassDeclaration, List<KSTypeArgument>>()

        transitionClasses.forEach { transitionImplementation ->
            if (transitionImplementation.primaryConstructor!!.parameters.isNotEmpty()) {
                return TypeSpecResult.Error("Transition must not have constructor parameters. The \"${transitionImplementation.toClassName().canonicalName}\" does not meet this requirement.")
            }
            val transitionSuperType = transitionImplementation.superTypes.map { it.resolve() }.first {
                val superClassDeclaration = it.declaration.closestClassDeclaration()
                superClassDeclaration != null && superClassDeclaration.isClassOrSubclassOf(Transition::class)
            }
            val transitionSuperTypeGenericTypes = transitionSuperType.innerArguments
            if (transitionSuperTypeGenericTypes.size != 2) {
                val errorMessage = "Super class of transition must have exactly two generic types (fromState and toState).\n" +
                        "But the super class of \"${transitionImplementation.toClassName().canonicalName}\" have ${transitionSuperTypeGenericTypes.size}: ${transitionSuperTypeGenericTypes.map { it.toTypeName() }}"
                return TypeSpecResult.Error(errorMessage)
            }
            transitionClassToSuperTypeGenericTypes[transitionImplementation] = transitionSuperTypeGenericTypes
        }

        val transitionImplementations = transitionClassToSuperTypeGenericTypes.map { (transitionImplementation, transitionSuperTypeGenericTypes) ->
            val (fromStateType, toStateType) = transitionSuperTypeGenericTypes
            val implementationBuilder = StringBuilder()
            implementationBuilder.append("····${transitionImplementation.toClassName().simpleName}().apply·{\n")
            implementationBuilder.append("········fromState·=·${fromStateType.toTypeName()}::class\n")
            implementationBuilder.append("········toState·=·${toStateType.toTypeName()}::class\n")
            implementationBuilder.append("····}")
            implementationBuilder.toString()
        }

        val getTransitionsFunctionCodeBuilder = StringBuilder()

        getTransitionsFunctionCodeBuilder.append("return·listOf(\n")

        transitionImplementations.forEach {
            getTransitionsFunctionCodeBuilder.append("${it},\n")
        }

        getTransitionsFunctionCodeBuilder.append(")")

        classBuilder.addFunction(
            FunSpec.builder("getTransitions")
                .addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("\"OverridingDeprecatedMember\", \"OVERRIDE_DEPRECATION\"").build())
                .addModifiers(KModifier.OVERRIDE)
                .addCode(getTransitionsFunctionCodeBuilder.toString()).build()
        )

        return TypeSpecResult.Success(classBuilder.build())

    }

}