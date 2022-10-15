package ru.kontur.mobile.visualfsm.backStack

import ru.kontur.mobile.visualfsm.Transition

/**
 * Interface for marking the need to add state to the back stack and clear back stack use it on [Transition] classes
 */
interface ToBackStackNewRoot: ToBackStack