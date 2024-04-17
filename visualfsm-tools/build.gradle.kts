plugins {
    id("org.jetbrains.kotlin.jvm")
}
apply(from = "../publish.gradle")

group = rootProject.group
version = rootProject.version

dependencies {
    implementation(projects.visualfsmCore)
    implementation(libs.kotlin.reflect)

    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.test {
    useJUnitPlatform()
}