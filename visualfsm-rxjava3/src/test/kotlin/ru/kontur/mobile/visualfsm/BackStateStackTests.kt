package ru.kontur.mobile.visualfsm

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.backStack.BackStackStrategy
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.TestFSMAsyncWorker
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.TestFSMFeature
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.TestFSMState
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.action.Cancel
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.action.Start

class BackStateStackTests {

    @Test
    fun featureBackTest() {
        val feature = TestFSMFeature(TestFSMState.Initial, TestFSMAsyncWorker())

        assertEquals(TestFSMState.Initial, feature.getCurrentState())

        feature.proceed(Start("async1", 1))

        assertEquals(TestFSMState.Async("async1", 1), feature.getCurrentState())

        feature.proceed(Cancel())

        assertEquals(TestFSMState.Initial, feature.getCurrentState())
    }
}