package ru.kontur.mobile.visualfsm.uuid

import java.util.UUID

actual object UUIDStringGenerator {
    actual fun randomUUID(): String {
        return UUID.randomUUID().toString()
    }
}