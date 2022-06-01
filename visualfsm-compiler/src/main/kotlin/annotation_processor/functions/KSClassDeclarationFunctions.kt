package annotation_processor.functions

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName
import kotlin.reflect.KClass

internal object KSClassDeclarationFunctions {

    private const val MAX_SUPER_CLASS_SEARCH_NESTING_LEVEL = 5

    @OptIn(KotlinPoetKspPreview::class)
    internal fun KSClassDeclaration.isClassOrSubclassOf(kClass: KClass<out Any>): Boolean {
        if (kClass.asClassName() == this.toClassName()) return true
        return superTypes
            .mapNotNull { it.resolve().declaration as? KSClassDeclaration }
            .any {
                if (kClass.asClassName() == it.toClassName()) return@any true
                it.isSubclassOf(kClass)
            }
    }

    @OptIn(KotlinPoetKspPreview::class)
    internal fun KSClassDeclaration.isSubclassOf(
        superClass: KClass<out Any>,
        maxNestingLevel: Int = MAX_SUPER_CLASS_SEARCH_NESTING_LEVEL,
        currentNestingLevel: Int = 0,
    ): Boolean {
        if (currentNestingLevel > maxNestingLevel) return false
        return superTypes
            .mapNotNull { it.resolve().declaration as? KSClassDeclaration }
            .any {
                if (superClass.asClassName() == it.toClassName()) return@any true
                it.isSubclassOf(superClass, maxNestingLevel, currentNestingLevel + 1)
            }
    }

    fun KSClassDeclaration.getAllNestedSealedSubclasses(): Sequence<KSClassDeclaration> {
        return getSealedSubclasses().flatMap {
            if (Modifier.SEALED in it.modifiers) it.getAllNestedSealedSubclasses() else sequenceOf(it)
        }
    }

}