package ru.kontur.mobile.visualfsm.baseTests.testFSM.action

import ru.kontur.mobile.visualfsm.TransitionsFunction
import ru.kontur.mobile.visualfsm.baseTests.testFSM.TestFSMState

class Finish(val success: Boolean, val salt: String = "") : TestFSMAction() {

    sealed interface FinishActionTransitionReturnValue

    @TransitionsFunction<TestFSMState>(TestFSMState.Complete::class, TestFSMState.Initial::class)
    fun transition(startState: TestFSMState.Async): TestFSMState {
        Thread.sleep(10)
        return if (success) {
            TestFSMState.Complete(startState.label, salt)
        } else {
            TestFSMState.Error
        }
    }

    private fun joja(startState: TestFSMState.Complete): TestFSMState {
        return if (startState.label == "kek") {
            TestFSMState.Complete(startState.label, salt)
        } else {
            joja(startState.copy(label = "kek"))
        }
    }

    @TransitionsFunction
    fun transition2(startState: TestFSMState.Complete): TestFSMState {
        // Thread.sleep(10)
        var state1: TestFSMState = if (startState.label == "kek") {
            TestFSMState.Complete(startState.label, salt)
        } else {
            TestFSMState.Initial
        }
        if (startState.label == "123") {
            state1 = TestFSMState.Error
        }
        return joja(startState)
        // if (startState.salt == "kek") {
        //     return TestFSMState.Error
        // } else {
        //     return TestFSMState.Initial
        // }
        // return try {
        //     TestFSMState.Complete(startState.label, salt)
        // } catch (e: Exception) {
        //     TestFSMState.Error
        // } catch (t: Throwable) {
        //     TestFSMState.Foo.Foo1
        // } finally {
        //     TestFSMState.Initial
        // }
        // return when {
        //     success -> TestFSMState.Complete(startState.label, salt)
        //     salt == "kek" -> TestFSMState.Error
        //     else -> TestFSMState.Foo.Foo1
        // }
        // return if (success) {
        //     TestFSMState.Complete(startState.label, salt)
        // } else {
        //     return if (salt == "kek") {
        //         TestFSMState.Error
        //     } else {
        //         TestFSMState.Foo.Foo1
        //     }
        // }
        // return TestFSMState.Foo.Foo1
    }

    // override fun getTransitions(): List<Transition2<out TestFSMState, out TestFSMState>> {
    //     return listOf(
    //         Transition2.getInstance(fromState = TestFSMState.Async::class, transform = this::transition),
    //         Transition2.getInstance(fromState = TestFSMState.Complete::class, transform = this::transition2),
    //     )
    // }
}

class Foo1
class Foo2
class Foo3
class Foo4
class Foo5
class Foo6
class Foo7

fun foo(flag: Boolean, flag2: Boolean, integer: Int): Any {
    if (flag) return Foo1()
    run { return@run Foo6() }
    return when (integer) {
        0 -> Foo3()
        1 -> run { return@run Foo4() }
        2 -> {
            Foo5()
        }

        else -> if (flag2) Foo1() else return Foo2()
    }
}