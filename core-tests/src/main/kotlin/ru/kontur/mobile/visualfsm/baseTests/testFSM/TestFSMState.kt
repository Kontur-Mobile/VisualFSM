package ru.kontur.mobile.visualfsm.baseTests.testFSM

import ru.kontur.mobile.visualfsm.State

sealed class TestFSMState : State {
    object Initial : TestFSMState()

    class Async(
        val label: String,
        val milliseconds: Int,
        val salt: String = "",
    ) : TestFSMState() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Async

            if (label != other.label) return false
            if (milliseconds != other.milliseconds) return false

            return true
        }

        override fun hashCode(): Int {
            var result = label.hashCode()
            result = 31 * result + milliseconds
            return result
        }
    }

    data class Complete(
        val label: String,
        val salt: String = "",
    ) : TestFSMState()

    object Error : TestFSMState()
}