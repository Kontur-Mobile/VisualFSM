ENG | [RUS](../ru/Quickstart-RU.md)

# Quickstart

## How to set up the library

### Android App Setup

#### In the gradle script file of the module where the annotations will be used

<details>
  <summary>Groovy(build.gradle)</summary>

```groovy
// Use KSP plugin
plugins {
    id "com.google.devtools.ksp" version "$kspVersion"
}

dependencies {
    // Base classes for Android, JVM and KMM projects (Feature and AsyncWorker coroutines edition)
    implementation "ru.kontur.mobile.visualfsm:visualfsm-core:1.1.0"

    // Optional - Support of RxJava 3 (FeatureRx, AsyncWorkerRx and dependent classes)
    implementation "ru.kontur.mobile.visualfsm:visualfsm-rxjava3:1.1.0"

    // Optional - Support of RxJava 2 (FeatureRx, AsyncWorkerRx and dependent classes)
    implementation "ru.kontur.mobile.visualfsm:visualfsm-rxjava2:1.1.0"

    // Code generation
    ksp "ru.kontur.mobile.visualfsm:visualfsm-compiler:1.1.0"

    // Optional - Classes for easy getting generated code
    implementation "ru.kontur.mobile.visualfsm:visualfsm-providers:1.1.0"

    // Optional - Graph creation and analysis
    testImplementation "ru.kontur.mobile.visualfsm:visualfsm-tools:1.1.0"
}
```

</details>
<details>
  <summary>Kotlin(build.gradle.kts)</summary>

```kotlin
// Use KSP plugin
plugins {
    id("com.google.devtools.ksp") version "1.6.10-1.0.6"
}

dependencies {
    // Base classes for Android, JVM and KMM projects (Feature and AsyncWorker coroutines edition)
    implementation("ru.kontur.mobile.visualfsm:visualfsm-core:1.1.0")

    // Optional - Support of RxJava 3 (FeatureRx, AsyncWorkerRx and dependent classes)
    implementation("ru.kontur.mobile.visualfsm:visualfsm-rxjava3:1.1.0")

    // Optional - Support of RxJava 2 (FeatureRx, AsyncWorkerRx and dependent classes)
    implementation("ru.kontur.mobile.visualfsm:visualfsm-rxjava2:1.1.0")

    // Code generation
    ksp("ru.kontur.mobile.visualfsm:visualfsm-compiler:1.1.0")

    // Optional - Classes for easy getting generated code
    implementation("ru.kontur.mobile.visualfsm:visualfsm-providers:1.1.0")

    // Optional - Graph creation and analysis
    testImplementation("ru.kontur.mobile.visualfsm:visualfsm-tools:1.1.0")
}
```

</details>

#### In the gradle script file of the app module

<details>
  <summary>Groovy(build.gradle)</summary>

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
  <summary>Kotlin(build.gradle.kts)</summary>

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

### KMM project setup

#### In the gradle script file of the module where the annotations will be used

<details>
  <summary>Kotlin(build.gradle.kts)</summary>

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
            // Base classes for Android, JVM and KMM projects (Feature and AsyncWorker coroutines edition)
            implementation("ru.kontur.mobile.visualfsm:visualfsm-core:1.1.0")

            // Optional - Graph creation and analysis
            testImplementation("ru.kontur.mobile.visualfsm:visualfsm-tools:1.1.0")

            // Add generated code to source code directories
            kotlin.srcDir("${buildDir.absolutePath}/generated/ksp/")
        }
    }
}

dependencies {
    // Code generation
    add("kspAndroid", "ru.kontur.mobile.visualfsm:visualfsm-compiler:1.1.0")
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
    implementation "ru.kontur.mobile.visualfsm:visualfsm-core:1.1.0"

    // Optional - Support of RxJava 3 (FeatureRx, AsyncWorkerRx and dependent classes)
    implementation "ru.kontur.mobile.visualfsm:visualfsm-rxjava3:1.1.0"

    // Optional - Support of RxJava 2 (FeatureRx, AsyncWorkerRx and dependent classes)
    implementation "ru.kontur.mobile.visualfsm:visualfsm-rxjava2:1.1.0"

    // Code generation
    ksp "ru.kontur.mobile.visualfsm:visualfsm-compiler:1.1.0"

    // Optional - Classes for easy getting generated code
    implementation "ru.kontur.mobile.visualfsm:visualfsm-providers:1.1.0"

    // Optional - Graph creation and analysis
    testImplementation "ru.kontur.mobile.visualfsm:visualfsm-tools:1.1.0"
}
```

</details>
<details>
  <summary>Kotlin(build.gradle.kts)</summary>

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
    // Base classes for Android, JVM and KMM projects (Feature and AsyncWorker coroutines edition)
    implementation("ru.kontur.mobile.visualfsm:visualfsm-core:1.1.0")

    // Optional - Support of RxJava 3 (FeatureRx, AsyncWorkerRx and dependent classes)
    implementation("ru.kontur.mobile.visualfsm:visualfsm-rxjava3:1.1.0")

    // Optional - Support of RxJava 2 (FeatureRx, AsyncWorkerRx and dependent classes)
    implementation("ru.kontur.mobile.visualfsm:visualfsm-rxjava2:1.1.0")

    // Code generation
    ksp("ru.kontur.mobile.visualfsm:visualfsm-compiler:1.1.0")

    // Optional - Classes for easy getting generated code
    implementation("ru.kontur.mobile.visualfsm:visualfsm-providers:1.1.0")

    // Optional - Graph creation and analysis
    testImplementation("ru.kontur.mobile.visualfsm:visualfsm-tools:1.1.0")
}
```

</details>

## A simple example of using the library

### SampleFSMState.kt

```kotlin
sealed class SampleFSMState : State {

    object Initial : SampleFSMState()

    object Loading : SampleFSMState()

    data class Success(val result: String) : SampleFSMState()
}
```

### SampleFSMAction.kt

```kotlin
sealed class SampleFSMAction : Action<SampleFSMState>()

class HandleResult(val result: String) : SampleFSMAction() {
    inner class Success : Transition<SampleFSMState.Loading, SampleFSMState.Success>() {
        override fun transform(state: SampleFSMState.Loading): SampleFSMState.Success {
            return SampleFSMState.Success(result)
        }
    }
}

class StartLoading : SampleFSMAction() {
    inner class InitiateLoading : Transition<SampleFSMState.Initial, SampleFSMState.Loading>() {
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
                    proceed(HandleResult(getResult()))
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

### SampleTransitionCallbacks.kt

```kotlin
class SampleTransitionCallbacks : TransitionCallbacks<SampleFSMState> {
    override fun onActionLaunched(action: Action<SampleFSMState>, currentState: SampleFSMState) {
        println("onActionLaunched\naction=$action\ncurrentState=$currentState")
    }

    override fun onTransitionSelected(
        action: Action<SampleFSMState>,
        transition: Transition<SampleFSMState, SampleFSMState>,
        currentState: SampleFSMState,
    ) {
        println("onTransitionSelected\naction=$action\ntransition=$transition\ncurrentState=$currentState")
    }

    override fun onNewStateReduced(
        action: Action<SampleFSMState>,
        transition: Transition<SampleFSMState, SampleFSMState>,
        oldState: SampleFSMState,
        newState: SampleFSMState,
    ) {
        println("onNewStateReduced\naction=$action\ntransition=$transition\noldState=$oldState\nnewState=$newState")
    }

    override fun onNoTransitionError(action: Action<SampleFSMState>, currentState: SampleFSMState) {
        println("onNoTransitionError\naction=$action\ncurrentState=$currentState")
    }

    override fun onMultipleTransitionError(action: Action<SampleFSMState>, currentState: SampleFSMState) {
        println("onMultipleTransitionError\naction=$action\ncurrentState=$currentState")
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
    transitionCallbacks = SampleTransitionCallbacks(),
    transitionsFactory = provideTransitionsFactory(), // Get an instance of the generated TransitionsFactory
    // Getting an instance of a generated TransitionsFactory for non-JVM and non-Android projects.
    // Until the first start of code generation, the class will not be visible in the IDE.
    // transitionsFactory = GeneratedAuthFeatureTransitionsFactory(),
)
```

### Main.kt

```kotlin
fun main() {
    val sampleFeature = SampleFSMFeature()
    sampleFeature.proceed(StartLoading())
    // Waiting for an asynchronous operation to complete.
    // Otherwise, the program will exit before the "Loading->Success" transition is executed.
    Thread.sleep(3100)
}
```

<details>
  <summary>Console output after application exit</summary>

```
onActionLaunched
action=sampleFSM.actions.StartLoading@17550481
currentState=sampleFSM.SampleFSMState$Initial@735f7ae5
onTransitionSelected
action=sampleFSM.actions.StartLoading@17550481
transition=sampleFSM.actions.StartLoading$InitiateLoading@6fb554cc
currentState=sampleFSM.SampleFSMState$Initial@735f7ae5
onNewStateReduced
action=sampleFSM.actions.StartLoading@17550481
transition=sampleFSM.actions.StartLoading$InitiateLoading@6fb554cc
oldState=sampleFSM.SampleFSMState$Initial@735f7ae5
newState=sampleFSM.SampleFSMState$Loading@77b52d12
onActionLaunched
action=sampleFSM.actions.HandleResult@71489e2
currentState=sampleFSM.SampleFSMState$Loading@77b52d12
onTransitionSelected
action=sampleFSM.actions.HandleResult@71489e2
transition=sampleFSM.actions.HandleResult$Success@54914a88
currentState=sampleFSM.SampleFSMState$Loading@77b52d12
onNewStateReduced
action=sampleFSM.actions.HandleResult@71489e2
transition=sampleFSM.actions.HandleResult$Success@54914a88
oldState=sampleFSM.SampleFSMState$Loading@77b52d12
newState=Success(result=result)
```

</details>

## Other examples

### An example for JVM and Android applications see [here](../../sample/src/main/kotlin/authFSM/AuthFSMFeature.kt)

### Example for KMM project coming soon