package ru.kontur.mobile.visualfsm

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.testFSM.TestFSMAsyncWorker
import ru.kontur.mobile.visualfsm.testFSM.TestFSMFeature
import ru.kontur.mobile.visualfsm.testFSM.TestFSMState
import ru.kontur.mobile.visualfsm.testFSM.action.Start
import kotlin.reflect.KClass

class StateDependencyManagerTests {

    @Test
    fun stateDependencyTest() {
        val stateDependencyInited: MutableList<Pair<Int, KClass<out TestFSMState>>> = mutableListOf()
        val stateDependencyRemoved: MutableList<Pair<Int, KClass<out TestFSMState>>> = mutableListOf()


        val feature = TestFSMFeature(
            initialState = TestFSMState.Initial,
            asyncWorker = TestFSMAsyncWorker(),
            stateDependencyManager = object : StateDependencyManager<TestFSMState> {
                override fun initDependencyForState(id: Int, state: TestFSMState) {
                    stateDependencyInited.add(id to state::class)
                }

                override fun removeDependencyForState(id: Int, state: TestFSMState) {
                    stateDependencyRemoved.add(id to state::class)
                }
            })

        assertEquals(TestFSMState.Initial, feature.getCurrentState())

        feature.proceed(Start("async1", 1))

        assertEquals(TestFSMState.Async("async1", 1), feature.getCurrentState())

        Thread.sleep(100)

        assertEquals(TestFSMState.Complete("async1"), feature.getCurrentState())

        feature.back()

        assertEquals(TestFSMState.Initial, feature.getCurrentState())

        assertTrue(stateDependencyInited.containsAll(listOf(
            0 to TestFSMState.Initial::class,
            1 to TestFSMState.Async::class,
            2 to TestFSMState.Complete::class,
            )))

        assertTrue(stateDependencyRemoved.containsAll(listOf(
            1 to TestFSMState.Async::class,
            2 to TestFSMState.Complete::class,
        )))
    }
}