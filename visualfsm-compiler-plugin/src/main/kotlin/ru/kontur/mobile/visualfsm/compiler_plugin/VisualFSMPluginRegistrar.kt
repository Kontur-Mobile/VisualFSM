package ru.kontur.mobile.visualfsm.compiler_plugin

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.messageCollector
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter

@OptIn(ExperimentalCompilerApi::class)
class VisualFSMPluginRegistrar : CompilerPluginRegistrar() {
    override val supportsK2: Boolean = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        configuration.messageCollector.logWarning("Registration started")
        // configuration.messageCollector.report(
        //     CompilerMessageSeverity.ERROR,
        //     "Hello there!!!"
        // )
        FirExtensionRegistrarAdapter.registerExtension(SimpleFirPluginRegistrar(configuration.messageCollector))
        IrGenerationExtension.registerExtension(extension = GeneratedDeclarationsIrBodyFiller(configuration.messageCollector))
    }
}

// class MyReturnTypesFIRExtension(session: FirSession) : FirDeclarationGenerationExtension(session) {
//
//     @OptIn(SymbolInternals::class)
//     override fun generateFunctions(
//         callableId: CallableId,
//         context: MemberGenerationContext?
//     ): List<FirNamedFunctionSymbol> {
//         if (context == null) return emptyList()
//
//         // Получаем символ функции из declaredScope
//         val symbol = context.declaredScope
//             ?.getFunctions(callableId.callableName)
//             ?.firstOrNull { it.callableId == callableId }
//             ?: return emptyList()
//
//         val firFunction = symbol.fir as? FirSimpleFunction ?: return emptyList()
//         val scopeSession = context.scopeSession
//
//         // Используем ReturnTypeCalculatorWithJump для анализа типов возвращаемых значений
//         val returnExpressions = firFunction.body?.statements
//             ?.filterIsInstance<FirReturnExpression>()
//             ?.mapNotNull { it.result } ?: emptyList()
//
//         val allReturnTypes = returnExpressions
//             .flatMap { expr ->
//                 ReturnTypeCalculatorWithJump.computePossibleTypes(expr, session, scopeSession)
//             }
//             .toSet()
//
//         // Сохраняем результат в атрибут FIR-функции
//         val key = FirDeclarationAttributeKey<Set<ConeKotlinType>>("ALL_RETURN_TYPES")
//         firFunction.putUserData(key, allReturnTypes)
//
//         return emptyList()
//     }
// }


// class VisualFSMIrExtension(private val messageCollector: MessageCollector) : IrGenerationExtension {
//     override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
//         moduleFragment.transformChildrenVoid(
//             transformer = VisualFSMTransformer(
//                 messageCollector = messageCollector,
//                 pluginContext = pluginContext,
//             )
//         )
//     }
// }
//
// class VisualFSMTransformer(
//     private val messageCollector: MessageCollector,
//     private val pluginContext: IrPluginContext,
// ) : IrElementTransformerVoid() {
//
//     private val annotationFqName = FqName("ru.kontur.mobile.visualfsm.VisualFSMTransition")
//     private val stateFqName = FqName("ru.kontur.mobile.visualfsm.State")
//     private val transitionFqName = FqName("ru.kontur.mobile.visualfsm.Transition")
//     private val actionFqName = FqName("ru.kontur.mobile.visualfsm.Action")
//
//     @OptIn(UnsafeDuringIrConstructionAPI::class)
//     override fun visitClass(declaration: IrClass): IrStatement {
//         val irClass = super.visitClass(declaration) as IrClass
//
//         foo(irClass)
//         // generateGetTransitionsIfNeeded(irClass)
//         // generateMarkerInterfaceAndPatchTransitionReturnType(irClass)
//
//         return irClass
//     }
//
//     @OptIn(UnsafeDuringIrConstructionAPI::class)
//     private fun foo(irClass: IrClass) {
//         if (!irClass.isChildOf(actionFqName)) return
//         if (irClass.modality in setOf(Modality.SEALED, Modality.ABSTRACT)) return
//
//         val functions = irClass.declarations
//             .filterIsInstance<IrFunction>()
//
//         val kekFunction = functions.firstOrNull { it.name.asString() == "kek" } ?: return
//
//         val types = collectReturnTypesFromFunction(kekFunction)
//
//         messageCollector.logWarning(types.joinToString { it.classFqName?.asString() ?: "NO_NAME" })
//     }
//
//     fun collectReturnTypesFromFunction(fn: IrFunction): Set<IrType> {
//         fn.returnType
//         val result = mutableSetOf<IrType>()
//
//         fun walk(expr: IrElement) {
//             when (expr) {
//                 is IrReturn -> {
//                     if (expr.returnTargetSymbol == fn.symbol) {
//                         walk(expr.value)
//                     }
//                 }
//
//                 is IrWhen -> {
//                     expr.branches.forEach { walk(it.result) }
//                 }
//
//                 is IrReturnableBlock -> {
//                     var hasReturnFromFn = false
//
//                     expr.statements.forEach { stmt ->
//                         if (stmt is IrReturn && stmt.returnTargetSymbol == fn.symbol) {
//                             hasReturnFromFn = true
//                             walk(stmt.value)
//                         } else if (stmt is IrExpression) {
//                             walk(stmt)
//                         }
//                     }
//
//                     if (!hasReturnFromFn) {
//                         val lastExpr = expr.statements.lastOrNull() as? IrExpression
//                         lastExpr?.let { walk(it) }
//                     }
//                 }
//
//                 is IrBlock -> {
//                     val statements = expr.statements
//                     statements.dropLast(1).forEach { stmt ->
//                         if (stmt is IrReturn && stmt.returnTargetSymbol == fn.symbol) {
//                             walk(stmt.value)
//                         }
//                     }
//
//                     val lastExpr = statements.lastOrNull() as? IrExpression
//                     lastExpr?.let { walk(it) }
//                 }
//
//                 is IrConstructorCall,
//                 is IrGetObjectValue,
//                 is IrCall,
//                     -> {
//                     val type = (expr as IrExpression).type
//                     if (!type.isUnit()) {
//                         result += type
//                     }
//                 }
//
//                 is IrExpression -> {
//                     val type = expr.type
//                     if (!type.isUnit()) {
//                         result += type
//                     }
//                 }
//
//                 else -> throw Throwable("ACHTUNG!!! $expr")
//             }
//
//
//         }
//
//         val body = fn.body as? IrBlockBody ?: return emptySet()
//         body.statements.forEach { walk(it) }
//
//         return result.filterNot { it.isUnit() }.toSet()
//     }
//
//
//     @OptIn(UnsafeDuringIrConstructionAPI::class)
//     private fun generateGetTransitionsIfNeeded(irClass: IrClass) {
//         if (!irClass.isChildOf(actionFqName)) return
//         if (irClass.modality in setOf(Modality.SEALED, Modality.ABSTRACT)) return
//
//         val nestedTransitions = irClass.declarations
//             .filterIsInstance<IrClass>()
//             .filter { nestedClass -> nestedClass.isChildOf(transitionFqName) }
//
//         if (nestedTransitions.isEmpty()) {
//             messageCollector.report(
//                 severity = CompilerMessageSeverity.EXCEPTION,
//                 message = "Base action class must have subclasses. The ${irClass.name} does not meet this requirement."
//             )
//             return
//         }
//
//         val actionSuperType = irClass.superTypes.firstOrNull {
//             it.classOrNull?.owner?.fqNameWhenAvailable == actionFqName
//         } as? IrSimpleType ?: return
//
//         val stateProjection = actionSuperType.arguments.firstOrNull() ?: return
//         val stateType = (stateProjection as IrTypeProjection).type
//         val transitionSymbol = pluginContext.referenceClass(ClassId.topLevel(transitionFqName)) ?: return
//
//         val transitionType = IrSimpleTypeImpl(
//             classifier = transitionSymbol,
//             hasQuestionMark = false,
//             arguments = listOf(
//                 makeTypeProjection(stateType, Variance.OUT_VARIANCE),
//                 makeTypeProjection(stateType, Variance.OUT_VARIANCE)
//             ),
//             annotations = emptyList(),
//             abbreviation = null
//         )
//
//         val returnListType = pluginContext.irBuiltIns.listClass.typeWith(transitionType)
//
//         val newFunction = pluginContext.irFactory.buildFun {
//             name = Name.identifier("getTransitions")
//             returnType = returnListType
//             modality = Modality.OPEN
//             visibility = irClass.visibility
//             isFakeOverride = true
//         }.apply {
//             parent = irClass
//             body = pluginContext.irFactory.createBlockBody(startOffset, endOffset) {
//                 val listOfSymbol: IrSimpleFunctionSymbol = pluginContext
//                     .referenceFunctions(
//                         callableId = CallableId(FqName("kotlin.collections"), Name.identifier("listOf"))
//                     )
//                     .firstOrNull { it.owner.valueParameters.size == nestedTransitions.size }
//                     ?: return@createBlockBody
//
//                 // val listCall = IrCallImpl.fromSymbolOwner(startOffset, endOffset, returnListType, listOfSymbol).apply {
//                 //     putTypeArgument(0, transitionType)
//                 //     nestedTransitions.forEachIndexed { index, nestedClass ->
//                 //         val nestedClassConstructor = nestedClass.constructors.firstOrNull() ?: return@forEachIndexed
//                 //         val nestedClassConstructorCall = IrConstructorCallImpl.fromSymbolOwner(
//                 //             type = nestedClass.defaultType,
//                 //             constructorSymbol = nestedClassConstructor.symbol,
//                 //             origin = null
//                 //         )
//                 //         putValueArgument(index = index, valueArgument = nestedClassConstructorCall)
//                 //     }
//                 // }
//                 // statements += IrReturnImpl(
//                 //     startOffset = startOffset,
//                 //     endOffset = endOffset,
//                 //     type = pluginContext.irBuiltIns.nothingType,
//                 //     returnTargetSymbol = this@apply.symbol,
//                 //     value = listCall
//                 // )
//             }
//         }
//
//         irClass.declarations += newFunction
//         messageCollector.logWarning("Generated getTransitions() for ${irClass.name.asString()}")
//     }
//
//     @OptIn(UnsafeDuringIrConstructionAPI::class)
//     private fun generateMarkerInterfaceAndPatchTransitionReturnType(irClass: IrClass) {
//         val annotation = irClass.annotations.find {
//             it.symbol.owner.parentAsClass.fqNameWhenAvailable == annotationFqName
//         } ?: return
//
//         val stateInterface = pluginContext.referenceClass(classId = ClassId.topLevel(stateFqName))?.owner
//             ?: return
//
//         val toStates = extractToStates(annotation)
//         if (toStates.isEmpty()) return
//
//         val newInterfaceName = toStates.joinToString(separator = "Or") {
//             it.kotlinFqName.asString()
//                 .removePrefix(it.packageFqName?.asString()?.plus('.') ?: "")
//                 .replace('.', '_')
//         }
//
//         val markerInterface = MarkerInterfaceBuilder(
//             pluginContext = pluginContext,
//             name = newInterfaceName,
//             packageFragment = toStates.first().getPackageFragment(),
//             stateInterface = stateInterface
//         ).build()
//
//         toStates.forEach { klass ->
//             klass.superTypes += markerInterface.symbol.defaultType
//         }
//
//         val transitionSuperType = irClass.superTypes.firstOrNull { irType ->
//             irType.classOrNull?.owner?.fqNameWhenAvailable == transitionFqName
//         } ?: return
//
//         val original = transitionSuperType as? IrSimpleType ?: return
//
//         val newSuperType = IrSimpleTypeImpl(
//             classifier = original.classifier,
//             hasQuestionMark = original.isNullable(),
//             arguments = listOf(
//                 original.arguments[0],
//                 makeTypeProjection(markerInterface.defaultType, Variance.OUT_VARIANCE)
//             ),
//             annotations = original.annotations,
//             abbreviation = original.abbreviation
//         )
//
//         val transformFn = irClass.functions.find { it.name.asString() == "transform" } ?: return
//         transformFn.returnType = markerInterface.defaultType
//
//         irClass.superTypes = listOf(newSuperType) + irClass.superTypes.drop(1)
//
//         messageCollector.logWarning("Patched TO type in Transition for class ${irClass.name.asString()}")
//     }
//
//     @OptIn(UnsafeDuringIrConstructionAPI::class)
//     private fun extractToStates(call: IrConstructorCall): List<IrClass> {
//         val arg = call.getValueArgument(1) as? IrVararg ?: return emptyList()
//         return arg.elements.mapNotNull {
//             (it as? IrClassReferenceImpl)?.symbol?.owner as? IrClass
//         }
//     }
// }
//
// class MarkerInterfaceBuilder(
//     private val pluginContext: IrPluginContext,
//     private val name: String,
//     private val packageFragment: IrPackageFragment,
//     private val stateInterface: IrClass,
// ) {
//     fun build(): IrClass {
//         return pluginContext.irFactory.buildClass(
//             builder = {
//                 name = Name.identifier(this@MarkerInterfaceBuilder.name)
//                 kind = ClassKind.INTERFACE
//                 visibility = DescriptorVisibilities.PUBLIC
//             }
//         ).apply {
//             parent = packageFragment
//             createImplicitParameterDeclarationWithWrappedDescriptor()
//             superTypes += stateInterface.defaultType
//             packageFragment.addChild(this)
//         }
//     }
// }
