package ru.kontur.mobile.visualfsm.gradle_plugin

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

@Suppress("unused") // Used via reflection.
class VisualFSMGradlePlugin : KotlinCompilerPluginSupportPlugin {

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>) = true

    override fun applyToCompilation(
        kotlinCompilation: KotlinCompilation<*>,
    ): Provider<List<SubpluginOption>> {
        return kotlinCompilation.target.project.provider { emptyList() }
    }

    override fun getCompilerPluginId(): String = "ru.kontur.mobile.visualfsm"

    override fun getPluginArtifact() = SubpluginArtifact(
        groupId = "ru.kontur.mobile.visualfsm",
        artifactId = "visualfsm-compiler-plugin",
        version = "4.0.0"
    )

    override fun getPluginArtifactForNative() = null

    override fun apply(target: Project) = Unit
}