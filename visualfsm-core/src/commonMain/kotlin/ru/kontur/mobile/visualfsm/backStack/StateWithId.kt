package ru.kontur.mobile.visualfsm.backStack

import ru.kontur.mobile.visualfsm.State

data class StateWithId<STATE: State>(val id: Int, val state: STATE)