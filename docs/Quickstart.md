ENG | [RUS](./ru/Quickstart-RU.md)

# Quickstart

## How to set up the library

### Android App Setup

#### In the gradle script file of the module where Feature class will be implemented

<details>
  <summary>Groovy(build.gradle)</summary>

```groovy
// Use KSP plugin
plugins {
    id "com.google.devtools.ksp" version "$kspVersion"
}

dependencies {
    // Base classes for Android, JVM and KMM projects (Feature and AsyncWorker coroutines edition)
    implementation "ru.kontur.mobile.visualfsm:visualfsm-core:$visualfsmVersion"

    // Optional - Support of RxJava 3 (FeatureRx, AsyncWorkerRx and dependent classes)
    implementation "ru.kontur.mobile.visualfsm:visualfsm-rxjava3:$visualfsmVersion"
    
    // Code generation
    ksp "ru.kontur.mobile.visualfsm:visualfsm-compiler:$visualfsmVersion"

    // Optional - Classes for easy getting generated code
    implementation "ru.kontur.mobile.visualfsm:visualfsm-providers:$visualfsmVersion"

    // Optional - Graph creation and analysis
    testImplementation "ru.kontur.mobile.visualfsm:visualfsm-tools:$visualfsmVersion"
}
```

</details>
<details>
  <summary>Kotlin(build.gradle.kts)</summary>

```kotlin
// Use KSP plugin
plugins {
    id("com.google.devtools.ksp") version "$kspVersion"
}

dependencies {
    // Base classes for Android, JVM and KMM projects (Feature and AsyncWorker coroutines edition)
    implementation("ru.kontur.mobile.visualfsm:visualfsm-core:$visualfsmVersion")

    // Optional - Support of RxJava 3 (FeatureRx, AsyncWorkerRx and dependent classes)
    implementation("ru.kontur.mobile.visualfsm:visualfsm-rxjava3:$visualfsmVersion")
    
    // Code generation
    ksp("ru.kontur.mobile.visualfsm:visualfsm-compiler:$visualfsmVersion")

    // Optional - Classes for easy getting generated code
    implementation("ru.kontur.mobile.visualfsm:visualfsm-providers:$visualfsmVersion")

    // Optional - Graph creation and analysis
    testImplementation("ru.kontur.mobile.visualfsm:visualfsm-tools:$visualfsmVersion")
}
```

</details>

#### In the gradle script file of the app module

<details>
  <summary>Groovy(build.gradle)</summary>

```groovy
// Add generated code to source code directories (for old versions Android Gradle Plugin)
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
  <summary>Kotlin(build.gradle.kts)</summary>

```kotlin
// Add generated code to source code directories (for old versions Android Gradle Plugin)
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

### KMM project setup

#### In the gradle script file of the module where the annotations will be used

<details>
  <summary>Kotlin(build.gradle.kts)</summary>

```kotlin
plugins {
    kotlin("multiplatform")
    id("com.android.library")
    // Use KSP plugin
    id("com.google.devtools.ksp") version "$kspVersion"
}

sourceSets {
    val commonMain by getting {
        dependencies {
            // Base classes for Android, JVM and KMM projects (Feature and AsyncWorker coroutines edition)
            implementation("ru.kontur.mobile.visualfsm:visualfsm-core:$visualfsmVersion")

            // Optional - Graph creation and analysis
            testImplementation("ru.kontur.mobile.visualfsm:visualfsm-tools:$visualfsmVersion")

            // Add generated code to source code directories
            kotlin.srcDir("${buildDir.absolutePath}/generated/ksp/")
        }
    }
}

dependencies {
    // Code generation
    add("kspAndroid", "ru.kontur.mobile.visualfsm:visualfsm-compiler:$visualfsmVersion")
}
```

</details>

### Setup for other Kotlin applications

#### In the gradle script file of the module where the annotations will be used

<details>
  <summary>Groovy(build.gradle)</summary>

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
    // Base classes for Android, JVM and KMM projects (Feature and AsyncWorker coroutines edition)
    implementation "ru.kontur.mobile.visualfsm:visualfsm-core:$visualfsmVersion"

    // Optional - Support of RxJava 3 (FeatureRx, AsyncWorkerRx and dependent classes)
    implementation "ru.kontur.mobile.visualfsm:visualfsm-rxjava3:$visualfsmVersion"
    
    // Code generation
    ksp "ru.kontur.mobile.visualfsm:visualfsm-compiler:$visualfsmVersion"

    // Optional - Classes for easy getting generated code
    implementation "ru.kontur.mobile.visualfsm:visualfsm-providers:$visualfsmVersion"

    // Optional - Graph creation and analysis
    testImplementation "ru.kontur.mobile.visualfsm:visualfsm-tools:$visualfsmVersion"
}
```

</details>
<details>
  <summary>Kotlin(build.gradle.kts)</summary>

```kotlin
// Use KSP plugin
plugins {
    id("com.google.devtools.ksp") version "$kspVersion"
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
    // Base classes for Android, JVM and KMM projects (Feature and AsyncWorker coroutines edition)
    implementation("ru.kontur.mobile.visualfsm:visualfsm-core:$visualfsmVersion")

    // Optional - Support of RxJava 3 (FeatureRx, AsyncWorkerRx and dependent classes)
    implementation("ru.kontur.mobile.visualfsm:visualfsm-rxjava3:$visualfsmVersion")
    
    // Code generation
    ksp("ru.kontur.mobile.visualfsm:visualfsm-compiler:$visualfsmVersion")

    // Optional - Classes for easy getting generated code
    implementation("ru.kontur.mobile.visualfsm:visualfsm-providers:$visualfsmVersion")

    // Optional - Graph creation and analysis
    testImplementation("ru.kontur.mobile.visualfsm:visualfsm-tools:$visualfsmVersion")
}
```

</details>

### For projects with enabled minification by ProGuard and used visualfsm-providers, add exclude to proguard-rules.pro
```
-keep class * implements ru.kontur.mobile.visualfsm.TransitionsFactory
```

## A simple example of using the library

### SampleFSMState.kt

```kotlin
sealed class SampleFSMState : State {

    object Initial : SampleFSMState()

    object Loading : SampleFSMState()

    data class Completed(val result: String) : SampleFSMState()
}
```

### SampleFSMAction.kt

```kotlin
sealed class SampleFSMAction : Action<SampleFSMState>()

class HandleResult(val result: String) : SampleFSMAction() {
    inner class Success : Transition<SampleFSMState.Loading, SampleFSMState.Completed>() {
        override fun transform(state: SampleFSMState.Loading): SampleFSMState.Completed {
            return SampleFSMState.Completed(result)
        }
    }
}

class Load : SampleFSMAction() {
    inner class StartLoading : Transition<SampleFSMState.Initial, SampleFSMState.Loading>() {
        override fun transform(state: SampleFSMState.Initial): SampleFSMState.Loading {
            return SampleFSMState.Loading
        }
    }
}
```

### SampleFSMAsyncWorker.kt

```kotlin
class SampleFSMAsyncWorker : AsyncWorker<SampleFSMState, SampleFSMAction>() {
    override fun onNextState(state: SampleFSMState): AsyncWorkerTask<SampleFSMState> {
        return when (state) {
            is SampleFSMState.Loading -> {
                AsyncWorkerTask.ExecuteAndCancelExist(state) {
                    val result = getResult()
                    proceed(HandleResult(result))
                }
            }
            else -> AsyncWorkerTask.Cancel()
        }
    }

    private suspend fun getResult(): String {
        delay(3000) // Do some async work
        return "result"
    }
}
```


### SampleFSMFeature.kt

```kotlin
@GenerateTransitionsFactory // annotation for enable generation of TransitionsFactory
// Use Feature with Kotlin Coroutines or FeatureRx with RxJava
class SampleFSMFeature : Feature<SampleFSMState, SampleFSMAction>(
    initialState = SampleFSMState.Initial,
    asyncWorker = SampleFSMAsyncWorker(),
    transitionsFactory = provideTransitionsFactory(), // Get an instance of the generated TransitionsFactory
    // Getting an instance of a generated TransitionsFactory for KMM projects:
    // Name generated by mask Generated[FeatureName]TransitionsFactory()    
    // transitionsFactory = GeneratedAuthFeatureTransitionsFactory(), // Until the first start of code generation, the class will not be visible in the IDE.
)
```


### Feature usage

```kotlin
// Observe states on Feature
sampleFeature.observeState().collect { state -> }

// Observe states on FeatureRx
sampleFeature.observeState().subscribe { state -> }

// Proceed Action
sampleFeature.proceed(Load())
```

### SampleFSMTests.kt

```kotlin
class SampleFSMTests {
    @Test
    fun generateDigraph() {
        println(
            VisualFSM.generateDigraph(
                baseAction = SampleFSMAction::class,
                baseState = SampleFSMState::class,
                initialState = SampleFSMState.Initial::class,
            )
        )
        Assertions.assertTrue(true)
    }

    @Test
    fun allStatesReachableTest() {
        val notReachableStates = VisualFSM.getUnreachableStates(
            baseAction = SampleFSMAction::class,
            baseState = SampleFSMState::class,
            initialState = SampleFSMState.Initial::class,
        )

        Assertions.assertTrue(
            notReachableStates.isEmpty(),
            "FSM have unreachable states: ${notReachableStates.joinToString(", ")}"
        )
    }
}
```

### generateDigraph() method output

```
digraph SampleFSMStateTransitions {
"Initial"
"Initial" -> "Loading" [label=" StartLoading"]
"Loading" -> "Completed" [label=" Success"]
}
```

For the local visualization (on your PC) use [webgraphviz](http://www.webgraphviz.com/).

## Demo projects
#### [Android app (Kotlin Coroutines, Jetpack Compose)](https://github.com/Kontur-Mobile/VisualFSM-Sample-Android)
#### [KMM (Android + iOS) app (Kotlin Coroutines, Jetpack Compose, SwiftUI)](https://github.com/Kontur-Mobile/VisualFSM-Sample-KMM)
#### [Command line app Kotlin (Kotlin Coroutines)](https://github.com/Kontur-Mobile/VisualFSM-Sample-CLI/tree/main/cli-sample)
#### [Command line app Kotlin (RxJava)](https://github.com/Kontur-Mobile/VisualFSM-Sample-CLI/tree/main/cli-sample-rx)
