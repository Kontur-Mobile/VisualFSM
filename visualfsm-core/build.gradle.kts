plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.atomicfu)
}
apply(from = "../publish.gradle")

group = rootProject.group
version = rootProject.version

kotlin {
    jvmToolchain(17)
    jvm {
        withJava()
        testRuns.named("test") {
            executionTask.configure {
                useJUnitPlatform()
            }
        }
    }
    js { browser { } }
    iosArm64()
    iosX64()
    iosSimulatorArm64()
    macosX64()
    linuxX64()
    mingwX64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}