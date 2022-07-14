package annotation_processor

import annotation_processor.functions.KSClassDeclarationFunctions.getCanonicalClassNameAndLink
import annotation_processor.functions.KSClassDeclarationFunctions.isClassOrSubclassOf
import annotation_processor.functions.KSClassDeclarationFunctions.isSubclassOf
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
import ru.kontur.mobile.visualfsm.Feature
import ru.kontur.mobile.visualfsm.GenerateTransitionsFactory
import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.rxjava3.FeatureRx

class AnnotationProcessor(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
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

        val generatedTransitionsFactoryFileSpec = TransitionsFactoryFileSpecFactory().create(
            baseActionClassDeclaration = baseActionClassDeclaration,
            baseStateClassDeclaration = baseStateClassDeclaration,
            className = generatedTransitionsFactoryClassName,
        )

        writeToFile(generatedTransitionsFactoryClassName, featureClassDeclaration.packageName.asString(), generatedTransitionsFactoryFileSpec)
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

        val featureSuperTypeClassDeclarations = featureSuperTypeGenericTypes.mapNotNull {
            it.type?.resolve()?.declaration?.closestClassDeclaration()
        }

        val baseStateClassDeclaration = featureSuperTypeClassDeclarations.firstOrNull { it.isClassOrSubclassOf(State::class) }
            ?: error("Super class of feature must have base state as one of two generic types. The \"${featureClassDeclaration.getCanonicalClassNameAndLink()}\" does not meet this requirement.")

        val baseActionClassDeclaration = featureSuperTypeClassDeclarations.firstOrNull { it.isClassOrSubclassOf(Action::class) }
            ?: error("Super class of feature must have base action as one of two generic types. The \"${featureClassDeclaration.getCanonicalClassNameAndLink()}\" does not meet this requirement.")

        return baseStateClassDeclaration to baseActionClassDeclaration
    }

    private fun writeToFile(className: String, packageName: String, fileSpec: TypeSpec) {
        val fileBuilder = FileSpec.builder(packageName, className)
        val file = fileBuilder.addType(fileSpec).build()
        file.writeTo(codeGenerator, Dependencies(false))
    }

    companion object {
        private val featureClasses = setOf(Feature::class, FeatureRx::class, ru.kontur.mobile.visualfsm.rxjava2.FeatureRx::class)
    }
}