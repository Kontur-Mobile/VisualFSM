package annotation_processor.functions

import annotation_processor.functions.LocationFunctions.getLinkOrEmptyString
import com.google.devtools.ksp.isPrivate
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import kotlin.reflect.KClass

internal object KSPropertyDeclarationFunctions {

    internal fun KSPropertyDeclaration.isPrivate(): Boolean {
        return type.resolve().declaration.isPrivate()
    }

    internal fun KSPropertyDeclaration.isTypeOfClass(superClass: KClass<out Any>): Boolean {
        return superClass.asClassName() == type.resolve().toClassName()
    }

    internal fun KSPropertyDeclaration.getName(): String {
        return this.simpleName.asString()
    }

    internal fun KSPropertyDeclaration.getCanonicalPropertyNameAndLink(): String {
        val propertyPackageName = type.resolve().declaration.packageName.asString()
        val propertyClassName = type.resolve().declaration.parentDeclaration?.simpleName?.asString()
        val propertyName = type.resolve().declaration.simpleName.asString()

        val path = "$propertyPackageName.$propertyClassName.$propertyName"

        return "$path${location.getLinkOrEmptyString()}"
    }
}