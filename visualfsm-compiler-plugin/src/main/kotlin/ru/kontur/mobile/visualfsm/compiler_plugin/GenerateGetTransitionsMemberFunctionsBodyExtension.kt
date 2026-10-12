package ru.kontur.mobile.visualfsm.compiler_plugin

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.impl.IrClassReferenceImpl
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.render
import org.jetbrains.kotlin.ir.visitors.IrVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class GenerateGetTransitionsMemberFunctionsBodyExtension(
    private val pluginContext: IrPluginContext,
    private val classToTransitionFunctions: Map<IrClass, List<IrSimpleFunction>>,
) : IrVisitorVoid() {

    private val messageCollector = pluginContext.messageCollector

    init {
        pluginContext.messageCollector.logWarning("GenerateGetTransitionsMemberFunctionsBodyExtension inited")
    }

    override fun visitElement(element: IrElement) {
        if (element !is IrFunction) element.acceptChildrenVoid(this)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitSimpleFunction(declaration: IrSimpleFunction) {

        val origin = declaration.origin
        when {
            origin !is IrDeclarationOrigin.GeneratedByPlugin -> return
            origin.pluginKey != AddGetTransitionsMemberFunctionsExtension.KEY -> return
        }

        val transitionClassCompanionId = ClassId.fromString("ru/kontur/mobile/visualfsm/Transition2.Companion")

        val transitionClassCompanionSymbol = pluginContext.referenceClass(transitionClassCompanionId)!!

        val getTransitionCallableId = CallableId(transitionClassCompanionId, Name.identifier("getInstance"))

        val getTransitionFunction = pluginContext.referenceFunctions(getTransitionCallableId).first()

        val declarationParent = declaration.parent as IrClass

        val listOfFunctionCallableId = CallableId(StandardNames.COLLECTIONS_PACKAGE_FQ_NAME, Name.identifier("listOf"))

        val listOfFunctions = pluginContext.referenceFunctions(listOfFunctionCallableId)

        val varargListOfFunction = listOfFunctions.first { function ->
            function.owner.parameters.firstOrNull()?.varargElementType != null
        }

        val transitionType = (declaration.returnType as IrSimpleType).arguments.first().typeOrFail

        val resultBody = DeclarationIrBuilder(pluginContext, declaration.symbol).irBlockBody {
            +irReturn(
                irCall(varargListOfFunction, declaration.returnType).apply {
                    typeArguments.clear()
                    typeArguments += declaration.returnType
                    arguments[varargListOfFunction.owner.parameters.first()] = irVararg(
                        elementType = transitionType,
                        values = getGetTransitionFunctionCalls(
                            declarationParent,
                            declaration,
                            getTransitionFunction,
                            transitionClassCompanionSymbol
                        )
                    )
                }
            )
        }

        resultBody.acceptChildrenVoid(LogVisitor("|"))

        messageCollector.logWarning("body2 render: ${resultBody.render()}")
        messageCollector.logWarning("body2 dump: ${resultBody.dump()}")
        messageCollector.logWarning("body2 dump kotlin like: ${resultBody.dumpKotlinLike()}")

        declaration.body = resultBody
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun IrBlockBodyBuilder.getGetTransitionFunctionCalls(
        declarationParent: IrClass,
        declaration: IrSimpleFunction,
        getTransitionFunction: IrSimpleFunctionSymbol,
        transitionClassCompanionSymbol: IrClassSymbol,
    ): List<IrCall> = classToTransitionFunctions[declarationParent]!!.map { transitionFunction ->
        val fromStateType =
            transitionFunction.parameters[1].type // TODO Сделать корректную детекцию, а не просто брать второй
        messageCollector.logWarning("transitionFunction.parameters: ${transitionFunction.parameters.joinToString { it.render() }}")
        val startStateClassReference = IrClassReferenceImpl(
            startOffset = UNDEFINED_OFFSET,
            endOffset = UNDEFINED_OFFSET,
            type = pluginContext.irBuiltIns.kClassClass.typeWith(fromStateType),
            symbol = fromStateType.classOrFail,
            classType = fromStateType
        )
        val kFunctionType =
            pluginContext.irBuiltIns.kFunctionN(1).typeWith(fromStateType, transitionFunction.returnType)
        val transitionFunctionReference = irFunctionReference(
            kFunctionType,
            transitionFunction.symbol,
        ).apply {
            dispatchReceiver = irGet(declaration.dispatchReceiverParameter!!)
        }
        irCall(getTransitionFunction).apply {
            typeArguments.clear()
            typeArguments += fromStateType
            typeArguments += transitionFunction.returnType
            dispatchReceiver = irGetObjectValue(
                type = transitionClassCompanionSymbol.defaultType,
                classSymbol = transitionClassCompanionSymbol,
            )
            val fromStateValueParameter =
                getTransitionFunction.owner.parameters.first { it.name.asString() == "fromState" }
            val transformValueParameter =
                getTransitionFunction.owner.parameters.first { it.name.asString() == "transform" }
            arguments[fromStateValueParameter] = startStateClassReference
            arguments[transformValueParameter] = transitionFunctionReference
        }
    }

    inner class LogVisitor(private val prefix: String) : IrVisitorVoid() {
        override fun visitElement(element: IrElement) {
            messageCollector.logWarning("getTransitions body element: $prefix$element")
            element.acceptChildrenVoid(LogVisitor("$prefix---|"))
        }
    }
}

// Константа для аннотации @TransitionsFunction
val TRANSITIONS_FUNCTION_ANNOTATION_FQ_NAME = FqName("ru.kontur.mobile.visualfsm.TransitionsFunction")