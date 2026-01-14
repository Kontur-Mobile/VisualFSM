plugins {
    id("org.jetbrains.kotlin.jvm") version "2.2.20"
    id("java-gradle-plugin")
    alias(libs.plugins.dokka)
    alias(libs.plugins.publish)
}

group = rootProject.group
version = rootProject.version

dependencies {
    implementation(kotlin("gradle-plugin-api"))
    compileOnly(projects.visualfsmCompilerPlugin)
}

gradlePlugin {
    plugins {
        create("VisualFSMPlugin") {
            id = "ru.kontur.mobile.visualfsm"
            displayName = "VisualFSM Plugin"
            description = "Kotlin compiler plugin for VisualFSM"
            implementationClass = "ru.kontur.mobile.visualfsm.gradle_plugin.VisualFSMGradlePlugin"
        }
    }
}

mavenPublishing {
    coordinates(group.toString(), project.name, version.toString())
}
