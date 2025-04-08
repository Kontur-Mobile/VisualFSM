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
    implementation(libs.kotlin.reflect)

    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.test {
    useJUnitPlatform()
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
