[ENG](../Quickstart.md) | RUS

# Первичная настройка

## Как подключить библиотеку

### Для Android приложения

#### В gradle скрипте модуля, в котором будет реализация класса Feature

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

### Если используете ProGuard и модуль visualfsm-providers, добавьте исключение в proguard-rules.pro
```
-keep class * implements ru.kontur.mobile.visualfsm.TransitionsFactory
```

## Простой пример использования библиотеки

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
@GenerateTransitionsFactory // аннотация для генерации TransitionsFactory
// Используйте Feature для Kotlin Coroutines или FeatureRx для RxJava
class SampleFSMFeature : Feature<SampleFSMState, SampleFSMAction>(
    initialState = SampleFSMState.Initial,
    asyncWorker = SampleFSMAsyncWorker(),
    transitionCallbacks = SampleTransitionCallbacks(),
    transitionsFactory = provideTransitionsFactory(), // Получение экземпляра сгенерованной TransitionsFactory при использовании visualfsm-providers
    // Для получения экземпляра TransitionsFactory для KMM проектов следует вызвать конструктор сгенерированного класса:
    // Имя генерируется по маске Generated[FeatureName]TransitionsFactory()
    // transitionsFactory = GeneratedSampleFSMFeatureTransitionsFactory(), // До первого запуска кодогенерации класс не будет виден в IDE.
)
```

### Использование Feature

```kotlin
// Подписка на состояния в Feature
sampleFeature.observeState().collect { state -> }

// Подписка на состояния в FeatureRx
sampleFeature.observeState().subscribe { state -> }

// Выполнение Action
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

### Вывод метода generateDigraph()

```
digraph SampleFSMStateTransitions {
"Initial"
"Initial" -> "Loading" [label=" StartLoading"]
"Loading" -> "Completed" [label=" Success"]
}
```

Для визуализации на компьютере разработчика используйте [webgraphviz](http://www.webgraphviz.com/).

## Демонстрационные проекты

### [Android приложение (Kotlin Coroutines, Jetpack Compose)](https://github.com/Kontur-Mobile/VisualFSM-Sample-Android)

### [Command line Kotlin приложение (Kotlin Coroutines)](../../sample)

### [Command line Kotlin приложение (RxJava)](../../sample-rx)

### Пример для KMM проекта появится в скором времени