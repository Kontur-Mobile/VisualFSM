package ru.kontur.mobile.visualfsm.testFSMs.sealedActionState.actions

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.testFSMs.sealedActionState.SealedActionStateFSMState

sealed class BaseSealedStateFSMAction : Action<SealedActionStateFSMState>()