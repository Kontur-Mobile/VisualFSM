package annotation_processor

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
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.Feature
import ru.kontur.mobile.visualfsm.ProceedsGeneratedActions
import ru.kontur.mobile.visualfsm.rxjava3.FeatureRx

class AnnotationProcessor(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val annotatedWithFeatureClassDeclarations = resolver
            .getSymbolsWithAnnotation(ProceedsGeneratedActions::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()

        if (!annotatedWithFeatureClassDeclarations.iterator().hasNext()) return emptyList()

        annotatedWithFeatureClassDeclarations.forEach {
            handleAnnotatedWithFeatureClassDeclaration(it)
        }

        return emptyList()
    }

    @OptIn(KotlinPoetKspPreview::class)
    private fun handleAnnotatedWithFeatureClassDeclaration(featureClassDeclaration: KSClassDeclaration) {

        val baseActionClassDeclaration = when (val baseActionClassDeclarationResult = getBaseActionClassDeclaration(featureClassDeclaration)) {
            is KSClassDeclarationResult.Error -> {
                logger.error(baseActionClassDeclarationResult.message)
                return
            }
            is KSClassDeclarationResult.Success -> baseActionClassDeclarationResult.classDeclaration
        }

        val generatedActionFactoryClassName = "Generated${baseActionClassDeclaration.toClassName().simpleName}Factory"

        val generatedActionFactoryFileSpecResult = GeneratedActionFactoryFileSpecFactory().create(
            baseActionClassDeclaration,
            generatedActionFactoryClassName,
        )

        val generatedActionFactoryFileSpec = when (generatedActionFactoryFileSpecResult) {
            is TypeSpecResult.Error -> {
                logger.error(generatedActionFactoryFileSpecResult.message)
                return
            }
            is TypeSpecResult.Success -> generatedActionFactoryFileSpecResult.typeSpec
        }

        writeToFile(generatedActionFactoryClassName, baseActionClassDeclaration.packageName.asString(), generatedActionFactoryFileSpec)

        val actionFileSpecFactory = ActionFileSpecFactory()

        baseActionClassDeclaration.getSealedSubclasses().forEach {
            val actionClassName = "${it.toClassName().simpleName}Impl"
            val actionFileSpec = when (val actionFileSpecResult = actionFileSpecFactory.create(it, actionClassName)) {
                is TypeSpecResult.Error -> {
                    logger.error(actionFileSpecResult.message)
                    return
                }
                is TypeSpecResult.Success -> actionFileSpecResult.typeSpec
            }
            writeToFile(actionClassName, it.packageName.asString(), actionFileSpec)
        }

    }

    @OptIn(KotlinPoetKspPreview::class)
    private fun getBaseActionClassDeclaration(featureClassDeclaration: KSClassDeclaration): KSClassDeclarationResult {

        val featureClasses = setOf(Feature::class, FeatureRx::class, ru.kontur.mobile.visualfsm.rxjava2.FeatureRx::class)

        if (featureClasses.none { featureClassDeclaration.isSubclassOf(it) }) {
            return KSClassDeclarationResult.Error("Only class inherited from ${featureClasses.joinToString(" or ")} can be annotated with @${ProceedsGeneratedActions::class.qualifiedName!!}. The \"${featureClassDeclaration.toClassName().canonicalName}\" does not meet this requirement.")
        }

        val featureSuperType = featureClassDeclaration.superTypes.map { it.resolve() }.first { superType ->
            val superClassDeclaration = superType.declaration.closestClassDeclaration()
            superClassDeclaration != null && featureClasses.any { superClassDeclaration.isClassOrSubclassOf(it) }
        }

        val featureSuperTypeGenericTypes = featureSuperType.innerArguments

        if (featureSuperTypeGenericTypes.size != 2) {
            val errorMessage = "Super class of feature must have exactly two generic types (state and action).\n" +
                    "But the super class of \"${featureClassDeclaration.toClassName().canonicalName}\" has ${featureSuperTypeGenericTypes.size}: ${featureSuperTypeGenericTypes.map { it.toTypeName() }}"
            return KSClassDeclarationResult.Error(errorMessage)
        }

        val actionType = featureSuperTypeGenericTypes
            .firstOrNull {
                val actionClassDeclaration = it.type?.resolve()?.declaration?.closestClassDeclaration()
                actionClassDeclaration != null && actionClassDeclaration.isClassOrSubclassOf(Action::class)
            }

        actionType ?: return KSClassDeclarationResult.Error("Error when trying to get base action type")

        val baseActionClassDeclaration = actionType.type?.resolve()?.declaration?.closestClassDeclaration()

        baseActionClassDeclaration ?: return KSClassDeclarationResult.Error("Error when trying to get base action class declaration")

        if (Modifier.SEALED !in baseActionClassDeclaration.modifiers) {
            return KSClassDeclarationResult.Error("Base Action class must be sealed. The \"${baseActionClassDeclaration.toClassName().canonicalName}\" does not meet this requirement.")
        }

        return KSClassDeclarationResult.Success(baseActionClassDeclaration)
    }

    @OptIn(KotlinPoetKspPreview::class)
    private fun writeToFile(className: String, packageName: String, fileSpec: TypeSpec) {
        val fileBuilder = FileSpec.builder(packageName, className)
        val file = fileBuilder.addType(fileSpec).build()
        file.writeTo(codeGenerator, Dependencies(false))
    }

}