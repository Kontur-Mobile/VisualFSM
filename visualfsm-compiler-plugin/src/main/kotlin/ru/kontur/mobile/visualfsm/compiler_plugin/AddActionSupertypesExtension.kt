package ru.kontur.mobile.visualfsm.compiler_plugin

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactory0
import org.jetbrains.kotlin.diagnostics.Severity.ERROR
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirSimpleFunctionChecker
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.expressions.FirGetClassCall
import org.jetbrains.kotlin.fir.expressions.FirReturnExpression
import org.jetbrains.kotlin.fir.expressions.FirVarargArgumentsExpression
import org.jetbrains.kotlin.fir.extensions.FirExtensionApiInternals
import org.jetbrains.kotlin.fir.resolve.fqName
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.fir.visitors.FirVisitorVoid

// @OptIn(FirExtensionApiInternals::class)
// class AddActionSupertypesExtension(
//     private val messageCollector: MessageCollector,
//     session: FirSession
// ) : FirAdditionalCheckersExtension(session) {
//
//     override val declarationCheckers: DeclarationCheckers
//         get() {
//             val checker: FirSimpleFunctionChecker = object : FirSimpleFunctionChecker(MppCheckerKind.Common) {
//                 override fun check(
//                     declaration: FirSimpleFunction,
//                     context: CheckerContext,
//                     reporter: DiagnosticReporter,
//                 ) {
//                     val testAnnotation = declaration.annotations.firstOrNull { annotation ->
//                         messageCollector.logWarning(
//                             message = "Annotation name ${annotation.fqName(session)?.asString()}"
//                         )
//                         annotation.fqName(session)?.asString() == "ru.kontur.mobile.visualfsm.TestFuncAnnotation"
//                     }
//                     if (testAnnotation == null) return
//                     val arguments = testAnnotation.argumentMapping.mapping.entries
//                     val kek = if (arguments.isEmpty()) {
//                         listOf()
//                     } else {
//                         (testAnnotation.argumentMapping.mapping.entries.first().value as FirVarargArgumentsExpression).arguments
//                     }
//                     val allowedTypes = (kek as List<FirGetClassCall>).map { it.argument.resolvedType }
//                     messageCollector.logWarning(
//                         message = allowedTypes.toString()
//                     )
//                     declaration.body?.accept(object : FirVisitorVoid() {
//
//                         override fun visitElement(element: FirElement) {
//                             element.acceptChildren(this) // важно: рекурсивный обход
//                         }
//
//                         override fun visitReturnExpression(returnExpression: FirReturnExpression) {
//                             if (returnExpression.target.labeledElement != declaration) return
//                             messageCollector.logWarning(
//                                 message = "TEST!!!!!!!!"
//                             )
//                             val exprType = returnExpression.result.resolvedType
//                             messageCollector.logWarning(
//                                 message = exprType.toString()
//                             )
//
//                             if (exprType !in allowedTypes) {
//                                 reporter.reportOn(
//                                     source = returnExpression.source,
//                                     factory = KtDiagnosticFactory0(
//                                         name = "Kurwa",
//                                         severity = ERROR,
//                                         defaultPositioningStrategy = SourceElementPositioningStrategies.DECLARATION_RETURN_TYPE,
//                                         psiType = PsiElement::class
//                                     ),
//                                     context = context
//                                 )
//                             }
//
//                             // val exprType = exprType
//                             // if (!exprType.isAllowed()) {
//                             //     reporter.reportOn(
//                             //         returnExpression.source,
//                             //         MyErrors.RETURN_TYPE_NOT_ALLOWED,
//                             //         exprType.render(),
//                             //         context
//                             //     )
//                             // }
//
//                             // Если return возвращает сложное выражение — продолжаем обход
//                             returnExpression.result.accept(this)
//                         }
//                     })
//                 }
//
//             }
//             return object : DeclarationCheckers() {
//                 override val simpleFunctionCheckers = super.simpleFunctionCheckers + checker
//             }
//         }
// }