package ru.kontur.mobile.visualfsm.annotation_processor

import annotation_processor.VisualFSMSymbolProcessorProvider
import com.tschuchort.compiletesting.*
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import java.io.File

@OptIn(ExperimentalCompilerApi::class)
object TestUtil {

    fun getKotlinCompilation(
        sources: List<SourceFile>,
        kspProcessorOptions: Map<String, String> = mutableMapOf(),
    ): KotlinCompilation {
        return KotlinCompilation().apply {
            configureKsp(useKsp2 = true) {
                symbolProcessorProviders += VisualFSMSymbolProcessorProvider()
            }
            inheritClassPath = true
            this.sources += sources
            this.kspProcessorOptions += kspProcessorOptions
        }
    }

    fun JvmCompilationResult.getKspCodeGeneratedSources(): List<File> {
        return getKspGeneratedSources(this, "kotlin")
    }

    fun JvmCompilationResult.getKspNoCodeGeneratedSources(): List<File> {
        return getKspGeneratedSources(this, "resources")
    }

    private fun getKspGeneratedSources(result: JvmCompilationResult, dirName: String): List<File> {
        val kspWorkingDir = result.getWorkingDir().resolve("ksp")
        val kspGeneratedDir = kspWorkingDir.resolve("sources")
        val kotlinGeneratedDir = kspGeneratedDir.resolve(dirName)
        return kotlinGeneratedDir.walkTopDown().toList() - kotlinGeneratedDir
    }

    private fun JvmCompilationResult.getWorkingDir(): File = outputDirectory.parentFile!!

}