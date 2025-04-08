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

    coordinates(group.toString(), project.name, version.toString())

    pom {
        name = findProperty("POM_NAME") as String
        description = findProperty("POM_DESCRIPTION") as String
        inceptionYear = findProperty("POM_INCEPTION_YEAR") as String
        url = findProperty("POM_URL") as String
        licenses {
            license {
                name = findProperty("POM_LICENSE_NAME") as String
                url = findProperty("POM_LICENSE_URL") as String
                distribution = findProperty("POM_LICENSE_DIST") as String
            }
        }
        developers {
            developer {
                id = findProperty("POM_DEVELOPER_ID") as String
                name = findProperty("POM_DEVELOPER_NAME") as String
                url = findProperty("POM_DEVELOPER_URL") as String
            }
        }
        scm {
            url = findProperty("POM_SCM_URL") as String
            connection = findProperty("POM_SCM_CONNECTION") as String
            developerConnection = findProperty("POM_SCM_DEV_CONNECTION") as String
        }
    }
}
