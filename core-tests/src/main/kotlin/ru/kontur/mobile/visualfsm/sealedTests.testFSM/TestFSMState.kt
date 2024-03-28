package ru.kontur.mobile.visualfsm.sealedTests.testFSM

import ru.kontur.mobile.visualfsm.State

sealed class TestFSMState : State {

    data class Initial(
        val count: Int,
    ) : TestFSMState()

    sealed class NavigationState : TestFSMState() {
        abstract val count: Int

        sealed class Screen : NavigationState() {

            data class Back(
                override val count: Int,
            ) : Screen()

            data class Next(
                override val count: Int,
            ) : Screen()
        }

        sealed class DialogState : NavigationState() {
            data class Show(
                override val count: Int,
            ) : DialogState()

            data class Hide(
                override val count: Int,
            ) : DialogState()

            fun copySealed(
                count: Int,
            ): DialogState {
                return when (this) {
                    is Hide -> copy(count = count)
                    is Show -> copy(count = count)
                }
            }
        }
    }
}