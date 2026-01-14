plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.dokka)
    alias(libs.plugins.publish)
}

group = rootProject.group
version = rootProject.version

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:2.2.20")
    implementation("org.jetbrains.kotlin:analysis-api-test-framework:2.2.20")

    implementation(projects.visualfsmCore)

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}

mavenPublishing {
    coordinates(group.toString(), project.name, version.toString())
}
