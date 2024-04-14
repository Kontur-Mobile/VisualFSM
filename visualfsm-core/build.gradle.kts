plugins {
    alias(libs.plugins.multiplatform)
    id(libs.plugins.atomicfu.get().pluginId)
}
apply(from = "../publish.gradle")

group = rootProject.group
version = rootProject.version

kotlin {
    jvm {
        jvmToolchain(17)
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