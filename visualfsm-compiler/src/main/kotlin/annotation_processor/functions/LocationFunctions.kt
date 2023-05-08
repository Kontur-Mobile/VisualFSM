package annotation_processor.functions

import com.google.devtools.ksp.symbol.FileLocation
import com.google.devtools.ksp.symbol.Location

internal object LocationFunctions {
    internal fun Location.getLinkOrEmptyString(): String {
        if (this !is FileLocation) {
            return ""
        }
        val fileName = filePath.substringAfterLast("/")
        return "($fileName:$lineNumber)"
    }
}