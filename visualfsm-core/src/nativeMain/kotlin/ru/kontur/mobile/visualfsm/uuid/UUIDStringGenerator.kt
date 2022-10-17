package ru.kontur.mobile.visualfsm.uuid

import kotlinx.uuid.UUID

actual object UUIDStringGenerator {
    actual fun randomUUID(): String {
        return UUID().toString()
    }
}