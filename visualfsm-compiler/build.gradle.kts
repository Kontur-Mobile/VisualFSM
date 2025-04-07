import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlin.jvm)
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
    implementation(projects.visualfsmRxjava3)

    implementation(libs.ksp.symbol.processing.api)

    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)

    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.vintage.engine)

    testImplementation(libs.kotlin.compile.testing)
    testImplementation(libs.kotlin.compile.testing.ksp)
}

tasks.test {
    useJUnitPlatform()
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates(group.toString(), "visualfsm-compiler", version.toString())

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