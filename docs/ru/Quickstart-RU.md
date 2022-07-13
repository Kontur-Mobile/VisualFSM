[ENG](../eng/Quickstart-ENG.md) | RUS

# Первичная настройка

## Как подключить библиотеку

### Для Android приложения

#### В gradle скрипте модуля, в котором будут использованы аннотации

<details>
  <summary>Groovy(build.gradle)</summary>

```groovy
// Подключаем KSP плагин
plugins {
    id "com.google.devtools.ksp" version "$kspVersion"
}

dependencies {
    // Базовые классы для Android, JVM и KMM проектов (Kotlin Coroutines версия Feature и AsyncWorker)
    implementation "ru.kontur.mobile.visualfsm:visualfsm-core:1.1.0"
    
    // Опционально - Поддержка RxJava 3 (FeatureRx, AsyncWorkerRx и их зависимости)
    implementation "ru.kontur.mobile.visualfsm:visualfsm-rxjava3:1.1.0"

    // Опционально - Поддержка RxJava 2 (FeatureRx, AsyncWorkerRx и их зависимости)
    implementation "ru.kontur.mobile.visualfsm:visualfsm-rxjava2:1.1.0"
    
    // Кодогенерация
    ksp "ru.kontur.mobile.visualfsm:visualfsm-compiler:1.1.0"
    
    // Опционально - Классы для удобного получения сгенерированного кода
    implementation "ru.kontur.mobile.visualfsm:visualfsm-providers:1.1.0"

    // Опционально - Анализ и построение графа
    testImplementation "ru.kontur.mobile.visualfsm:visualfsm-tools:1.1.0"
}
```

</details>
<details>
  <summary>Kotlin(build.gradle.kts)</summary>

```kotlin
// Подключаем KSP плагин
plugins {
    id("com.google.devtools.ksp") version "1.6.10-1.0.6"
}

dependencies {
    // Базовые классы для Android, JVM и KMM проектов (Kotlin Coroutines версия Feature и AsyncWorker)
    implementation("ru.kontur.mobile.visualfsm:visualfsm-core:1.1.0")

    // Опционально - Поддержка RxJava 3 (FeatureRx, AsyncWorkerRx и их зависимости)
    implementation("ru.kontur.mobile.visualfsm:visualfsm-rxjava3:1.1.0")

    // Опционально - Поддержка RxJava 2 (FeatureRx, AsyncWorkerRx и их зависимости)
    implementation("ru.kontur.mobile.visualfsm:visualfsm-rxjava2:1.1.0")

    // Кодогенерация
    ksp("ru.kontur.mobile.visualfsm:visualfsm-compiler:1.1.0")

    // Опционально - Классы для удобного получения сгенерированного кода
    implementation("ru.kontur.mobile.visualfsm:visualfsm-providers:1.1.0")

    // Опционально - Анализ и построение графа
    testImplementation("ru.kontur.mobile.visualfsm:visualfsm-tools:1.1.0")
}
```

</details>

#### В gradle скрипте app модуля

<details>
  <summary>Groovy(build.gradle)</summary>

```groovy
// Добавляем сгенерированный код в каталоги исходного кода
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
// Добавляем сгенерированный код в каталоги исходного кода
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

### Для КММ проекта

#### В gradle скрипте модуля, в котором будут использованы аннотации

<details>
  <summary>Kotlin(build.gradle.kts)</summary>

```kotlin
plugins {
    kotlin("multiplatform")
    id("com.android.library")
    // Подключаем KSP плагин
    id("com.google.devtools.ksp") version (kspVersion)
}

sourceSets {
    val commonMain by getting {
        dependencies {
            // Базовые классы для Android, JVM и KMM проектов (Kotlin Coroutines версия Feature и AsyncWorker)
            implementation("ru.kontur.mobile.visualfsm:visualfsm-core:1.1.0")
            
            // Опционально - Анализ и построение графа
            testImplementation("ru.kontur.mobile.visualfsm:visualfsm-tools:1.1.0")
            
            // Добавляем сгенерированный код в каталоги исходного кода
            kotlin.srcDir("${buildDir.absolutePath}/generated/ksp/")
        }
    }
}

dependencies {
    // Кодогенерация
    add("kspAndroid", "ru.kontur.mobile.visualfsm:visualfsm-compiler:1.1.0")
}
```

</details>

### Для других Kotlin приложений

#### В gradle скрипте модуля, в котором будут использованы аннотации

<details>
  <summary>Groovy(build.gradle)</summary>

```groovy
// Подключаем KSP плагин
plugins {
    id "com.google.devtools.ksp" version "$kspVersion"
}

// Добавляем сгенерированный код в каталоги исходного кода
kotlin {
    sourceSets {
        main.kotlin.srcDirs += 'build/generated/ksp/main/kotlin'
        test.kotlin.srcDirs += 'build/generated/ksp/test/kotlin'
    }
}

dependencies {
    // Базовые классы для Android, JVM и KMM проектов (Kotlin Coroutines версия Feature и AsyncWorker)
    implementation "ru.kontur.mobile.visualfsm:visualfsm-core:1.1.0"

    // Опционально - Поддержка RxJava 3 (FeatureRx, AsyncWorkerRx и их зависимости)
    implementation "ru.kontur.mobile.visualfsm:visualfsm-rxjava3:1.1.0"

    // Опционально - Поддержка RxJava 2 (FeatureRx, AsyncWorkerRx и их зависимости)
    implementation "ru.kontur.mobile.visualfsm:visualfsm-rxjava2:1.1.0"

    // Кодогенерация
    ksp "ru.kontur.mobile.visualfsm:visualfsm-compiler:1.1.0"

    // Опционально - Классы для удобного получения сгенерированного кода
    implementation "ru.kontur.mobile.visualfsm:visualfsm-providers:1.1.0"

    // Опционально - Анализ и построение графа
    testImplementation "ru.kontur.mobile.visualfsm:visualfsm-tools:1.1.0"
}
```

</details>
<details>
  <summary>Kotlin(build.gradle.kts)</summary>

```kotlin
// Подключаем KSP плагин
plugins {
    id("com.google.devtools.ksp") version "1.6.10-1.0.6"
}

// Добавляем сгенерированный код в каталоги исходного кода
kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}

dependencies {
    // Базовые классы для Android, JVM и KMM проектов (Kotlin Coroutines версия Feature и AsyncWorker)
    implementation("ru.kontur.mobile.visualfsm:visualfsm-core:1.1.0")

    // Опционально - Поддержка RxJava 3 (FeatureRx, AsyncWorkerRx и их зависимости)
    implementation("ru.kontur.mobile.visualfsm:visualfsm-rxjava3:1.1.0")

    // Опционально - Поддержка RxJava 2 (FeatureRx, AsyncWorkerRx и их зависимости)
    implementation("ru.kontur.mobile.visualfsm:visualfsm-rxjava2:1.1.0")

    // Кодогенерация
    ksp("ru.kontur.mobile.visualfsm:visualfsm-compiler:1.1.0")

    // Опционально - Классы для удобного получения сгенерированного кода
    implementation("ru.kontur.mobile.visualfsm:visualfsm-providers:1.1.0")

    // Опционально - Анализ и построение графа
    testImplementation("ru.kontur.mobile.visualfsm:visualfsm-tools:1.1.0")
}
```

</details>

## Простой пример использования библиотеки

### SampleFSMState.kt

```kotlin
sealed class SampleFSMState : State {

    object Initial: SampleFSMState()

    object Loading: SampleFSMState()

    data class Success(val result: String): SampleFSMState()
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
@GenerateTransitionsFactory // аннотация для генерации TransitionsFactory
// Используйте Feature для Kotlin Coroutines или FeatureRx для RxJava
class SampleFSMFeature : Feature<SampleFSMState, SampleFSMAction>(
    initialState = SampleFSMState.Initial,
    asyncWorker = SampleFSMAsyncWorker(),
    transitionCallbacks = SampleTransitionCallbacks(),
    transitionsFactory = provideTransitionsFactory(), // Получаем экземпляр сгенерованной TransitionsFactory
    // Получение экземпляра сгенерованной TransitionsFactory для не JVM и не Android проектов.
    // До первого запуска кодогенерации класс не будет виден в IDE.
    // transitionsFactory = GeneratedSampleFSMFeatureTransitionsFactory(),
)
```

### Main.kt

```kotlin
fun main() {
    val sampleFeature = SampleFSMFeature()
    sampleFeature.proceed(StartLoading())
    // Дожидаемся выполнения асинхронной операции.
    // Иначе программа завершит работу до выполнения перехода "Loading->Success".
    Thread.sleep(3100)
}
```

<details>
  <summary>Вывод в консоль после завершения работы приложения</summary>

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

## Другие примеры

### Пример для JVM и Android приложений смотрите [здесь](../../sample/src/main/kotlin/authFSM/AuthFSMFeature.kt)

### Пример для KMM проекта появится в скором времени