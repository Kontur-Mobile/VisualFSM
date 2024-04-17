import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("org.jetbrains.kotlin.jvm")
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(projects.visualfsmCore)
    implementation(projects.visualfsmProviders)
    ksp(projects.visualfsmCompiler)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(projects.visualfsmTools)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotlinx.coroutines.test)
}

kotlin {
    sourceSets {
        main.configure { kotlin.srcDir("build/generated/ksp/main/kotlin") }
        test.configure { kotlin.srcDir("build/generated/ksp/test/kotlin") }
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events.addAll(setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED))
    }
}