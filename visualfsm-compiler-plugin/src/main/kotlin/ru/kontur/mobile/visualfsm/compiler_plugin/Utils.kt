package ru.kontur.mobile.visualfsm.compiler_plugin

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.name.FqName

fun IrClass.isChildOf(fqName: FqName): Boolean {
    this.superTypes.forEach { irTypeOfSuper ->
        val irClassOfSuper = irTypeOfSuper.getClass() ?: return@forEach
        if (irClassOfSuper.fqNameWhenAvailable == fqName || irClassOfSuper.isChildOf(fqName)) {
            return true
        }
    }
    return false
}

fun MessageCollector.logWarning(message: String) {
    report(
        CompilerMessageSeverity.STRONG_WARNING,
        message
    )
}

fun MessageCollector.logError(message: String) {
    report(
        CompilerMessageSeverity.ERROR,
        message
    )
}

