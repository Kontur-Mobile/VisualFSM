package ru.kontur.mobile.visualfsm.compiler_plugin

import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.expressions.*
import org.jetbrains.kotlin.fir.extensions.*
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate
import org.jetbrains.kotlin.fir.plugin.createMemberFunction
import org.jetbrains.kotlin.fir.plugin.createTopLevelClass
import org.jetbrains.kotlin.fir.render
import org.jetbrains.kotlin.fir.resolve.fqName
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.renderForDebugging
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.fir.types.type
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class GenerateTransitionInterfacesExtension(
    private val messageCollector: MessageCollector,
    session: FirSession,
) : FirDeclarationGenerationExtension(session) {

    private val interfaceToClass by lazy {
        session.predicateBasedProvider.getSymbolsByPredicate(PREDICATE).filterIsInstance<FirRegularClassSymbol>()
            .associateBy { classSymbol ->
                // val segments = classSymbol.classId.packageFqName.pathSegments().map { it.identifier } + "transitions"
                // val packageFqName = FqName.fromSegments(segments)
                ClassId(
                    packageFqName = classSymbol.classId.packageFqName,
                    topLevelName = Name.identifier("${classSymbol.classId.shortClassName.identifier}Transitions")
                )
            }
    }

    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(PREDICATE)
    }

    @ExperimentalTopLevelDeclarationsGenerationApi
    override fun getTopLevelClassIds(): Set<ClassId> {
        messageCollector.logWarning("getTopLevelClassIds started")
        messageCollector.logWarning("interfaceToClass keys: ${interfaceToClass.keys.joinToString()}")
        return interfaceToClass.keys
    }

    @OptIn(SymbolInternals::class)
    @ExperimentalTopLevelDeclarationsGenerationApi
    override fun generateTopLevelClassLikeDeclaration(classId: ClassId): FirClassLikeSymbol<*>? {
        messageCollector.logWarning("generateTopLevelClassLikeDeclaration started for ${classId.shortClassName}")
        val annotatedClass = interfaceToClass[classId] ?: run {
            messageCollector.logWarning("No annotated class found for $classId")
            return null
        }
        val actionAnnotation = annotatedClass.annotations.firstOrNull {
            it.fqName(session)?.asString() == "ru.kontur.mobile.visualfsm.VisualFSMAction"
        } as? FirAnnotationCall ?: run {
            messageCollector.logWarning("No VisualFSMAction annotation found for ${annotatedClass.classId}")
            return null
        }

        messageCollector.logWarning("actionAnnotation: ${actionAnnotation.render()}")

        val stateType = actionAnnotation.resolvedType
            .typeArguments
            .firstOrNull()
            ?.type
            ?: run {
                messageCollector.logError("Generic argument not found for VisualFSMAction in ${annotatedClass.classId.asString()}")
                return null
            }

        messageCollector.logWarning("stateType: ${stateType.renderForDebugging()}")

        val transitionFunctionCalls: List<FirFunctionCall> =
            when (val firstArgument = actionAnnotation.arguments.firstOrNull()) {
                is FirNamedArgumentExpression -> {
                    val arrayOfCall = firstArgument.unwrapArgument() as? FirFunctionCall
                    arrayOfCall?.arguments?.filterIsInstance<FirFunctionCall>() ?: emptyList()
                }

                is FirVarargArgumentsExpression -> {
                    firstArgument.arguments.filterIsInstance<FirFunctionCall>()
                }

                else -> actionAnnotation.arguments.filterIsInstance<FirFunctionCall>()
            }

        if (transitionFunctionCalls.isEmpty()) {
            messageCollector.logWarning("No transition functions found for ${annotatedClass.classId.asString()}. Skipping.")
            return null
        }

        messageCollector.logWarning("transitionFunctionCalls: ${transitionFunctionCalls.joinToString { it.render() }}")

        val transitionsInterface = createTopLevelClass(
            classId = classId,
            key = KEY,
            classKind = ClassKind.INTERFACE,
            config = {
                // Явно указываем, что интерфейс не final
                modality = Modality.ABSTRACT
            }
        )
        messageCollector.logWarning("Created class file for: ${classId.asSingleFqName()}")
        messageCollector.logWarning("transitionsInterface created: ${transitionsInterface.render()}")

        messageCollector.logWarning("Final transitionsInterface: ${transitionsInterface.render()}")
        messageCollector.logWarning("generateTopLevelClassLikeDeclaration: before return")
        return transitionsInterface.symbol
    }

    @OptIn(SymbolInternals::class)
    override fun getCallableNamesForClass(
        classSymbol: FirClassSymbol<*>,
        context: MemberGenerationContext,
    ): Set<Name> {
        val annotatedClass = interfaceToClass[classSymbol.classId] ?: run {
            return emptySet()
        }
        val actionAnnotation = annotatedClass.annotations.firstOrNull {
            it.fqName(session)?.asString() == "ru.kontur.mobile.visualfsm.VisualFSMAction"
        } as? FirAnnotationCall ?: run {
            messageCollector.logWarning("getCallableNamesForClass No VisualFSMAction annotation found for ${annotatedClass.classId}")
            return emptySet()
        }

        val transitionFunctionCalls: List<FirFunctionCall> =
            when (val firstArgument = actionAnnotation.arguments.firstOrNull()) {
                is FirNamedArgumentExpression -> {
                    val arrayOfCall = firstArgument.unwrapArgument() as? FirFunctionCall
                    arrayOfCall?.arguments?.filterIsInstance<FirFunctionCall>() ?: emptyList()
                }

                is FirVarargArgumentsExpression -> {
                    firstArgument.arguments.filterIsInstance<FirFunctionCall>()
                }

                else -> actionAnnotation.arguments.filterIsInstance<FirFunctionCall>()
            }

        val names = transitionFunctionCalls.map { transitionFunctionCall ->

            val transitionFunctionNameArg =
                transitionFunctionCall.arguments.getOrNull(1)?.unwrapArgument() as? FirLiteralExpression

            if (transitionFunctionNameArg == null) {
                messageCollector.logError("Invalid arguments in transitionFunctionCall: ${transitionFunctionCall.render()}")
                return emptySet()
            }

            val transitionFunctionName = transitionFunctionNameArg.value as? String ?: run {
                messageCollector.logError("Invalid transitionFunctionName: ${transitionFunctionCall.render()}")
                return emptySet()
            }

            Name.identifier(transitionFunctionName)
        }

        return names.toSet()
    }

    @OptIn(SymbolInternals::class)
    override fun generateFunctions(
        callableId: CallableId,
        context: MemberGenerationContext?,
    ): List<FirNamedFunctionSymbol> {
        messageCollector.logWarning("generateFunctions: ${callableId}")
        if (context == null) return emptyList()
        val annotatedClass = interfaceToClass[context.owner.classId] ?: return emptyList()
        val actionAnnotation = annotatedClass.annotations.firstOrNull {
            it.fqName(session)?.asString() == "ru.kontur.mobile.visualfsm.VisualFSMAction"
        } as? FirAnnotationCall ?: run {
            messageCollector.logWarning("No VisualFSMAction annotation found for ${annotatedClass.classId}")
            return emptyList()
        }

        messageCollector.logWarning("actionAnnotation: ${actionAnnotation.render()}")

        val stateType = actionAnnotation.resolvedType
            .typeArguments
            .firstOrNull()
            ?.type
            ?: run {
                messageCollector.logError("Generic argument not found for VisualFSMAction in ${annotatedClass.classId.asString()}")
                return emptyList()
            }

        val transitionFunctionCalls: List<FirFunctionCall> =
            when (val firstArgument = actionAnnotation.arguments.firstOrNull()) {
                is FirNamedArgumentExpression -> {
                    val arrayOfCall = firstArgument.unwrapArgument() as? FirFunctionCall
                    arrayOfCall?.arguments?.filterIsInstance<FirFunctionCall>() ?: emptyList()
                }

                is FirVarargArgumentsExpression -> {
                    firstArgument.arguments.filterIsInstance<FirFunctionCall>()
                }

                else -> actionAnnotation.arguments.filterIsInstance<FirFunctionCall>()
            }

        val map: Map<String, ConeKotlinType> = transitionFunctionCalls.associate { transitionFunctionCall ->

            val startStateArg = transitionFunctionCall.arguments.getOrNull(0)?.unwrapArgument() as? FirGetClassCall
            val transitionFunctionNameArg =
                transitionFunctionCall.arguments.getOrNull(1)?.unwrapArgument() as? FirLiteralExpression

            if (startStateArg == null || transitionFunctionNameArg == null) {
                error("Invalid arguments in transitionFunctionCall: ${transitionFunctionCall.render()}")
            }

            val startStateExpr = startStateArg.argument
            messageCollector.logWarning("startStateExpr: ${startStateExpr.render()}")

            val transitionFunctionName = transitionFunctionNameArg.value as? String ?: run {
                error("Invalid transitionFunctionName: ${transitionFunctionCall.render()}")
            }

            transitionFunctionName to startStateExpr.resolvedType
        }

        val function = createMemberFunction(
            owner = context.owner,
            key = KEY,
            name = callableId.callableName,
            returnType = stateType,
            config = {
                // Указываем, что метод абстрактный
                modality = Modality.ABSTRACT
                valueParameter(
                    name = Name.identifier("startState"),
                    type = map[callableId.callableName.identifier]!!
                )
            }
        )
        return listOf(function.symbol)
    }

    private object KEY : GeneratedDeclarationKey() {
        override fun toString(): String {
            return "GenerateTransitionInterfacesKey"
        }
    }

    companion object {
        private val PREDICATE = LookupPredicate.create {
            annotated(FqName("ru.kontur.mobile.visualfsm.VisualFSMAction"))
        }
    }
}

private fun FirExpression.unwrapArgument(): FirExpression {
    return if (this is FirNamedArgumentExpression) this.expression else this
}