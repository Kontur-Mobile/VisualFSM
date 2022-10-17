package ru.kontur.mobile.visualfsm.uuid

@JsModule("uuid")
@JsNonModule
external val uuid: dynamic
actual object UUIDStringGenerator {
    actual fun randomUUID(): String {
        return uuid.v4()
    }
}