package ru.kontur.mobile.visualfsm.compiler_plugin

import org.jetbrains.kotlin.CompilerVersionOfApiDeprecation
import org.jetbrains.kotlin.DeprecatedForRemovalCompilerApi
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactory2
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirFunctionReturnChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirRegularClassChecker
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirErrors
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.declarations.DirectDeclarationsAccess
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.declarations.utils.nameOrSpecialName
import org.jetbrains.kotlin.fir.expressions.FirAnnotationCall
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi
import org.jetbrains.kotlin.psi.KtClass

class VisualFSMCheckersExtension(
    private val messageCollector: MessageCollector,
    session: FirSession,
) : FirAdditionalCheckersExtension(session) {
    override val declarationCheckers: DeclarationCheckers = object : DeclarationCheckers() {
        override val regularClassCheckers: Set<FirRegularClassChecker>
            get() = setOf(VisualFSMClassChecker())
    }

    private class Kek : FirFunctionReturnChecker {

    }

    private inner class VisualFSMClassChecker : FirRegularClassChecker(MppCheckerKind.Common) {

        @OptIn(SymbolInternals::class, DirectDeclarationsAccess::class)
        context(context: CheckerContext, reporter: DiagnosticReporter)
        override fun check(declaration: FirRegularClass) {
            // Проверяем наличие аннотации @VisualFSMAction
            val hasAnnotation = declaration.annotations.any { annotation ->
                val annotationCall = annotation as? FirAnnotationCall ?: return@any false
                val typeRef = annotationCall.annotationTypeRef
                val classId = typeRef.coneType.classId?.asString()
                classId == "ru.kontur.mobile.visualfsm.VisualFSMAction"
            }

            if (!hasAnnotation) return

            // Находим соответствующий интерфейс <ClassName>Transitions
            val className = declaration.nameOrSpecialName.asString()
            val interfaceName = "${className}Transitions"
            val interfaceClassId = ClassId(
                declaration.symbol.classId.packageFqName,
                Name.identifier(interfaceName)
            )
            val interfaceSymbol =
                session.symbolProvider.getClassLikeSymbolByClassId(interfaceClassId) as? FirRegularClassSymbol

            if (interfaceSymbol == null) {
                messageCollector.logWarning(
                    "Interface $interfaceName not found for class $className"
                )
                return
            }

            // Сравниваем методы интерфейса с реализованными методами
            val interfaceMethods = interfaceSymbol.fir.declarations
                .filterIsInstance<FirSimpleFunction>()
                .associateBy { it.name.asString() } // Используем associateBy для доступа к FirSimpleFunction
            val implementedMethods = declaration.declarations
                .filterIsInstance<FirSimpleFunction>()
                .map { it.name.asString() }

            // Получаем PSI элемент для отчётности (если нужно)
            val psiElement = declaration.source?.psi as? KtClass

            // Проверяем наличие всех методов интерфейса
            interfaceMethods.keys.forEach { methodName ->
                if (methodName !in implementedMethods) {
                    val missingMethod = interfaceMethods[methodName]!!
                    @Suppress("UNCHECKED_CAST")
                    reporter.reportOn(
                        source = declaration.source,
                        factory = FirErrors.ABSTRACT_MEMBER_NOT_IMPLEMENTED as KtDiagnosticFactory2<FirRegularClass, FirSimpleFunction>,
                        a = declaration,
                        b = missingMethod,
                    )
                    messageCollector.report(
                        CompilerMessageSeverity.ERROR,
                        "Class $className must implement $methodName from $interfaceName"
                    )
                }
            }
        }
    }
}