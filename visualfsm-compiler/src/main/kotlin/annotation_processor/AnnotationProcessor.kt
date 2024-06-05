package annotation_processor

import annotation_processor.functions.KSClassDeclarationFunctions.getCanonicalClassNameAndLink
import annotation_processor.functions.KSClassDeclarationFunctions.isClassOrSubclassOf
import annotation_processor.functions.KSClassDeclarationFunctions.isSubclassOf
import annotation_processor.transition_wrapper.TransitionWrapper
import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.innerArguments
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.DslAction
import ru.kontur.mobile.visualfsm.Feature
import ru.kontur.mobile.visualfsm.GenerateTransitionsFactory
import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.rxjava3.FeatureRx

class AnnotationProcessor(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
    private val options: Map<String, String>,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val annotatedWithFeatureClassDeclarations = resolver
            .getSymbolsWithAnnotation(GenerateTransitionsFactory::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()

        if (!annotatedWithFeatureClassDeclarations.iterator().hasNext()) return emptyList()

        try {
            annotatedWithFeatureClassDeclarations.forEach {
                handleAnnotatedWithFeatureClassDeclaration(it)
            }
        } catch (t: Throwable) {
            logger.error(t.message ?: "Unknown error:\n${t.stackTraceToString()}")
        }

        return emptyList()
    }

    private fun handleAnnotatedWithFeatureClassDeclaration(featureClassDeclaration: KSClassDeclaration) {

        if (featureClasses.none { featureClassDeclaration.isSubclassOf(it) }) {
            logger.error("Only class inherited from ${featureClasses.joinToString(" or ")} can be annotated with @${GenerateTransitionsFactory::class.qualifiedName!!}. The \"${featureClassDeclaration.getCanonicalClassNameAndLink()}\" does not meet this requirement.")
            return
        }

        val (baseStateClassDeclaration, baseActionClassDeclaration) = getBaseStateAndBaseActionClassDeclaration(featureClassDeclaration)

        if (Modifier.SEALED !in baseActionClassDeclaration.modifiers) {
            logger.error("Base Action class must be sealed. The \"${baseActionClassDeclaration.getCanonicalClassNameAndLink()}\" does not meet this requirement.")
            return
        }

        val generatedTransitionsFactoryClassName = "Generated${featureClassDeclaration.toClassName().simpleName}TransitionsFactory"

        val actionsWithTransitions = ActionsWithTransitionsProvider.provide(baseActionClassDeclaration)

        val generatedTransitionsFactoryFileSpec = TransitionsFactoryFileSpecFactory().create(
            baseActionClassDeclaration = baseActionClassDeclaration,
            baseStateClassDeclaration = baseStateClassDeclaration,
            className = generatedTransitionsFactoryClassName,
            actionsWithTransitions = actionsWithTransitions,
        )

        writeToFile(generatedTransitionsFactoryClassName, featureClassDeclaration.packageName.asString(), generatedTransitionsFactoryFileSpec)

        if (options["generateAllTransitionsCsvFiles"] == "true") {
            val packageName = featureClassDeclaration.packageName.asString()
            writeAllTransitionsFile(packageName, baseStateClassDeclaration, actionsWithTransitions)
        }
    }

    private fun getBaseStateAndBaseActionClassDeclaration(featureClassDeclaration: KSClassDeclaration): Pair<KSClassDeclaration, KSClassDeclaration> {

        val featureSuperType = featureClassDeclaration.superTypes.map { it.resolve() }.first { superType ->
            val superClassDeclaration = superType.declaration.closestClassDeclaration()
            superClassDeclaration != null && featureClasses.any { superClassDeclaration.isClassOrSubclassOf(it) }
        }

        val featureSuperTypeGenericTypes = featureSuperType.innerArguments

        if (featureSuperTypeGenericTypes.size != 2) {
            val errorMessage = "Super class of feature must have exactly two generic types (state and action). " +
                    "But the super class of \"${featureClassDeclaration.getCanonicalClassNameAndLink()}\" has ${featureSuperTypeGenericTypes.size}: ${featureSuperTypeGenericTypes.map { it.toTypeName() }}"
            error(errorMessage)
        }

        featureSuperTypeGenericTypes.forEach { featureSuperTypeGenericType ->
            try {
                featureSuperTypeGenericType.toTypeName()
            } catch (e: IllegalArgumentException) {
                error("Super class of \"${featureClassDeclaration.getCanonicalClassNameAndLink()}\" contains generic parameter with invalid class name.")
            }
        }

        val featureSuperTypeClassDeclarations = featureSuperTypeGenericTypes.mapNotNull {
            it.type?.resolve()?.declaration?.closestClassDeclaration()
        }

        val baseStateClassDeclaration = featureSuperTypeClassDeclarations.firstOrNull { it.isClassOrSubclassOf(State::class) }
            ?: error("Super class of feature must have base state as one of two generic types. The \"${featureClassDeclaration.getCanonicalClassNameAndLink()}\" does not meet this requirement.")

        val baseActionClassDeclaration = featureSuperTypeClassDeclarations.firstOrNull {
            it.isClassOrSubclassOf(Action::class) || it.isClassOrSubclassOf(DslAction::class)
        }
            ?: error("Super class of feature must have base action as one of two generic types. The \"${featureClassDeclaration.getCanonicalClassNameAndLink()}\" does not meet this requirement.")

        return baseStateClassDeclaration to baseActionClassDeclaration
    }

    private fun writeAllTransitionsFile(
        packageName: String,
        baseStateClassDeclaration: KSClassDeclaration,
        actionsWithTransitions: Map<KSClassDeclaration, List<TransitionWrapper>>,
    ) {

        val stream = codeGenerator.createNewFile(
            dependencies = Dependencies(false),
            packageName = packageName,
            fileName = "${baseStateClassDeclaration.simpleName.asString()}AllTransitions",
            extensionName = "csv"
        )

        val allTransitionsList = AllTransitionsListProvider.provide(baseStateClassDeclaration, actionsWithTransitions.values.flatten())

        stream.bufferedWriter().use {
            it.write(allTransitionsList)
        }
    }

    private fun writeToFile(className: String, packageName: String, fileSpec: TypeSpec) {
        val fileBuilder = FileSpec.builder(packageName, className)
        val file = fileBuilder.addType(fileSpec).build()
        file.writeTo(codeGenerator, Dependencies(false))
    }

    companion object {
        private val featureClasses = setOf(Feature::class, FeatureRx::class)
    }
}