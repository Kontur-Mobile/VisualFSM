import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dokka)
    alias(libs.plugins.publish)
}

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

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates(group.toString(), "visualfsm-rxjava3", version.toString())

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