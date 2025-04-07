import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.atomicfu)
    alias(libs.plugins.dokka)
    alias(libs.plugins.publish)
}

group = rootProject.group
version = rootProject.version

kotlin {
    jvmToolchain(17)

    jvm()

    js {
        browser()
        nodejs()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmWasi {
        nodejs()
    }

    // According to https://kotlinlang.org/docs/native-target-support.html
    // Tier 1
    macosX64()
    macosArm64()
    iosSimulatorArm64()
    iosX64()
    iosArm64()

    // Tier 2
    linuxX64()
    linuxArm64()
    watchosSimulatorArm64()
    watchosX64()
    watchosArm32()
    watchosArm64()
    tvosSimulatorArm64()
    tvosX64()
    tvosArm64()

    // Tier 3
    mingwX64()
    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()
    watchosDeviceArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates(group.toString(), "visualfsm-core", version.toString())

    pom {
        name = "VisualFSM"
        description = "VisualFSM - Kotlin Multiplatform library for FSM with visualization and analysis tools"
        inceptionYear = "2024"
        url = "https://github.com/kotlin-hands-on/fibonacci/"
        licenses {
            license {
                name = "MIT License"
                url = "https://raw.githubusercontent.com/Kontur-Mobile/VisualFSM/main/LICENSE"
                distribution = "https://raw.githubusercontent.com/Kontur-Mobile/VisualFSM/main/LICENSE"
            }
        }
        developers {
            developer {
                id = "skbkontur"
                name = "SKB Kontur"
                url = "https://kontur.ru/"
            }
        }
        scm {
            url = "https://github.com/Kontur-Mobile/visualfsm"
            connection = "scm:git:git://github.com/Kontur-Mobile/visualfsm.git"
            developerConnection = "scm:git:git://github.com/Kontur-Mobile/visualfsm.git"
        }
    }
}