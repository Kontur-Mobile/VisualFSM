package ru.kontur.mobile.visualfsm.uuid

import platform.Foundation.NSUUID

actual object UUIDStringGenerator {
    actual fun randomUUID(): String {
        return NSUUID().UUIDString()
    }
}