package annotation_processor

import com.squareup.kotlinpoet.TypeSpec

internal sealed class TypeSpecResult {
    data class Error(val message: String) : TypeSpecResult()
    data class Success(val typeSpec: TypeSpec) : TypeSpecResult()
}
