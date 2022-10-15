package ru.kontur.mobile.visualfsm.testFSMWithBackStack

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.TestFSMWBSAsyncWorker
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.TestFSMWBSFeature
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.TestFSMWBSState
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.action.Cancel
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.action.Start

class BackStateStackTests {

    @Test
    fun featureBackTest() {
        val feature = TestFSMWBSFeature(TestFSMWBSState.Initial, TestFSMWBSAsyncWorker())

        assertEquals(TestFSMWBSState.Initial, feature.getCurrentState())

        feature.proceed(Start("async1", 1))

        assertEquals(TestFSMWBSState.Async("async1", 1), feature.getCurrentState())

        feature.proceed(Cancel())

        assertEquals(TestFSMWBSState.Initial, feature.getCurrentState())
    }
}