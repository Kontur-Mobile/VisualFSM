import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("org.jetbrains.kotlin.jvm")
    alias(libs.plugins.ksp)
}
apply(from = "../publish.gradle")

group = rootProject.group
version = rootProject.version

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(projects.visualfsmCore)
    implementation(projects.visualfsmProviders)
    implementation(libs.rxJava3)

    testImplementation(projects.coreTests)
    testImplementation(projects.visualfsmTools)
    kspTest(projects.visualfsmCompiler)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotlinx.coroutines.core)
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