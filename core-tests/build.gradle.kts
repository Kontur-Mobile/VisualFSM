import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("org.jetbrains.kotlin.jvm")
    alias(libs.plugins.ksp)
    id("ru.kontur.mobile.visualfsm") version "4.0.0"
}

dependencies {
    implementation(projects.visualfsmCore)
    implementation(projects.visualfsmProviders)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(projects.visualfsmTools)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotlinx.coroutines.test)
}

kotlin {
    jvmToolchain(21)

    // sourceSets {
    //     main.configure { kotlin.srcDir("build/generated/ksp/main/kotlin") }
    //     test.configure { kotlin.srcDir("build/generated/ksp/test/kotlin") }
    // }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events.addAll(setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED))
    }
}