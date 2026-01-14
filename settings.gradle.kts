enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        mavenLocal()
    }
}

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        mavenLocal()
    }
}

rootProject.name = "VisualFSM"

include(":visualfsm-core")
include(":visualfsm-rxjava3")
include(":visualfsm-compiler")
include(":visualfsm-tools")
include(":visualfsm-providers")
include(":core-tests")
include(":visualfsm-compiler-plugin")
include(":visualfsm-gradle-plugin")