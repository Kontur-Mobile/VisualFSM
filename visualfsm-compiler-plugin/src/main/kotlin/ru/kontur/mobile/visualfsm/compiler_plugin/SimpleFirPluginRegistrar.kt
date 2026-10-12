package ru.kontur.mobile.visualfsm.compiler_plugin

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class SimpleFirPluginRegistrar(private val messageCollector: MessageCollector) : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        messageCollector.logWarning("Registering GenerateTransitionInterfacesExtension")
        // +FirAdditionalCheckersExtension.Factory { session ->
        //     AddActionSupertypesExtension(messageCollector, session)
        // }
        // +FirDeclarationGenerationExtension.Factory { session ->
        //     GenerateTransitionInterfacesExtension(messageCollector, session)
        // }
        // +FirSupertypeGenerationExtension.Factory { session ->
        //     AddSupertypeWithTransitionFunctions(messageCollector, session)
        // }
        // +FirAdditionalCheckersExtension.Factory { session ->
        //     VisualFSMCheckersExtension(messageCollector, session)
        // }
        +FirDeclarationGenerationExtension.Factory { session ->
            AddGetTransitionsMemberFunctionsExtension(messageCollector, session)
        }
        +FirAdditionalCheckersExtension.Factory { session ->
            FooChecker(messageCollector, session)
        }
    }
}
