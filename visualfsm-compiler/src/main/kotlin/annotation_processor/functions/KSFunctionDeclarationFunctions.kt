package annotation_processor.functions

import com.google.devtools.ksp.symbol.FileLocation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Location
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import kotlin.reflect.KClass

internal object KSFunctionDeclarationFunctions {

    internal fun KSFunctionDeclaration.getCanonicalClassNameAndLink(): String {
        return "fun ${simpleName.getShortName()}${getLinkOrEmptyString(location)}"
    }

    private fun getLinkOrEmptyString(location: Location): String {
        if (location !is FileLocation) return ""
        val fileName = location.filePath.substringAfterLast("/")
        return "($fileName:${location.lineNumber})"
    }
}