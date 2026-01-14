package ru.kontur.mobile.visualfsm.compiler_plugin

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.visitors.IrVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.name.FqName

class CollectGetTransitionsMemberFunctionsInfoExtension(
    pluginContext: IrPluginContext,
    private val classToTransitionFunctions: MutableMap<IrClass, MutableList<IrSimpleFunction>>,
) : IrVisitorVoid() {

    private val messageCollector = pluginContext.messageCollector

    override fun visitElement(element: IrElement) {
        if (element !is IrSimpleFunction) element.acceptChildrenVoid(this)
    }

    override fun visitSimpleFunction(declaration: IrSimpleFunction) {
        if (!declaration.hasAnnotation(FqName("ru.kontur.mobile.visualfsm.TransitionsFunction"))) return
        val functionClass = (declaration.parent as? IrClass) ?: return
        val functions = classToTransitionFunctions[functionClass]
        if (functions == null) {
            classToTransitionFunctions[functionClass] = mutableListOf(declaration)
        } else {
            functions += declaration
        }
        messageCollector.logWarning("CollectGetTransitionsMemberFunctionsInfoExtension ${declaration.name}")
    }
}