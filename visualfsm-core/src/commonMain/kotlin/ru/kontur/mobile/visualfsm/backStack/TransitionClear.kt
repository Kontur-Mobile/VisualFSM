package ru.kontur.mobile.visualfsm.backStack

import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.Transition

/**
 * Describes the transition rule between states.
 * This transition works with the back stack of states, after its execution the state stack will be cleared.
 * In generic contains [initial state][fromState] and [destination state][toState].
 * Defines [predicate] and [transform] functions
 */
abstract class TransitionClear<FROM : State, TO : State> : Transition<FROM, TO>()