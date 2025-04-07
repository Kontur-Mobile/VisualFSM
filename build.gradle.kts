plugins {
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.multiplatform) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.atomicfu) apply false
    alias(libs.plugins.publish) apply false
}

group = "ru.kontur.mobile.visualfsm"
version = "4.0.0"
