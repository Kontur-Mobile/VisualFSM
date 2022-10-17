package ru.kontur.mobile.visualfsm.backStack

import ru.kontur.mobile.visualfsm.State

data class StateWithId<STATE: State>(val id: String, val state: STATE)