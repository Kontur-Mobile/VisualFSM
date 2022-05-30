package ru.kontur.mobile.visualfsm

abstract class GeneratedActionFactory<ACTION : Action<*>> {
    abstract fun create(action: ACTION): ACTION
}