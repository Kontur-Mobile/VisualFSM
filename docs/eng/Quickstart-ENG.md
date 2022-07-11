ENG | [RUS](../ru/Quickstart-RU.md)

# Quickstart

## How to set up main classes

Base classes for Android, JVM and KMM projects (Feature and AsyncWorker coroutines edition)

```kotlin
implementation("ru.kontur.mobile.visualfsm:visualfsm-core:1.1.0")
```

Support of RxJava 3 (FeatureRx, AsyncWorkerRx and dependent classes)

```kotlin
implementation("ru.kontur.mobile.visualfsm:visualfsm-rxjava3:1.1.0")
```

Support of RxJava 2 (FeatureRx, AsyncWorkerRx and dependent classes)

```kotlin
implementation("ru.kontur.mobile.visualfsm:visualfsm-rxjava2:1.1.0")
```

Tools for:

Graph creation and analysis

```kotlin
testImplementation("ru.kontur.mobile.visualfsm:visualfsm-tools:1.1.0")
```

## How to set up and enable code generation

### How to set up code generation

#### _Android App Setup_

##### In the gradle.build file of the module where the annotations will be used

<details>
  <summary>Groovy</summary>

```groovy
// Use KSP plugin
plugins {
    id "com.google.devtools.ksp" version "$kspVersion"
}

dependencies {
    // Use AnnotationProcessor
    ksp "ru.kontur.mobile.visualfsm:visualfsm-compiler:1.1.0"
    // Use to easily get the generated code
    implementation "ru.kontur.mobile.visualfsm:visualfsm-providers:1.1.0"
}
```

</details>
<details>
  <summary>Kotlin</summary>

```kotlin
// Use KSP plugin
plugins {
    id("com.google.devtools.ksp") version "1.6.10-1.0.6"
}

dependencies {
    // Use AnnotationProcessor
    ksp("ru.kontur.mobile.visualfsm:visualfsm-compiler:1.1.0")
    // Use to easily get the generated code
    implementation("ru.kontur.mobile.visualfsm:visualfsm-providers:1.1.0")
}
```

</details>

##### In the gradle.build file of the app module

<details>
  <summary>Groovy</summary>

```groovy
// Add generated code to source code directories
android {
    ...
    applicationVariants.all { variant ->
        variant.sourceSets.java.each {
            it.srcDirs += "build/generated/ksp/${variant.name}/kotlin"
        }
    }
}
```

</details>
<details>
  <summary>Kotlin</summary>

```kotlin
// Add generated code to source code directories
android {
    ...
    applicationVariants.all {
        kotlin {
            sourceSets {
                getByName(name) {
                    kotlin.srcDir("build/generated/ksp/$name/kotlin")
                }
            }
        }
    }
}
```

</details>

#### _KMM project setup_

##### In the gradle.build file of the module where the annotations will be used

<details>
  <summary>Kotlin</summary>

```kotlin
plugins {
    kotlin("multiplatform")
    id("com.android.library")
    // Use KSP plugin
    id("com.google.devtools.ksp") version (kspVersion)
}

sourceSets {
    val commonMain by getting {
        dependencies {
            implementation("ru.kontur.mobile.visualfsm:visualfsm-core:1.1.0")
            // Add generated code to source code directories
            kotlin.srcDir("${buildDir.absolutePath}/generated/ksp/")
        }
    }
}

dependencies {
    // Use to easily get the generated code
    add("kspAndroid", "ru.kontur.mobile.visualfsm:visualfsm-compiler:1.1.0")
}
```

</details>

#### _Setup for other Kotlin applications_

##### In the gradle.build file of the module where the annotations will be used

<details>
  <summary>Groovy</summary>

```groovy
// Use KSP plugin
plugins {
    id "com.google.devtools.ksp" version "$kspVersion"
}

// Add generated code to source code directories
kotlin {
    sourceSets {
        main.kotlin.srcDirs += 'build/generated/ksp/main/kotlin'
        test.kotlin.srcDirs += 'build/generated/ksp/test/kotlin'
    }
}

dependencies {
    // Use AnnotationProcessor
    ksp "ru.kontur.mobile.visualfsm:visualfsm-compiler:1.1.0"
    // Use to easily get the generated code. For jvm projects only.
    implementation "ru.kontur.mobile.visualfsm:visualfsm-providers:1.1.0"
}
```

</details>
<details>
  <summary>Kotlin</summary>

```kotlin
// Use KSP plugin
plugins {
    id("com.google.devtools.ksp") version "1.6.10-1.0.6"
}

// Add generated code to source code directories
kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}

dependencies {
    // Use AnnotationProcessor
    ksp("ru.kontur.mobile.visualfsm:visualfsm-compiler:1.1.0")
    // Use to easily get the generated code. For jvm projects only.
    implementation("ru.kontur.mobile.visualfsm:visualfsm-providers:1.1.0")
}
```

</details>

### How to enable code generation

1. Annotate the Feature class with the GenerateTransitionsFactory annotation
2. Pass the transitionsFactory parameter to the Feature constructor
    1. For jvm project use the provideTransitionsFactory function
    2. For a non-jvm project, pass an instance of the generated class.
       The name of the generated class is formed as "Generated\*Feature\*TransitionsFactory",
       where \*Feature\* is the name of the annotated Feature class.
       Please note that until the first run of code generation, the class will not be visible in the IDE

<details>
  <summary>Example for jvm project</summary>

```kotlin
// Use Feature with Kotlin Coroutines or FeatureRx with RxJava
@GenerateTransitionsFactory // annotation for enable generation of TransitionsFactory
class AuthFeature(initialState: AuthFSMState) : Feature<AuthFSMState, AuthFSMAction>(
    initialState = initialState,
    transitionsFactory = provideTransitionsFactory() // Get an instance of the generated TransitionsFactory
)
```

</details>
<details>
  <summary>Example for a non-jvm project</summary>

```kotlin
// Use Feature with Kotlin Coroutines or FeatureRx with RxJava
@GenerateTransitionsFactory // annotation for enable generation of TransitionsFactory
class AuthFeature(initialState: AuthFSMState) : Feature<AuthFSMState, AuthFSMAction>(
    initialState = initialState,
    transitionsFactory = GeneratedAuthFeatureTransitionsFactory()
)
```

</details>