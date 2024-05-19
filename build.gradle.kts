plugins{
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.multiplatform) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.kotlin) apply false
}

group = "ru.kontur.mobile.visualfsm"
version = "2.99.0"

buildscript {
    dependencies{
        classpath(libs.atomicfu.gradle.plugin)
    }
}