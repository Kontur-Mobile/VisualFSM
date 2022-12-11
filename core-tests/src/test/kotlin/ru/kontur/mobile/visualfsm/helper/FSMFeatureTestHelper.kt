package ru.kontur.mobile.visualfsm.helper

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.Feature
import ru.kontur.mobile.visualfsm.State

@OptIn(ExperimentalCoroutinesApi::class)
fun <STATE : State, ACTION : Action<STATE>, FEATURE: Feature<STATE, ACTION>> runFSMFeatureTest(
    featureFactory: (TestDispatcher) -> FEATURE,
    block: TestScope.(
        feature: FEATURE,
        states: List<State>
    ) -> Unit
) {
    runTest {
        val states = mutableListOf<State>()
        val feature = featureFactory(StandardTestDispatcher(testScheduler))

        val statesJob = launch {
            feature.observeState().toList(states)
        }
        advanceUntilIdle()

        block(feature, states)

        statesJob.cancel()
    }
}