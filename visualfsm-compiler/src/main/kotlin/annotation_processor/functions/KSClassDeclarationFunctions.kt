package annotation_processor.functions

import com.google.devtools.ksp.symbol.FileLocation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Location
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import kotlin.reflect.KClass

internal object KSClassDeclarationFunctions {

    internal fun KSClassDeclaration.isClassOrSubclassOf(kClass: KClass<out Any>): Boolean {
        if (kClass.asClassName() == this.toClassName()) return true
        return superTypes
            .mapNotNull { it.resolve().declaration as? KSClassDeclaration }
            .any {
                if (kClass.asClassName() == it.toClassName()) return@any true
                it.isSubclassOf(kClass)
            }
    }

    internal fun KSClassDeclaration.isSubclassOf(superClass: KClass<out Any>): Boolean {
        return superTypes
            .mapNotNull { it.resolve().declaration as? KSClassDeclaration }
            .any {
                if (superClass.asClassName() == it.toClassName()) return@any true
                it.isSubclassOf(superClass)
            }
    }

    internal fun KSClassDeclaration.getAllNestedSealedSubclasses(): Sequence<KSClassDeclaration> {
        return getSealedSubclasses().flatMap {
            if (Modifier.SEALED in it.modifiers) it.getAllNestedSealedSubclasses() else sequenceOf(it)
        }
    }

    fun KSClassDeclaration.getCanonicalClassNameAndLink(): String {
        return "${toClassName().canonicalName}${getLinkOrEmptyString(location)}"
    }

    private fun getLinkOrEmptyString(location: Location): String {
        if (location !is FileLocation) return ""
        val fileName = location.filePath.substringAfterLast("/")
        return "($fileName:${location.lineNumber})"
    }
}