package ru.kontur.mobile.visualfsm.compiler_plugin

import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.getContainingClassSymbol
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.plugin.createMemberFunction
import org.jetbrains.kotlin.fir.resolve.getSuperTypes
import org.jetbrains.kotlin.fir.resolve.providers.FirSymbolProvider
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinTypeProjectionOut
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.constructType
import org.jetbrains.kotlin.fir.types.renderForDebugging
import org.jetbrains.kotlin.fir.types.type
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class AddGetTransitionsMemberFunctionsExtension(
    private val messageCollector: MessageCollector,
    session: FirSession,
) : FirDeclarationGenerationExtension(session) {

    @OptIn(SymbolInternals::class)
    private val actionClassToTransitionFunctions: Map<FirClassLikeSymbol<*>, List<FirNamedFunctionSymbol>> by lazy {
        val functions = session.predicateBasedProvider.getSymbolsByPredicate(TRANSITIONS_FUNCTIONS_PREDICATE)
            .filterIsInstance<FirNamedFunctionSymbol>()
        val result: MutableMap<FirClassLikeSymbol<*>, MutableList<FirNamedFunctionSymbol>> = mutableMapOf()
        functions.forEach { function ->
            var actionClass = function.getContainingClassSymbol() ?: run {
                // TODO Тут надо выдать ошибку, что функция не лежит внутри класса
                // TODO Ещё нужно сделать проверку, что функция лежит внутри Action класса
                messageCollector.logError(TODO())
                return@forEach
            }
            val actionClassFunctions = result[actionClass]
            if (actionClassFunctions == null) {
                result[actionClass] = mutableListOf(function)
            } else {
                actionClassFunctions += function
            }
        }
        result
    }

    override fun getCallableNamesForClass(
        classSymbol: FirClassSymbol<*>,
        context: MemberGenerationContext,
    ): Set<Name> {
        return if (actionClassToTransitionFunctions[classSymbol] != null) {
            setOf(Name.identifier("getTransitions"))
        } else {
            super.getCallableNamesForClass(classSymbol, context)
        }
    }

    override fun generateFunctions(
        callableId: CallableId,
        context: MemberGenerationContext?,
    ): List<FirNamedFunctionSymbol> {
        if (context == null) return super.generateFunctions(callableId, context)
        context.owner.resolvedSuperTypeRefs
        context.owner.resolvedSuperTypes
        val actionSuperType = context.owner.getSuperTypes(session).first { type ->
            type.classId?.asFqNameString() == "ru.kontur.mobile.visualfsm.Action"
        }
        messageCollector.logWarning("actionSuperType.typeArguments: ${actionSuperType.typeArguments.joinToString { it.toString() }}")
        messageCollector.logWarning("actionSuperType.typeArguments.first().type: ${actionSuperType.typeArguments.first().type}")

        val stateType = actionSuperType.typeArguments.first().type!!

        val symbolProvider: FirSymbolProvider = session.symbolProvider // Ваш провайдер символов

        val transitionClassId = ClassId.fromString("ru/kontur/mobile/visualfsm/Transition2")
        val listClassId = ClassId.topLevel(StandardNames.FqNames.list)

        val transitionSymbol = symbolProvider.getClassLikeSymbolByClassId(transitionClassId)!!
        val listSymbol = symbolProvider.getClassLikeSymbolByClassId(listClassId)!!

        val outAnyProjection = ConeKotlinTypeProjectionOut(stateType)

        val transitionType = transitionSymbol.constructType(
            typeArguments = arrayOf(outAnyProjection, outAnyProjection),
            isMarkedNullable = false
        )

        val listType = listSymbol.constructType(
            typeArguments = arrayOf(transitionType),
            isMarkedNullable = false
        )
        messageCollector.logWarning("transitionType: ${transitionType}")
        messageCollector.logWarning("transitionType render: ${transitionType.renderForDebugging()}")

        messageCollector.logWarning("listType: ${listType}")
        messageCollector.logWarning("listType render: ${listType.renderForDebugging()}")
        val function = createMemberFunction(
            owner = context.owner,
            key = KEY,
            name = callableId.callableName,
            returnType = listType,
        ) {
            status {
                isOverride = true
            }
        }
        return listOf(function.symbol)
    }

    object KEY : GeneratedDeclarationKey()

    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(TRANSITIONS_FUNCTIONS_PREDICATE)
    }

    companion object {
        private val TRANSITIONS_FUNCTIONS_PREDICATE = LookupPredicate.create {
            annotated(FqName("ru.kontur.mobile.visualfsm.TransitionsFunction"))
        }
    }
}