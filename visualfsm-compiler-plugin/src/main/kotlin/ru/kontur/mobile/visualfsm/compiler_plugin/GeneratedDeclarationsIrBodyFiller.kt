/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package ru.kontur.mobile.visualfsm.compiler_plugin

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid

class GeneratedDeclarationsIrBodyFiller(private val messageCollector: MessageCollector) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        messageCollector.logWarning("GeneratedDeclarationsIrBodyFiller generate")
        val classToTransitionFunctions = mutableMapOf<IrClass, MutableList<IrSimpleFunction>>()
        val transformers = listOf(
            CollectGetTransitionsMemberFunctionsInfoExtension(pluginContext, classToTransitionFunctions),
            GenerateGetTransitionsMemberFunctionsBodyExtension(pluginContext, classToTransitionFunctions),
        )
        for (transformer in transformers) {
            messageCollector.logWarning("transformer: $transformer")
            moduleFragment.acceptChildrenVoid(transformer)
        }
    }
}
