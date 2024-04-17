enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

include(":visualfsm-core")
include(":visualfsm-rxjava2")
include(":visualfsm-rxjava3")
include(":visualfsm-compiler")
include(":visualfsm-tools")
include(":visualfsm-providers")
include(":core-tests")