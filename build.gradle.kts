plugins{
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.multiplatform) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.kotlin) apply false
}

group = "ru.kontur.mobile.visualfsm"
version = "3.0.0-alpha"

buildscript {
    dependencies{
        classpath(libs.atomicfu.gradle.plugin)
    }
}