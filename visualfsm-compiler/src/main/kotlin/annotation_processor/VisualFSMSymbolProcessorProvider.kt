package annotation_processor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class VisualFSMSymbolProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        environment.options
        return VisualFSMSymbolProcessor(
            logger = environment.logger,
            codeGenerator = environment.codeGenerator,
            options = environment.options,
        )
    }
}