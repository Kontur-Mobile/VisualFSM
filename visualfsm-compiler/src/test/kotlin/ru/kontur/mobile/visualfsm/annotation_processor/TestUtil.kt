package ru.kontur.mobile.visualfsm.annotation_processor

import annotation_processor.VisualFSMSymbolProcessorProvider
import com.tschuchort.compiletesting.*
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
object TestUtil {

    fun getKotlinCompilation(
        sources: List<SourceFile>,
        kspProcessorOptions: Map<String, String> = emptyMap(),
    ): KotlinCompilation {
        return KotlinCompilation().apply {
            configureKsp {
                symbolProcessorProviders += VisualFSMSymbolProcessorProvider()
            }
            inheritClassPath = true
            this.sources += sources
            this.kspProcessorOptions += kspProcessorOptions
        }
    }

}