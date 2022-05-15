package ru.kontur.mobile.visualfsm.testFSM

import ru.kontur.mobile.visualfsm.FeatureRx
import ru.kontur.mobile.visualfsm.testFSM.action.Start
import ru.kontur.mobile.visualfsm.testFSM.action.Stop
import ru.kontur.mobile.visualfsm.testFSM.action.TestFSMAction

class TestFeature : FeatureRx<TestFSMState, TestFSMAction>(TestFSMStore(), TestFSMAsyncWorker()) {
    fun start() {
        proceed(Start())
    }

    fun stop() {
        proceed(Stop())
    }
}