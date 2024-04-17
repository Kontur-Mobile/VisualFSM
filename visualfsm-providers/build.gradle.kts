plugins {
    id("org.jetbrains.kotlin.jvm")
}

apply(from = "../publish.gradle")

group = rootProject.group
version = rootProject.version

dependencies {
    implementation(projects.visualfsmCore)

    implementation(libs.kotlin.reflect)
}

tasks.test {
    useJUnitPlatform()
}