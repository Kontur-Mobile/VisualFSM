package annotation_processor

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import ru.kontur.mobile.visualfsm.GeneratedActionFactory

internal class GeneratedActionFactoryFileSpecFactory {

    internal fun create(baseActionClassDeclaration: KSClassDeclaration, className: String): TypeSpecResult {
        return getTypeSpec(className, baseActionClassDeclaration)
    }

    @OptIn(KotlinPoetKspPreview::class)
    private fun getTypeSpec(className: String, baseActionClassDeclaration: KSClassDeclaration): TypeSpecResult {

        val classBuilder = TypeSpec.classBuilder(className)

        classBuilder.superclass(
            GeneratedActionFactory::class.asClassName().parameterizedBy(baseActionClassDeclaration.asStarProjectedType().toTypeName())
        )

        val actionSealedSubclasses = baseActionClassDeclaration.getSealedSubclasses()

        if (!actionSealedSubclasses.iterator().hasNext()) {
            return TypeSpecResult.Error("Base action class must have sealed subclasses")
        }

        val createFunctionCodeBuilder = StringBuilder()

        createFunctionCodeBuilder.append("return·when·(action)·{\n")
        actionSealedSubclasses.forEach { actionSubclassDeclaration ->
            createFunctionCodeBuilder.append("····is·${actionSubclassDeclaration.toClassName()}·->·${actionSubclassDeclaration.toClassName()}Impl(")
            val actionSubclassConstructor = actionSubclassDeclaration.primaryConstructor
            actionSubclassConstructor
                ?: return TypeSpecResult.Error("Error when trying get \"${actionSubclassDeclaration.toClassName().canonicalName}\" primary constructor")
            actionSubclassConstructor.parameters.forEachIndexed { index, parameter ->
                if (!parameter.isVal && !parameter.isVar) {
                    return TypeSpecResult.Error("All action constructor parameters must be declared as properties (have the var or val keyword). The \"${parameter.name}\" parameter in the \"${actionSubclassDeclaration.toClassName().canonicalName}\" constructor does not meet this requirement.")
                }
                if (setOf(Modifier.PRIVATE, Modifier.PROTECTED, Modifier.INTERNAL).any { it in parameter.type.modifiers }) {
                    return TypeSpecResult.Error("All action constructor parameters must have \"public\" visibility. The \"${parameter.name}\" parameter in the \"${actionSubclassDeclaration.toClassName().canonicalName}\" constructor does not meet this requirement.")
                }
                createFunctionCodeBuilder.append("action.${parameter.name!!.asString()}")
                if (index < actionSubclassConstructor.parameters.size - 1) {
                    createFunctionCodeBuilder.append(",·")
                }
            }
            createFunctionCodeBuilder.append(")\n")
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

}