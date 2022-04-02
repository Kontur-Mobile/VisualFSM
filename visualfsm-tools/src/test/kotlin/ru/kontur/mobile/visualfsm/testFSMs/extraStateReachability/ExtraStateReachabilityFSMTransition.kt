package ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability

import ru.kontur.mobile.visualfsm.Transition
import kotlin.reflect.KClass

abstract class ExtraStateReachabilityFSMTransition<FROM : ExtraStateReachabilityFSMState, TO : ExtraStateReachabilityFSMState>(
    override val fromState: KClass<FROM>,
    override val toState: KClass<TO>
) : Transition<FROM, TO>(fromState, toState)