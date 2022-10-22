package ru.kontur.mobile.visualfsm.backStateStackTests

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.backStateStackTests.rx.TestFSMAsyncWorkerRx
import ru.kontur.mobile.visualfsm.backStateStackTests.rx.TestFSMFeatureRx
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.TestFSMWBSState
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.action.Cancel
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.action.Start

class BackStateStackTests {

    @Test
    fun featureBackTest() {
        val feature = TestFSMFeatureRx(TestFSMWBSState.Initial, TestFSMAsyncWorkerRx())

        assertEquals(TestFSMWBSState.Initial, feature.getCurrentState())

        feature.proceed(Start("async1", 1))

        assertEquals(TestFSMWBSState.Async("async1", 1), feature.getCurrentState())

        feature.proceed(Cancel())

        assertEquals(TestFSMWBSState.Initial, feature.getCurrentState())
    }
}