package ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability

import ru.kontur.mobile.visualfsm.Transition
import kotlin.reflect.KClass

abstract class AllStatesReachabilityFSMTransition<FROM : AllStatesReachabilityFSMState, TO : AllStatesReachabilityFSMState>(
    override val fromState: KClass<FROM>,
    override val toState: KClass<TO>
) : Transition<FROM, TO>(fromState, toState)