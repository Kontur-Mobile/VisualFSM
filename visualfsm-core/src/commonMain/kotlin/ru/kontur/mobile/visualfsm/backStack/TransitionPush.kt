package ru.kontur.mobile.visualfsm.backStack

import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.Transition

/**
 * Describes the transition rule between states.
 * This transition works with the back stack of states, after its execution,
 * the old state will be added to the back stack of states.
 * In generic contains [initial state][fromState] and [destination state][toState].
 * Defines [predicate] and [transform] functions
 */
abstract class TransitionPush<FROM : State, TO : State> : Transition<FROM, TO>()