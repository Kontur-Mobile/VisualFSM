plugins{
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.multiplatform) apply false
}

group = "ru.kontur.mobile.visualfsm"
version = "2.0.0"

buildscript {
    dependencies{
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.dokka.gradle.plugin)
        classpath(libs.atomicfu.gradle.plugin)
    }
}