package annotation_processor

import com.google.devtools.ksp.symbol.KSClassDeclaration

internal sealed class KSClassDeclarationResult {
    data class Error(val message: String) : KSClassDeclarationResult()
    data class Success(val classDeclaration: KSClassDeclaration) : KSClassDeclarationResult()
}
