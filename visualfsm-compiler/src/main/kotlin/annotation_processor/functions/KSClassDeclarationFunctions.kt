package annotation_processor.functions

import com.google.devtools.ksp.symbol.*
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

    internal fun KSDeclaration.simpleStateNameWithSealedName(baseStateClassDeclaration: KSClassDeclaration): String {
        return this.qualifiedName!!.asString().substringAfterLast("${baseStateClassDeclaration.simpleName.asString()}.")
    }

    internal fun KSClassDeclaration.getCanonicalClassNameAndLink(): String {
        return "${toClassName().canonicalName}${getLinkOrEmptyString(location)}"
    }

    private fun getLinkOrEmptyString(location: Location): String {
        if (location !is FileLocation) return ""
        val fileName = location.filePath.substringAfterLast("/")
        return "($fileName:${location.lineNumber})"
    }
}