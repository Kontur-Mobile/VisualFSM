package ru.kontur.mobile.visualfsm.compiler_plugin

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirClassLikeDeclaration
import org.jetbrains.kotlin.fir.declarations.utils.nameOrSpecialName
import org.jetbrains.kotlin.fir.expressions.UnresolvedExpressionTypeAccess
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.FirSupertypeGenerationExtension
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.types.constructClassLikeType
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class AddSupertypeWithTransitionFunctions(
    private val messageCollector: MessageCollector,
    session: FirSession,
) : FirSupertypeGenerationExtension(session) {

    init {
        messageCollector.logWarning("AddSupertypeWithTransitionFunctions inited ${session::class.simpleName} ($session)")
    }

    private val classesToInterfaces by lazy {
        session.predicateBasedProvider.getSymbolsByPredicate(PREDICATE)
            .filterIsInstance<FirRegularClassSymbol>()
            .associateWith {
                ClassId(
                    packageFqName = it.classId.packageFqName,
                    topLevelName = Name.identifier("${it.classId.shortClassName.identifier}Transitions")
                )
            }
    }

    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(PREDICATE)
    }

    @OptIn(UnresolvedExpressionTypeAccess::class)
    override fun needTransformSupertypes(declaration: FirClassLikeDeclaration): Boolean {
        return declaration.symbol in classesToInterfaces.keys
    }

    @OptIn(SymbolInternals::class)
    override fun computeAdditionalSupertypes(
        classLikeDeclaration: FirClassLikeDeclaration,
        resolvedSupertypes: List<FirResolvedTypeRef>,
        typeResolver: TypeResolveService,
    ): List<ConeKotlinType> {

        messageCollector.logWarning("computeAdditionalSupertypes started for ${classLikeDeclaration.nameOrSpecialName.asString()}")

        val interfaceClassId = classesToInterfaces[classLikeDeclaration.symbol] ?: return emptyList()

        return listOf(interfaceClassId.constructClassLikeType())
    }


    companion object {
        private val PREDICATE = LookupPredicate.create {
            annotated(FqName("ru.kontur.mobile.visualfsm.VisualFSMAction"))
        }
    }
}