[ENG](../eng/Quickstart-ENG.md) | RUS

# Первичная настройка

## Dependencies connection

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

Анализа и построения графа.

```kotlin
testImplementation("ru.kontur.mobile.visualfsm:visualfsm-tools:1.1.0")
```

## Как подключить и настроить кодогенерацию

### Как подключить кодогенерацию

#### _Для Android приложения_

##### В gradle.build файле модуля, в котором будут использованы аннотации

<details>
  <summary>Groovy</summary>

```groovy
// Подключаем KSP плагин
plugins {
    id "com.google.devtools.ksp" version "$kspVersion"
}

dependencies {
    // Подключаем AnnotationProcessor
    ksp "ru.kontur.mobile.visualfsm:visualfsm-compiler:1.1.0"
    // Поключаем для удобного получения сгенерированного кода.
    implementation "ru.kontur.mobile.visualfsm:visualfsm-providers:1.1.0"
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
    // Поключаем для удобного получения сгенерированного кода.
    implementation("ru.kontur.mobile.visualfsm:visualfsm-providers:1.1.0")
}
```

</details>

##### В gradle.build файле app модуля

<details>
  <summary>Groovy</summary>

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
  <summary>Kotlin</summary>

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

#### _Для КММ проекта_

##### В gradle.build файле модуля, в котором будут использованы аннотации

<details>
  <summary>Kotlin</summary>

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
            implementation("ru.kontur.mobile.visualfsm:visualfsm-core:1.1.0")
            // Добавляем сгенерированный код в каталоги исходного кода
            kotlin.srcDir("${buildDir.absolutePath}/generated/ksp/")
        }
    }
}

dependencies {
    // Поключаем для удобного получения сгенерированного кода
    add("kspAndroid", "ru.kontur.mobile.visualfsm:visualfsm-compiler:1.1.0")
}
```

</details>

#### _Для других Kotlin приложений_

##### В gradle.build файле модуля, в котором будут использованы аннотации

<details>
  <summary>Groovy</summary>

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
    // Подключаем AnnotationProcessor
    ksp "ru.kontur.mobile.visualfsm:visualfsm-compiler:1.1.0"
    // Поключаем для удобного получения сгенерированного кода. Только для jvm проектов.
    implementation "ru.kontur.mobile.visualfsm:visualfsm-providers:1.1.0"
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
    // Поключаем для удобного получения сгенерированного кода. Только для jvm проектов.
    implementation("ru.kontur.mobile.visualfsm:visualfsm-providers:1.1.0")
}
```

</details>

### Как настроить кодогенерацию

1. Пометить Feature класс аннотацией GenerateTransitionsFactory
2. Передать в конструктор Feature параметр transitionsFactory
    1. Для jvm проекта использовать функцию provideTransitionsFactory
    2. Для не jvm проекта передавать экземпляр сгенерированного класса.
       Имя сгенерированного класса формируется как "Generated\*Feature\*TransitionsFactory",
       где \*Feature\* это имя Feature класса помеченного аннотацией.

<details>
  <summary>Пример для jvm проекта</summary>

```kotlin
// Используйте Feature для Kotlin Coroutines или FeatureRx для RxJava
@GenerateTransitionsFactory // аннотация для генерации TransitionsFactory
class AuthFeature(initialState: AuthFSMState) : Feature<AuthFSMState, AuthFSMAction>(
    initialState = initialState,
    transitionsFactory = provideTransitionsFactory() // Получаем экземпляр сгенерованной TransitionsFactory
)
```

</details>
<details>
  <summary>Пример для не jvm проекта</summary>

```kotlin
// Используйте Feature для Kotlin Coroutines или FeatureRx для RxJava
@GenerateTransitionsFactory // аннотация для генерации TransitionsFactory
class AuthFeature(initialState: AuthFSMState) : Feature<AuthFSMState, AuthFSMAction>(
    initialState = initialState,
    transitionsFactory = GeneratedAuthFeatureTransitionsFactory()
)
```

</details>