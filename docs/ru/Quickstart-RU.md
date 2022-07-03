# Первичная настройка

## Как подключить основные классы

Базовые классы для Android, JVM и KMM проектов (Kotlin Coroutines версия Feature и AsyncWorker)

```kotlin
implementation("ru.kontur.mobile.visualfsm:visualfsm-core:1.1.0")
```

Поддержка RxJava 3 (FeatureRx, AsyncWorkerRx и их зависимости)

```kotlin
implementation("ru.kontur.mobile.visualfsm:visualfsm-rxjava3:1.1.0")
```

Поддержка RxJava 2 (FeatureRx, AsyncWorkerRx и их зависимости)

```kotlin
implementation("ru.kontur.mobile.visualfsm:visualfsm-rxjava2:1.1.0")
```

Инструменты для:

* Анализа и построения графа.
* Получения сгенерированных классов

```kotlin
testImplementation("ru.kontur.mobile.visualfsm:visualfsm-tools:1.1.0")
```

## Как подключить и настроить кодогенерацию

### Как подключить кодогенерацию

#### _Для Котлин приложения_

##### В gradle файле модуля, в котором будут использованы аннотации

<details>
  <summary>Groovy</summary>

```groovy
// Подключаем KSP плагин
plugins {
    id "com.google.devtools.ksp" version "1.6.21-1.0.6"
}

// Добавляем сгенерированный код в каталоги исходного кода
kotlin {
    sourceSets {
        main.kotlin.srcDirs += 'build/generated/ksp/main/kotlin'
        test.kotlin.srcDirs += 'build/generated/ksp/test/kotlin'
    }
}

dependencies {
    // Подключаем AnnotationProcessor
    ksp "ru.kontur.mobile.visualfsm:visualfsm-compiler:1.1.0"
    // Поключаем инструменты для удобного получения сгенерированного кода. Только для jvm проектов.
    implementation "ru.kontur.mobile.visualfsm:visualfsm-tools:1.1.0"
}
```

</details>
<details>
  <summary>Kotlin</summary>

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
    // Подключаем AnnotationProcessor
    ksp("ru.kontur.mobile.visualfsm:visualfsm-compiler:1.1.0")
    // Поключаем инструменты для удобного получения сгенерированного кода. Только для jvm проектов.
    implementation("ru.kontur.mobile.visualfsm:visualfsm-tools:1.1.0")
}
```

</details>

#### _Для Андроид приложения_

##### В gradle файле модуля, в котором будут использованы аннотации

<details>
  <summary>Groovy</summary>

```groovy
// Подключаем KSP плагин
plugins {
    id "com.google.devtools.ksp" version "1.6.21-1.0.6"
}

dependencies {
    // Подключаем AnnotationProcessor
    ksp "ru.kontur.mobile.visualfsm:visualfsm-compiler:1.1.0"
    // Поключаем инструменты для удобного получения сгенерированного кода.
    implementation "ru.kontur.mobile.visualfsm:visualfsm-tools:1.1.0"
}
```

</details>
<details>
  <summary>Kotlin</summary>

```kotlin
// Подключаем KSP плагин
plugins {
    id("com.google.devtools.ksp") version "1.6.10-1.0.6"
}

dependencies {
    // Подключаем AnnotationProcessor
    ksp("ru.kontur.mobile.visualfsm:visualfsm-compiler:1.1.0")
    // Поключаем инструменты для удобного получения сгенерированного кода.
    implementation("ru.kontur.mobile.visualfsm:visualfsm-tools:1.1.0")
}
```

</details>

##### В gradle файле app модуля

<details>
  <summary>Groovy</summary>

```groovy
// Добавляем сгенерированный код в каталоги исходного кода
android {
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
// Добавляем сгенерированный код в каталоги исходного кода
android.applicationVariants.all {
    kotlin {
        sourceSets {
            getByName(name) {
                kotlin.srcDir("build/generated/ksp/$name/kotlin")
            }
        }
    }
}
```

</details>

### Как настроить кодогенерацию

1. Пометить Feature класс аннотацией GenerateTransitionFactory
2. Передать в конструктор Feature параметр transitionFactory
    1. Для jvm проекта использовать функцию provideTransitionFactory
    2. Для не jvm проекта передавать экземпляр сгенерированного класса.
       Имя сгенерированного класса формируется как "Generated\*Feature\*TransitionFactory",
       где \*Feature\* это имя Feature класса помеченного аннотацией.

<details>
  <summary>Пример для jvm проекта</summary>

```kotlin
// Используйте Feature для Kotlin Coroutines или FeatureRx для RxJava
@GenerateTransitionFactory // аннотация для генерации TransitionFactory
class AuthFeature(initialState: AuthFSMState) : Feature<AuthFSMState, AuthFSMAction>(
    initialState = initialState,
    transitionFactory = provideTransitionFactory() // Получаем экземпляр сгенерованной TransitionFactory
)
```

</details>
<details>
  <summary>Пример для не jvm проекта</summary>

```kotlin
// Используйте Feature для Kotlin Coroutines или FeatureRx для RxJava
@GenerateTransitionFactory // аннотация для генерации TransitionFactory
class AuthFeature(initialState: AuthFSMState) : Feature<AuthFSMState, AuthFSMAction>(
    initialState = initialState,
    transitionFactory = GeneratedAuthFeatureTransitionFactory()
)
```

</details>