# <img src="../img/logo.png" alt="VisualFSM" height="192"/>

[![MavenCentral](https://img.shields.io/maven-central/v/ru.kontur.mobile.visualfsm/visualfsm-core)](https://search.maven.org/artifact/ru.kontur.mobile.visualfsm/visualfsm-core)
[![Telegram](https://img.shields.io/static/v1?label=Telegram&message=Channel&color=0088CC)](https://t.me/visualfsm)
[![Telegram](https://img.shields.io/static/v1?label=Telegram&message=Chat&color=0088CC)](https://t.me/visualfsm_support)

[ENG](../ExternalStateSources.md) | RUS

### Внешние источники состояний (опционально)
Возможность взаимодействия между Feature через общий источник состояний является опциональной,
это может быть полезно для взаимодействия между Features через общее состояние
или для взаимной работы с другими фреймворками, основанными на работе с состояниями.

Одним из вариантов взаимодействия между родительскими и дочерними Features является использование
единого дерева состояний, при этом дочерняя Feature может читать и управлять только своей веткой состояний,
а состояние родительской Feature всегда будет включать в себя актуальное состояние дочерней Feature.

Таким образом реализуется единый источник правды между несколькими Features.

Для реализации такого взаимодействия необходимо передать в конструктор дочерней Feature реализацию IStateSource
(или IStateSourceRx, если вы используете RxJava для асинхронной работы)

```kotlin
interface IStateSource<STATE : State> {

    /**
     * Provides a [flow][StateFlow] of [states][State]
     *
     * @return a [flow][StateFlow] of [states][State]
     */
    fun observeState(): StateFlow<STATE>

    /**
     * Returns current state
     *
     * @return current [state][State]
     */
    fun getCurrentState(): STATE

    /**
     * Update current state
     *
     * @param newState [State] for update
     */
    fun updateState(newState: STATE)
}
```

```kotlin
interface IStateSourceRx<STATE : State> {
    /**
     * Provides a [observable][Observable] of [states][State]
     *
     * @return a [observable][Observable] of [states][State]
     */
    fun observeState(): Observable<STATE>

    /**
     * Returns current state
     *
     * @return current [state][State]
     */
    fun getCurrentState(): STATE

    /**
     * Update current state
     *
     * @param newState [State] for update
     */
    fun updateState(newState: STATE)
}
```

Пример реализации IStateSourceRx
```kotlin
class ChildStateSource @Inject constructor(
    private val parentFeature: ParentFeature,
) : IStateSourceRx<ChildFSMState> {

    override fun observeState(): Observable<ChildFSMState> {
        return parentFeature.observeState().mapNotNull { (it as? ParentFSMState.StateWithChildStateField)?.childState }
    }

    override fun getCurrentState(): ChildFSMState {
        return (parentFeature.getCurrentState() as ParentFSMState.StateWithChildStateField).childState
    }

    override fun updateState(newState: ChildFSMState) {
        // метод updateChildState родительской Feature вызывает Action в котором обновляется поле childState состояния родительской Feature
        parentFeature.updateChildState(newState)
    }
}
```


В родительской Feature, если она является корневой, нет необходимости передавать внешнюю реализацию IStateSource